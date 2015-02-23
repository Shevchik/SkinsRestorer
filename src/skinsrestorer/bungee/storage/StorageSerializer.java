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
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.storage.IStorageSerializer;

public class StorageSerializer implements IStorageSerializer {

	@Override
	public LinkedHashMap<String, SkinProfile> loadData() {
		LinkedHashMap<String, SkinProfile> profiles = new LinkedHashMap<String, SkinProfile>();
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), "data.yml");
		try {
			Configuration configuration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(datafile);
			for (String name : configuration.getKeys()) {
				long creationDate = configuration.getLong(name+".timestamp");
				String propertyname = configuration.getString(name+".propertyname");
				String propertyvalue = configuration.getString(name+".propertyvalue");
				String propertysignature = configuration.getString(name+".propertysignature");
				if (propertyname == null || propertyvalue == null || propertysignature == null) {
					continue;
				}
				boolean isForced = configuration.getBoolean(name+".forced", false);
				SkinProfile skinData = new SkinProfile(new SkinProperty(propertyname, propertyvalue, propertysignature), creationDate, isForced);
				profiles.put(name, skinData);
			}
		} catch (IOException e) {
		}
		return profiles;
	}

	@Override
	public void saveData(LinkedHashMap<String, SkinProfile> data) {
		long saved = 0;
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), "data.yml");
		Configuration configuration = new Configuration();
		for (Entry<String, SkinProfile> entry : data.entrySet()) {
			configuration.set(entry.getKey()+".timestamp", entry.getValue().getCreationDate());
			configuration.set(entry.getKey()+".propertyname", entry.getValue().getPlayerSkinProperty().getName());
			configuration.set(entry.getKey()+".propertyvalue", entry.getValue().getPlayerSkinProperty().getValue());
			configuration.set(entry.getKey()+".propertysignature", entry.getValue().getPlayerSkinProperty().getSignature());
			configuration.set(entry.getKey()+".forced", entry.getValue().isForced());
			saved++;
			if (saved >= IStorageSerializer.MAX_STORAGE_SIZE) {
				break;
			}
		}
		try {
			datafile.getParentFile().mkdirs();
			datafile.createNewFile();
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(configuration, datafile);
		} catch (IOException e) {
		}
	}

}
