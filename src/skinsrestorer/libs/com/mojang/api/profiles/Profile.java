package skinsrestorer.libs.com.mojang.api.profiles;

public class Profile {

	private String id;
	private String name;

	public Profile(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
