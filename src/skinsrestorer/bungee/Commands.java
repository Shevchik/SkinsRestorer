package skinsrestorer.bungee;

import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.utils.SkinFetchUtils;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

public class Commands extends Command {

	public Commands() {
		super("skinsrestorer", "skinsrestorer.cmds", new String[] {"sr"});
	}

	@Override
	public void execute(final CommandSender sender, String[] args) {
		if ((args.length == 2) && args[0].equalsIgnoreCase("drop")) {
			SkinsRestorer.getInstance().getSkinStorage().removeSkinData(args[1]);
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
							SkinProfile profile = SkinFetchUtils.fetchSkinProfile(name);
							SkinsRestorer.getInstance().getSkinStorage().addSkinData(name, profile);
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
