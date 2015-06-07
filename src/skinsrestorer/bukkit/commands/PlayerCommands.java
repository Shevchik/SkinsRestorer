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

package skinsrestorer.bukkit.commands;

import java.util.concurrent.TimeUnit;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.storage.CooldownStorage;
import skinsrestorer.shared.storage.LocaleStorage;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.SkinFetchUtils;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class PlayerCommands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, final String[] args) {
		if (!sender.hasPermission("skinsrestorer.playercmds")) {
			sender.sendMessage("You don't have permission to do this");
			return true;
		}
		if (!(sender instanceof Player)) {
			sender.sendMessage("This commands are only for players");
			return true;
		}
		final Player player = (Player) sender;
		if ((args.length == 1) && args[0].equalsIgnoreCase("clear")) {
			if (SkinStorage.getInstance().isSkinDataForced(player.getName())) {
				SkinStorage.getInstance().removeSkinData(player.getName());
				player.sendMessage(ChatColor.BLUE+LocaleStorage.getInstance().PLAYER_SKIN_CHANGE_SKIN_DATA_CLEARED);
			}
			return true;
		} else
		if ((args.length == 2) && args[0].equalsIgnoreCase("set")) {
			if (CooldownStorage.getInstance().isAtCooldown(player.getUniqueId())) {
				player.sendMessage(ChatColor.RED+LocaleStorage.getInstance().PLAYER_SKIN_CHANGE_COOLDOWN);
				return true;
			}
			CooldownStorage.getInstance().setCooldown(player.getUniqueId(), 10, TimeUnit.MINUTES);
			SkinsRestorer.executor.execute(
				new Runnable() {
					@Override
					public void run() {
						String from = args[1];
						try {
							SkinProfile skinprofile = SkinFetchUtils.fetchSkinProfile(from, null);
							SkinStorage.getInstance().setSkinData(player.getName(), skinprofile);
							player.sendMessage(ChatColor.BLUE+LocaleStorage.getInstance().PLAYER_SKIN_CHANGE_SUCCESS);
						} catch (SkinFetchFailedException e) {
							player.sendMessage(ChatColor.RED+LocaleStorage.getInstance().PLAYER_SKIN_CHANGE_FAILED+e.getMessage());
							CooldownStorage.getInstance().resetCooldown(player.getUniqueId());
						}
					}
				}
			);
			return true;
		}
		return false;
	}

}
