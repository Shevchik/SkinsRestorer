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

package skinsrestorer.shared.storage;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import skinsrestorer.shared.format.SkinProfile;

public class SkinStorage {

	private IStorageSerializer serializer;
	public SkinStorage(IStorageSerializer serializer) {
		this.serializer = serializer;
	}

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
		SkinProfile profile = skins.get(name.toLowerCase()); 
		return profile != null ? profile : SkinProfile.NONE;
	}

	public Map<String, SkinProfile> getSkinData() {
		return Collections.unmodifiableMap(skins);
	}


	public void loadData() {
		skins.putAll(serializer.loadData());
	}

	public void saveData() {
		serializer.saveData(skins);
	}

}
