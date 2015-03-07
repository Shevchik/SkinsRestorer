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

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import skinsrestorer.libs.com.google.gson.Gson;
import skinsrestorer.libs.com.google.gson.GsonBuilder;
import skinsrestorer.libs.com.google.gson.JsonIOException;
import skinsrestorer.libs.com.google.gson.reflect.TypeToken;
import skinsrestorer.shared.format.SkinProfile;

public class SkinStorage {

	private static final String cachefile = "cache.json";
	private static final Gson gson =
		new GsonBuilder()
		.registerTypeHierarchyAdapter(SkinProfile.class, new SkinProfile.GsonTypeAdapter())
		.setPrettyPrinting()
		.create();
	private static final Type type = new TypeToken<ConcurrentHashMap<String, SkinProfile>>(){}.getType();

	private File pluginfolder;

	public SkinStorage(File pluginfolder) {
		this.pluginfolder = pluginfolder;
	}

	private ConcurrentHashMap<String, SkinProfile> skins = new ConcurrentHashMap<String, SkinProfile>();

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
		try {
			Map<String, SkinProfile> gsondata = gson.fromJson(new FileReader(new File(pluginfolder, cachefile)), type);
			if (gsondata != null) {
				skins.putAll(gsondata);
			}
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}

	public void saveData() {
		try (FileWriter writer = new FileWriter(new File(pluginfolder, cachefile))) {
			ConcurrentHashMap<String, SkinProfile> serialize = new ConcurrentHashMap<String, SkinProfile>();
			for (Entry<String, SkinProfile> entry : skins.entrySet()) {
				if (System.currentTimeMillis() - entry.getValue().getCreationDate() < 30 * 24 * 60 * 60 * 1000) {
					serialize.put(entry.getKey(), entry.getValue());
				}
			}
			writer.write(gson.toJson(serialize, type));
		} catch (JsonIOException | IOException e) {
			e.printStackTrace();
		}
	}

}
