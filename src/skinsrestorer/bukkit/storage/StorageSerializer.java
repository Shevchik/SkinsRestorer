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

package skinsrestorer.bukkit.storage;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.format.Profile;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.storage.IStorageSerializer;

public class StorageSerializer implements IStorageSerializer {

	@Override
	public LinkedHashMap<String, SkinProfile> loadData() {
		LinkedHashMap<String, SkinProfile> profiles = new LinkedHashMap<String, SkinProfile>();
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), IStorageSerializer.STORAGE_FILE_NAME);
		YamlConfiguration data = YamlConfiguration.loadConfiguration(datafile);
		ConfigurationSection cs = data.getConfigurationSection("");
		if (cs == null) {
			return profiles;
		}
		for (String name : cs.getKeys(false)) {
			String username = cs.getString(name+".username");
			String uuid = cs.getString(name+".uuid");
			long creationDate = cs.getLong(name+".timestamp");
			String propertyname = cs.getString(name+".propertyname");
			String propertyvalue = cs.getString(name+".propertyvalue");
			String propertysignature = cs.getString(name+".propertysignature");
			boolean isForced = cs.getBoolean(name+".forced", false);
			SkinProfile skinData = new SkinProfile(new Profile(uuid.replace("-", ""), username), new SkinProperty(propertyname, propertyvalue, propertysignature), creationDate);
			if (isForced) {
				skinData.setForced();
			}
			profiles.put(name, skinData);
		}
		return profiles;
	}

	@Override
	public void saveData(LinkedHashMap<String, SkinProfile> data) {
		long saved = 0;
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), IStorageSerializer.STORAGE_FILE_NAME);
		YamlConfiguration config = new YamlConfiguration();
		for (Entry<String, SkinProfile> entry : data.entrySet()) {
			config.set(entry.getKey()+".username", entry.getValue().getName());
			config.set(entry.getKey()+".uuid", entry.getValue().getUUID().toString());
			config.set(entry.getKey()+".timestamp", entry.getValue().getCreationDate());
			config.set(entry.getKey()+".propertyname", entry.getValue().getPlayerSkinProperty().getName());
			config.set(entry.getKey()+".propertyvalue", entry.getValue().getPlayerSkinProperty().getValue());
			config.set(entry.getKey()+".propertysignature", entry.getValue().getPlayerSkinProperty().getSignature());
			config.set(entry.getKey()+".forced", entry.getValue().isForced());
			saved++;
			if (saved >= IStorageSerializer.MAX_STORAGE_SIZE) {
				break;
			}
		}
		try {
			config.save(datafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
