package skinsrestorer.libs.com.mojang.api.profiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import skinsrestorer.libs.com.mojang.api.http.BasicHttpClient;
import skinsrestorer.libs.com.mojang.api.http.HttpBody;
import skinsrestorer.libs.com.mojang.api.http.HttpHeader;
import skinsrestorer.libs.org.json.simple.JSONArray;
import skinsrestorer.libs.org.json.simple.JSONObject;
import skinsrestorer.libs.org.json.simple.parser.JSONParser;
import skinsrestorer.libs.org.json.simple.parser.ParseException;

public class HttpProfileRepository {

	private static final int PROFILES_PER_REQUEST = 100;

	private final String agent;
	private BasicHttpClient client;

	public HttpProfileRepository(String agent) {
		this(agent, BasicHttpClient.getInstance());
	}

	public HttpProfileRepository(String agent, BasicHttpClient client) {
		this.agent = agent;
		this.client = client;
	}

	public Profile[] findProfilesByNames(String... names) {
		List<Profile> profiles = new ArrayList<Profile>();
		try {

			List<HttpHeader> headers = new ArrayList<HttpHeader>();
			headers.add(new HttpHeader("Content-Type", "application/json"));

			int namesCount = names.length;
			int start = 0;
			int i = 0;
			do {
				int end = PROFILES_PER_REQUEST * (i + 1);
				if (end > namesCount) {
					end = namesCount;
				}
				String[] namesBatch = Arrays.copyOfRange(names, start, end);
				HttpBody body = getHttpBody(namesBatch);
				Profile[] result = post(getProfilesUrl(), body, headers);
				profiles.addAll(Arrays.asList(result));

				start = end;
				i++;
			} while (start < namesCount);
		} catch (Exception e) {
		}

		return profiles.toArray(new Profile[profiles.size()]);
	}

	private URL getProfilesUrl() throws MalformedURLException {
		return new URL("https://api.mojang.com/profiles/" + agent);
	}

	private Profile[] post(URL url, HttpBody body, List<HttpHeader> headers) throws IOException, ParseException {
		String response = client.post(url, body, headers);
		JSONArray jsonProfiles = (JSONArray) new JSONParser().parse(response);
		Profile[] profiles = new Profile[jsonProfiles.size()];
		for (int i = 0; i < jsonProfiles.size(); i++) {
			JSONObject jsonProfile = (JSONObject) jsonProfiles.get(i);
			String id = (String) jsonProfile.get("id");
			String name = (String) jsonProfile.get("name");
			profiles[i] = new Profile(id, name);
		}
		return profiles;
	}

	private static HttpBody getHttpBody(String... namesBatch) {
		return new HttpBody(JSONArray.toJSONString(Arrays.asList(namesBatch)));
	}

}
