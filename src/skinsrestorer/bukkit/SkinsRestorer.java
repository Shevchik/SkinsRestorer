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

package skinsrestorer.bukkit;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import skinsrestorer.bukkit.commands.AdminCommands;
import skinsrestorer.bukkit.commands.PlayerCommands;
import skinsrestorer.bukkit.listeners.LoginListener;
import skinsrestorer.bukkit.listeners.ProtocolSupportListener;
import skinsrestorer.shared.storage.CooldownStorage;
import skinsrestorer.shared.storage.LocaleStorage;
import skinsrestorer.shared.storage.SkinStorage;

public class SkinsRestorer extends JavaPlugin implements Listener {

	public static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	private static SkinsRestorer instance;
	public static SkinsRestorer getInstance() {
		return instance;
	}

	private Logger log;
	public void logInfo(String message) {
		log.info(message);
	}

	@Override
	public void onEnable() {
		instance = this;
		log = getLogger();
		LocaleStorage.init(getDataFolder());
		SkinStorage.init(getDataFolder());
		getCommand("skinsrestorer").setExecutor(new AdminCommands());
		getCommand("skin").setExecutor(new PlayerCommands());
		PluginManager pm = getServer().getPluginManager();
		if (pm.isPluginEnabled("ProtocolSupport")) {
			pm.registerEvents(new ProtocolSupportListener(), this);
		} else if (pm.isPluginEnabled("ProtocolLib")) {
			pm.registerEvents(new LoginListener(), this);
		} else {
			throw new RuntimeException("You need either ProtocolSupport or ProtocolLib for this plugin to function");
		}
		executor.scheduleWithFixedDelay(CooldownStorage.cleanupCooldowns, 0, 1, TimeUnit.MINUTES);
	}

	@Override
	public void onDisable() {
		SkinStorage.getInstance().saveData();
		executor.shutdown();
		instance = null;
	}

}
