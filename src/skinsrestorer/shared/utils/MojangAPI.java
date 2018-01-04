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

package skinsrestorer.shared.utils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import skinsrestorer.libs.com.google.gson.Gson;
import skinsrestorer.libs.com.google.gson.JsonArray;
import skinsrestorer.libs.com.google.gson.JsonObject;
import skinsrestorer.libs.com.google.gson.JsonPrimitive;
import skinsrestorer.libs.com.google.gson.JsonSyntaxException;
import skinsrestorer.shared.format.Profile;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class MojangAPI {

	private static final Gson gson = new Gson();

	private static final String profileurl = "https://api.mojang.com/profiles/minecraft";
	public static Profile getProfile(String nick) throws SkinFetchFailedException, IOException, JsonSyntaxException {
		//open connection
		HttpURLConnection connection = (HttpURLConnection) setupConnection(new URL(profileurl));
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		//write body
		try (DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
			JsonArray names = new JsonArray();
			names.add(new JsonPrimitive(nick));
			writer.write(gson.toJson(names).getBytes(StandardCharsets.UTF_8));
		}
		//check response code
		if (connection.getResponseCode() == 429) {
			throw new SkinFetchFailedException(SkinFetchFailedException.Reason.RATE_LIMITED);
		}
		//read response
		try (InputStream is = connection.getInputStream()) {
			String result = readStreamToString(is);
			JsonArray jsonProfiles = gson.fromJson(result, JsonArray.class);
			if (jsonProfiles.size() > 0) {
				JsonObject jsonProfile = (JsonObject) jsonProfiles.get(0).getAsJsonObject();
				return new Profile(jsonProfile.get("id").getAsString(), jsonProfile.get("name").getAsString());
			}
			throw new SkinFetchFailedException(SkinFetchFailedException.Reason.NO_PREMIUM_PLAYER);
		}
	}

	private static final String skullbloburl = "https://sessionserver.mojang.com/session/minecraft/profile/";
	public static SkinProfile getSkinProfile(String id) throws IOException, JsonSyntaxException, SkinFetchFailedException {
		//open connection
		HttpURLConnection connection =  (HttpURLConnection) setupConnection(new URL(skullbloburl+id.replace("-", "")+"?unsigned=false"));
		//check response code
		if (connection.getResponseCode() == 429) {
			throw new SkinFetchFailedException(SkinFetchFailedException.Reason.RATE_LIMITED);
		}
		//read response
		try (InputStream is = connection.getInputStream()) {
			String result = readStreamToString(is);
			JsonObject obj = gson.fromJson(result, JsonObject.class);
			String username = obj.get("name").getAsString();
			JsonArray properties = obj.get("properties").getAsJsonArray();
			for (int i = 0; i < properties.size(); i++) {
				JsonObject property = properties.get(i).getAsJsonObject();
				String name = property.get("name").getAsString();
				String value = property.get("value").getAsString();
				String signature = property.get("signature").getAsString();
				if (name.equals("textures")) {
					return new SkinProfile(new Profile(id, username), new SkinProperty(name, value, signature), System.currentTimeMillis(), false);
				}
			}
			throw new SkinFetchFailedException(SkinFetchFailedException.Reason.NO_SKIN_DATA);
		}
	}

	private static URLConnection setupConnection(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		connection.setConnectTimeout(10000);
		connection.setReadTimeout(10000);
		connection.setUseCaches(false);
		connection.setDoInput(true);
		connection.setDoOutput(true);
		return connection;
	}

	private static String readStreamToString(InputStream is) {
		return new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
	}

}
