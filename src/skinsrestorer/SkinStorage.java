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

import java.io.File;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.properties.Property;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class SkinStorage {

	private SkinsRestorer plugin;
	public SkinStorage(SkinsRestorer plugin) {
		this.plugin = plugin;
	}

	//map to hold skin data
	private HashMap<String, SkinProfile> skins = new HashMap<String, SkinProfile>(50000);

	public boolean hasLoadedSkinData(String name) {
		return skins.containsKey(name.toLowerCase());
	}

	public void addSkinData(String name, SkinProfile data) {
		skins.put(name.toLowerCase(), data);
	}

	public void removeSkinData(String name) {
		skins.remove(name.toLowerCase());
	}

	public SkinProfile getLoadedSkinData(String name) {
		return skins.get(name.toLowerCase());
	}

	public Map<String, SkinProfile> getSkinData() {
		return Collections.unmodifiableMap(skins);
	}

	private long maxHeldSkinDataNumber = 200000;
	private long maxDaysBeforeExpire = 30;

	public void loadData() {
		int loadedSkins = 0;
		File datafile = new File(plugin.getDataFolder(), "data.yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(datafile);
		ConfigurationSection cs = data.getConfigurationSection("");
		if (cs == null) {
			return;
		}
		for (String name : cs.getKeys(false)) {
			if (loadedSkins >= maxHeldSkinDataNumber) {
				return;
			}
			long creationDate = cs.getLong(name+".timestamp");
			if (System.currentTimeMillis() - creationDate > maxDaysBeforeExpire * 24 * 60 * 60 * 1000) {
				return;
			}
			String uuid = cs.getString(name+".uuid");
			String propertyname = cs.getString(name+".propertyname");
			String propertyvalue = cs.getString(name+".propertyvalue");
			String propertysignature = cs.getString(name+".propertysignature");
			try {
				SkinProfile skinData = new SkinProfile(UUID.fromString(uuid), new Property(propertyname, propertyvalue, propertysignature), creationDate);
				addSkinData(name, skinData);
				loadedSkins++;
			} catch (Exception e) {
			}
		}
	}

	public void saveData() {
		File datafile = new File(plugin.getDataFolder(), "data.yml");
		FileConfiguration data = new YamlConfiguration();
		for (Entry<String, SkinProfile> entry : getSkinData().entrySet()) {
			data.set(entry.getKey()+".uuid", entry.getValue().getUUID().toString());
			data.set(entry.getKey()+".timestamp", entry.getValue().getCreationDate());
			data.set(entry.getKey()+".propertyname", entry.getValue().getPlayerSkinData().getName());
			data.set(entry.getKey()+".propertyvalue", entry.getValue().getPlayerSkinData().getValue());
			data.set(entry.getKey()+".propertysignature", entry.getValue().getPlayerSkinData().getSignature());
		}
		try {
			data.save(datafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
