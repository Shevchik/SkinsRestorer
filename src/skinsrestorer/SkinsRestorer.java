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

import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import skinsrestorer.listeners.IListener;
import skinsrestorer.listeners.LoginListener;
import skinsrestorer.listeners.v_1_7.Version_1_7_Listener;
import skinsrestorer.listeners.v_s_1_8.Version_Spigot_1_8_Listener;
import skinsrestorer.listeners.v_s_p_1_7_1_8.Version_Spigot_Protocol_1_7_1_8_Listener;
import skinsrestorer.storage.SkinStorage;

public class SkinsRestorer extends JavaPlugin implements Listener {

	private static SkinsRestorer instance;
	public static SkinsRestorer getInstance() {
		return instance;
	}

	private Logger log;

	public void logInfo(String message) {
		log.info(message);
	}

	private SkinStorage storage;
	public SkinStorage getSkinStorage() {
		return storage;
	}

	private Config configuration;
	public Config getConfiguration() {
		return configuration;
	}

	@Override
	public void onEnable() {
		instance = this;
		log = getLogger();
		storage = new SkinStorage();
		storage.loadData();
		configuration = new Config();
		configuration.loadConfig();
		getCommand("skinsrestorer").setExecutor(new Commands());
		LoginListener loginlistener = new LoginListener();
		getServer().getPluginManager().registerEvents(loginlistener, this);
		startListeners();
	}

	@Override
	public void onDisable() {
		storage.saveData();
		instance = null;
	}

	private IListener versionedListener;
	public void startListeners() {
		if (versionedListener != null) {
			versionedListener.unregister();
		}
		switch (configuration.getServerVersion()) {
			case VERSION_1_7: {
				versionedListener = new Version_1_7_Listener();
				break;
			}
			case VERSION_SPIGOT_PROTOCOL: {
				versionedListener = new Version_Spigot_Protocol_1_7_1_8_Listener();
				break;
			}
			case VERSION_SPIGOT_1_8: {
				versionedListener = new Version_Spigot_1_8_Listener();
				break;
			}
		}
		versionedListener.register();
	}

}
