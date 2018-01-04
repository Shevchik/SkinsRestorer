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

package skinsrestorer.bungee;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import skinsrestorer.bungee.commands.AdminCommands;
import skinsrestorer.bungee.commands.PlayerCommands;
import skinsrestorer.bungee.listeners.LoginListener;
import skinsrestorer.bungee.listeners.ProtocolSupportListener;
import skinsrestorer.shared.storage.CooldownStorage;
import skinsrestorer.shared.storage.LocaleStorage;
import skinsrestorer.shared.storage.SkinStorage;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;

public class SkinsRestorer extends Plugin {

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
		PluginManager pm = getProxy().getPluginManager();
		if (pm.getPlugin("ProtocolSupportBungee") != null) {
			pm.registerListener(this, new ProtocolSupportListener());
		} else {
			pm.registerListener(this, new LoginListener());
		}
		pm.registerCommand(this, new AdminCommands());
		pm.registerCommand(this, new PlayerCommands());
		getProxy().getScheduler().schedule(this, CooldownStorage.cleanupCooldowns, 0, 1, TimeUnit.MINUTES);
	}

	@Override
	public void onDisable() {
		SkinStorage.getInstance().saveData();
		instance = null;
	}

}
