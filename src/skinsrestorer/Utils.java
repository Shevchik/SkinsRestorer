package skinsrestorer;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import net.minecraft.util.com.google.gson.Gson;
import net.minecraft.util.org.apache.commons.io.IOUtils;
import skinsrestorer.PropResult.Prop;

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
	public static Prop getProp(String id) {
		try {
			URL url = new URL(skullbloburl+id);
			URLConnection connection = url.openConnection();
			connection.setConnectTimeout(7000);
			InputStream is = connection.getInputStream();
			String result = IOUtils.toString(is, Charsets.UTF_8);
			Gson gson = new Gson();
			PropResult propr = gson.fromJson(result, PropResult.class);
			is.close();
			if (!propr.properties.isEmpty()) {
				return propr.properties.get(0);
			}
		} catch (Exception e) {
		}
		return null;
	}

}
