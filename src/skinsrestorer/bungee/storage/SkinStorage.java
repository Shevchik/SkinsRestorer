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

package skinsrestorer.bungee.storage;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;

public class SkinStorage {

	private LinkedHashMap<String, SkinProfile> skins = new LinkedHashMap<String, SkinProfile>(150, 0.75F, true);

	public boolean hasLoadedSkinData(String name) {
		return skins.containsKey(name.toLowerCase());
	}

	public void addSkinData(String name, SkinProfile data) {
		skins.put(name.toLowerCase(), data);
	}

	public void removeSkinData(String name) {
		skins.remove(name.toLowerCase());
	}

	public SkinProfile getLoadedSkinData(String name) {
		return skins.get(name.toLowerCase());
	}

	public Map<String, SkinProfile> getSkinData() {
		return Collections.unmodifiableMap(skins);
	}

	private final long maxHeldSkinDataNumber = 10000;

	public void loadData() {
		int loadedSkins = 0;
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), "data.yml");
		if (datafile.exists()) {
			try {
				Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(datafile);
				for (String name : configuration.getKeys()) {
					if (loadedSkins >= maxHeldSkinDataNumber) {
						return;
					}
					long creationDate = configuration.getLong(name+".timestamp");
					String propertyname = configuration.getString(name+".propertyname");
					String propertyvalue = configuration.getString(name+".propertyvalue");
					String propertysignature = configuration.getString(name+".propertysignature");
					SkinProfile skinData = new SkinProfile(new SkinProperty(propertyname, propertyvalue, propertysignature), creationDate);
					addSkinData(name, skinData);
					loadedSkins++;
				}
			} catch (IOException e) {
			}
		}
	}

	public void saveData() {
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), "data.yml");
		Configuration configuration = new Configuration();
		for (Entry<String, SkinProfile> entry : getSkinData().entrySet()) {
			configuration.set(entry.getKey()+".timestamp", entry.getValue().getCreationDate());
			configuration.set(entry.getKey()+".propertyname", entry.getValue().getPlayerSkinProperty().getName());
			configuration.set(entry.getKey()+".propertyvalue", entry.getValue().getPlayerSkinProperty().getValue());
			configuration.set(entry.getKey()+".propertysignature", entry.getValue().getPlayerSkinProperty().getSignature());
		}
		try {
			datafile.getParentFile().mkdirs();
			datafile.createNewFile();
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, datafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
