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

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.utils.SkinFetchUtils;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class PlayerCommands extends Command {

	public PlayerCommands() {
		super("skin", "skinsrestorer.playercmds");
	}

	@Override
	public void execute(CommandSender sender, final String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			TextComponent component = new TextComponent("This commands are only for players");
			sender.sendMessage(component);
		}
		final ProxiedPlayer player = (ProxiedPlayer) sender;
		if ((args.length == 1) && args[0].equalsIgnoreCase("clear")) {
			SkinsRestorer.getInstance().getSkinStorage().removeSkinData(player.getName());
			TextComponent component = new TextComponent("Your skin data cleared");
			component.setColor(ChatColor.BLUE);
			player.sendMessage(component);
		} else
		if ((args.length == 2) && args[0].equalsIgnoreCase("set")) {
			ProxyServer.getInstance().getScheduler().runAsync(
				SkinsRestorer.getInstance(),
				new Runnable() {
					@Override
					public void run() {
						String from = args[1];
						try {
							SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(from);
							if (skinprofile.isValid() && !skinprofile.isForced()) {
								skinprofile =  skinprofile.cloneAsForced();
							} else {
								skinprofile = SkinFetchUtils.fetchSkinProfile(from, skinprofile.getUUID());
							}
							SkinsRestorer.getInstance().getSkinStorage().addSkinData(player.getName(), skinprofile);
							TextComponent component = new TextComponent("Your skin has been updated, relog to see changes");
							component.setColor(ChatColor.BLUE);
							player.sendMessage(component);
						} catch (SkinFetchFailedException e) {
							TextComponent component = new TextComponent("Skin fetch failed: "+e.getMessage());
							component.setColor(ChatColor.RED);
							player.sendMessage(component);
						}
					}
				}
			);
		}
	}

}
