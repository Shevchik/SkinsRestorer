package skinsrestorer;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class SkinsRestorer extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		SkinListeners listener = new SkinListeners(this);
		getServer().getPluginManager().registerEvents(listener, this);
		listener.registerPlayerSkinListener();
	}

}
