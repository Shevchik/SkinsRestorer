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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	private ServerVersion serverversion = ServerVersion.VERSION_SPIGOT_1_8;

	private int skinCacheTime = 24 * 60 * 60;

	public int getSkinCacheTime() {
		return skinCacheTime;
	}

	public ServerVersion getServerVersion() {
		return serverversion;
	}

	private ArrayList<String> allPossibleVersions = new ArrayList<String>();
	{
		for (ServerVersion version : ServerVersion.values()) {
			allPossibleVersions.add(version.toString());
		}
	}

	public void loadConfig() {
		File conffile = new File(SkinsRestorer.getInstance().getDataFolder(), "config.yml");
		FileConfiguration conf = YamlConfiguration.loadConfiguration(conffile);
		skinCacheTime = conf.getInt("skin_cache_time", skinCacheTime);
		try {
			serverversion = ServerVersion.valueOf(conf.getString("server_version", serverversion.toString()));
		} catch (Throwable t) {
			serverversion = ServerVersion.VERSION_SPIGOT_1_8;
		}
		conf = new YamlConfiguration();
		conf.set("skin_cache_time", skinCacheTime);
		conf.set("server_version", serverversion.toString());
		conf.set("all_possible_versions", allPossibleVersions);
		try {
			conf.save(conffile);
		} catch (IOException e) {
		}
	}

	public static enum ServerVersion {
		VERSION_1_7, VERSION_SPIGOT_PROTOCOL, VERSION_SPIGOT_1_8
	}

}
