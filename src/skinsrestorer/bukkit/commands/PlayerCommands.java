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

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
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
		}
		final Player player = (Player) sender;
		if ((args.length == 1) && args[0].equalsIgnoreCase("clear")) {
			SkinsRestorer.getInstance().getSkinStorage().removeSkinData(player.getName());
			player.sendMessage(ChatColor.BLUE+"Your skin data cleared");
			return true;
		} else
		if ((args.length == 2) && args[0].equalsIgnoreCase("set")) {
			SkinsRestorer.executor.execute(
				new Runnable() {
					@Override
					public void run() {
						String from = args[1];
						try {
							SkinProfile skinprofile = SkinFetchUtils.fetchSkinProfile(from);
							skinprofile.setForced();
							SkinsRestorer.getInstance().getSkinStorage().addSkinData(player.getName(), skinprofile);
							player.sendMessage(ChatColor.BLUE+"Your skin has been updated, relog to see changes");
						} catch (SkinFetchFailedException e) {
							player.sendMessage(ChatColor.RED+"Skin fetch failed: "+e.getMessage());
						}
					}
				}
			);
			return true;
		}
		return false;
	}

}
