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

import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class AdminCommands implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, Command cmd, String label, final String[] args) {
		if (!sender.hasPermission("skinsrestorer.cmds")) {
			sender.sendMessage("You don't have permission to do this");
			return true;
		}
		if ((args.length == 2) && args[0].equalsIgnoreCase("drop")) {
			SkinStorage.getInstance().removeSkinData(args[1]);
			sender.sendMessage(ChatColor.BLUE+"Skin data for player "+args[1]+" dropped");
			return true;
		} else
		if ((args.length == 2) && args[0].equalsIgnoreCase("update")) {
			SkinsRestorer.executor.execute(
				new Runnable() {
					@Override
					public void run() {
						String name = args[1];
						try {
							SkinStorage.getInstance().getOrCreateSkinData(name).attemptUpdate();
							sender.sendMessage(ChatColor.BLUE+"Skin data updated");
						} catch (SkinFetchFailedException e) {
							sender.sendMessage(ChatColor.RED+"Skin fetch failed: "+e.getMessage());
						}
					}
				}
			);
			return true;
		}
		return false;
	}

}
