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

package skinsrestorer.bungee.commands;

import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class AdminCommands extends Command {

	public AdminCommands() {
		super("skinsrestorer", "skinsrestorer.cmds", new String[] {"sr"});
	}

	@Override
	public void execute(final CommandSender sender, String[] args) {
		if ((args.length == 2) && args[0].equalsIgnoreCase("drop")) {
			SkinStorage.getInstance().removeSkinData(args[1]);
			TextComponent component = new TextComponent("Skin data for player "+args[1]+" dropped");
			component.setColor(ChatColor.BLUE);
			sender.sendMessage(component);
		} else
		if ((args.length == 2) && args[0].equalsIgnoreCase("update")) {
			final String name = args[1];
			ProxyServer.getInstance().getScheduler().runAsync(
				SkinsRestorer.getInstance(),
				new Runnable() {
					@Override
					public void run() {
						try {
							SkinStorage.getInstance().getOrCreateSkinData(name).attemptUpdate();
							TextComponent component = new TextComponent("Skin data updated");
							component.setColor(ChatColor.BLUE);
							sender.sendMessage(component);
						} catch (SkinFetchFailedException e) {
							TextComponent component = new TextComponent("Skin fetch failed: "+e.getMessage());
							component.setColor(ChatColor.RED);
							sender.sendMessage(component);
						}
					}
				}
			);
		}
	}

}
