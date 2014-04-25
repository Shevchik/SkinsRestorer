package skinsrestorer;

import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.properties.Property;

public class SkinProfile {

	private UUID uuid;
	private Property prop;
	public SkinProfile(UUID uuid, Property prop) {
		this.uuid = uuid;
		this.prop = prop;
	}

	public UUID getUUID() {
		return uuid;
	}

	public Property getProperty() {
		return prop;
	}

}
