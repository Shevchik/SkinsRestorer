package skinsrestorer;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.org.apache.commons.io.IOUtils;

import com.google.common.base.Charsets;
import com.mojang.api.profiles.HttpProfileRepository;
import com.mojang.api.profiles.Profile;

public class Utils {

	public static Profile getProfile(String nick) {
		HttpProfileRepository repo = new HttpProfileRepository("minecraft");
		Profile[] profiles = repo.findProfilesByNames(nick);
		if (profiles.length == 1) {
			return profiles[0];
		}
		return null;
	}

	private static String skullbloburl = "https://sessionserver.mojang.com/session/minecraft/profile/";
	public static Property getProp(String id) {
		try {
			URL url = new URL(skullbloburl+id+"?unsigned=false");
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);
			connection.setUseCaches(false);
			InputStream is = connection.getInputStream();
			String result = IOUtils.toString(is, Charsets.UTF_8);
			System.out.println(result);
			IOUtils.closeQuietly(is);
			JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(result)).get("properties");
			for (int i = 0; i < properties.size(); i++) {
				JSONObject property = (JSONObject) properties.get(i);
				String name = (String) property.get("name");
				String value = (String) property.get("value");
				String signature = (String) property.get("signature");
				if (name.equals("textures")) {
				return new Property(name, value, signature);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
