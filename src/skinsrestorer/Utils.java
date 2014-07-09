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
	
	public static String getUUIDString(String id) {
		StringBuilder build = new StringBuilder();
		char[] chars = id.toCharArray();
		int i = 0;
		for (; i < 8; i++) {
			build.append(chars[i]);
		}
		build.append("-");
		for (int c = 0; c < 3; c++) {
			for (int j = 0; j < 4; j++) {
				i++;
				build.append(chars[i]);
			}
			build.append("-");
		}
		for (; i < chars.length; i++) {
			build.append(chars[i]);
		}
		return build.toString();
	}

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
			URL url = new URL(skullbloburl+id);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(7000);
			InputStream is = connection.getInputStream();
			String result = IOUtils.toString(is, Charsets.UTF_8);
			is.close();
			JSONArray properties = (JSONArray) ((JSONObject) new JSONParser().parse(result)).get("properties");
            for (int i = 0; i < properties.size(); i++) {
                JSONObject property = (JSONObject) properties.get(i);
                String name = (String) property.get("name");
                String value = (String) property.get("value");
                if (name.equals("textures")) {
                	return new Property(value, name);
                }
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
