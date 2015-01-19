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

import java.util.logging.Logger;

import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import skinsrestorer.bukkit.listeners.LoginListener;
import skinsrestorer.bukkit.storage.SkinStorage;

public class SkinsRestorer extends JavaPlugin implements Listener {

	private static SkinsRestorer instance;
	public static SkinsRestorer getInstance() {
		return instance;
	}

	private Logger log;
	public void logInfo(String message) {
		log.info(message);
	}

	private SkinStorage storage = new SkinStorage();
	public SkinStorage getSkinStorage() {
		return storage;
	}

	@Override
	public void onEnable() {
		instance = this;
		log = getLogger();
		storage.loadData();
		getCommand("skinsrestorer").setExecutor(new Commands());
		LoginListener loginlistener = new LoginListener();
		getServer().getPluginManager().registerEvents(loginlistener, this);
	}

	@Override
	public void onDisable() {
		storage.saveData();
		instance = null;
	}

}
