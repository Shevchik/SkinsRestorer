/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package skinsrestorer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.json.simple.parser.ParseException;

import com.mojang.api.profiles.Profile;

public class NPCScan {

	private SkinsRestorer plugin;
	public NPCScan(SkinsRestorer plugin) {
		this.plugin = plugin;
	}

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	private String srnoskinmetadata = "SkinsRestorerNPCNoSkin";

	public void startScan() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(
			plugin,
			new Runnable() {
				@Override
				public void run() {
					HashSet<Player> onlinePlayers = new HashSet<Player>(Arrays.asList(Bukkit.getOnlinePlayers()));
					for (World world : Bukkit.getWorlds()) {
						for (final Player player : world.getPlayers()) {
							if (!onlinePlayers.contains(player) && !plugin.getSkinStorage().hasLoadedSkinData(player.getName()) && !player.hasMetadata(srnoskinmetadata)) {
								executor.execute(
									new Runnable() {
										@Override
										public void run() {
											SkinProfile data = getSkinData(player.getName()); 
											if (data == null) {
												player.setMetadata(srnoskinmetadata, new FixedMetadataValue(plugin, new Object()));
											} else {
												plugin.getSkinStorage().addSkinData(player.getName(), data);
											}
										}
									}
								);
							}
						}
					}
				}
			},
			1, 1000
		);
	}

	private SkinProfile getSkinData(String name) {
		Profile prof = DataUtils.getProfile(name);
		if (prof == null) {
			return null;
		}
		Property prop = DataUtils.getProp(prof.getId());
		if (prop == null) {
			return null;
		}
		try {
			SkinProfile skinprofile = new SkinProfile(UUIDTypeAdapter.fromString(prof.getId()), prop);
			return skinprofile;
		} catch (ParseException e) {
		}
		return null;
	}

}
