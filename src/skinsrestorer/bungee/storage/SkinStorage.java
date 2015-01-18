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

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import skinsrestorer.shared.format.SkinProfile;

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

	/*public void loadData() {
		int loadedSkins = 0;
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), "data.yml");
		FileConfiguration data = YamlConfiguration.loadConfiguration(datafile);
		ConfigurationSection cs = data.getConfigurationSection("");
		if (cs == null) {
			return;
		}
		for (String name : cs.getKeys(false)) {
			if (loadedSkins >= maxHeldSkinDataNumber) {
				return;
			}
			long creationDate = cs.getLong(name+".timestamp");
			String propertyname = cs.getString(name+".propertyname");
			String propertyvalue = cs.getString(name+".propertyvalue");
			String propertysignature = cs.getString(name+".propertysignature");
			try {
				SkinProfile skinData = new SkinProfile(new SkinProperty(propertyname, propertyvalue, propertysignature), creationDate);
				addSkinData(name, skinData);
				loadedSkins++;
			} catch (Exception e) {
			}
		}
	}

	public void saveData() {
		File datafile = new File(SkinsRestorer.getInstance().getDataFolder(), "data.yml");
		FileConfiguration data = new YamlConfiguration();
		for (Entry<String, SkinProfile> entry : getSkinData().entrySet()) {
			data.set(entry.getKey()+".timestamp", entry.getValue().getCreationDate());
			data.set(entry.getKey()+".propertyname", entry.getValue().getPlayerSkinProperty().getName());
			data.set(entry.getKey()+".propertyvalue", entry.getValue().getPlayerSkinProperty().getValue());
			data.set(entry.getKey()+".propertysignature", entry.getValue().getPlayerSkinProperty().getSignature());
		}
		try {
			data.save(datafile);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

}
