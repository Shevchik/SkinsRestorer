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

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.json.simple.parser.ParseException;

import com.mojang.api.profiles.Profile;

public class Commands implements CommandExecutor {

	private SkinsRestorer plugin;
	public Commands(SkinsRestorer plugin) {
		this.plugin = plugin;
	}

	private ExecutorService executor = Executors.newSingleThreadExecutor();

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, final String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage("Can be used only from console");
			return true;
		}
		if (!sender.hasPermission("skinsrestorer.head")) {
			sender.sendMessage("You don't have permission to do this");
			return true;
		}
		final Player player = (Player) sender;
		if (args.length == 2 && args[0].equalsIgnoreCase("head")) {
			player.sendMessage(ChatColor.BLUE + "Preparing head itemstack. Please wait.");
			executor.execute(
				new Runnable() {
					@Override
					public void run() {
						final ItemStack playerhead = new ItemStack(Material.SKULL_ITEM);
						playerhead.setDurability((short) 3);
						String name = args[1];
						SkinProfile skinprofile = plugin.getSkinStorage().getLoadedSkinData(name);
						try {
							SkullMeta meta = (SkullMeta) playerhead.getItemMeta();
							if (meta == null) {
								meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
							}
							if (skinprofile == null) {
								Profile prof = DataUtils.getProfile(name);
								if (prof == null) {
									throw new RuntimeException("Can't find a valid premium player with that name");
								}
								Property prop = DataUtils.getProp(prof.getId());
								if (prop == null) {
									throw new RuntimeException("No skin data found for player with that name");
								}
								skinprofile = new SkinProfile(UUIDTypeAdapter.fromString(prof.getId()), prop);
								plugin.getSkinStorage().addSkinData(name, skinprofile);
							}
							GameProfile newprofile = new GameProfile(skinprofile.getUUID(), name);
							newprofile.getProperties().clear();
							newprofile.getProperties().put(skinprofile.getHeadSkinData().getName(), skinprofile.getHeadSkinData());
							Field profileField = meta.getClass().getDeclaredField("profile");
							profileField.setAccessible(true);
							profileField.set(meta, newprofile);
							playerhead.setItemMeta(meta);
							plugin.getSkinStorage().addSkinData(name, skinprofile);
						} catch (Exception e) {
							player.sendMessage(ChatColor.RED + "Skin wasn't applied to head because of the error: "+e.getMessage());
						}
						Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, 
							new Runnable() {
								@Override
								public void run() {
									player.getInventory().addItem(playerhead);
									player.sendMessage(ChatColor.BLUE + "Head given");
								}
							}
						);
					}
				}
			);
			return true;
		} else
		if (args.length == 2 && args[0].equalsIgnoreCase("drop")) {
			plugin.getSkinStorage().removeSkinData(args[1]);
			player.sendMessage(ChatColor.BLUE+"Skin data for player "+args[1]+" dropped");
		} else
		if (args.length == 2 && args[0].equalsIgnoreCase("update")) {
			executor.execute(
				new Runnable() {
					@Override
					public void run() {
						String name = args[1];
						Profile prof = DataUtils.getProfile(name);
						if (prof == null) {
							player.sendMessage(ChatColor.RED+"Can't find a valid premium player with that name");
							return;
						}
						Property prop = DataUtils.getProp(prof.getId());
						if (prop == null) {
							player.sendMessage(ChatColor.RED+"No skin data found for player with that name");
							return;
						}
						try {
							SkinProfile skinprofile = new SkinProfile(UUIDTypeAdapter.fromString(prof.getId()), prop);
							plugin.getSkinStorage().addSkinData(name, skinprofile);
							player.sendMessage(ChatColor.BLUE+"Skin data for player "+name+" updated");
						} catch (ParseException e) {
							player.sendMessage(ChatColor.RED+"Failed to decode skin data for player with that name");
						}
					}
				}
			);
		}
		return false;
	}

}
