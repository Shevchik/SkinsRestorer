package skinsrestorer.listeners.v_s_1_8;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import skinsrestorer.storage.SkinProfile;
import skinsrestorer.storage.SkinProperty;

class ProfileUtils {

	public static GameProfile recreateProfile(GameProfile oldprofile, SkinProfile skinprofile) {
		GameProfile newProfile = new GameProfile(skinprofile.getUUID(), oldprofile.getName());
		newProfile.getProperties().putAll(oldprofile.getProperties());
		PropertyMap properties = oldprofile.getProperties();
		Property skinProperty = convertProperty(skinprofile.getPlayerSkinProperty());
		if (!properties.containsKey(skinProperty.getName())) {
			properties.put(skinProperty.getName(), skinProperty);
		}
		return newProfile;
	}

	private static Property convertProperty(SkinProperty property) {
		if (property.hasSignature()) {
			return new Property(property.getName(), property.getValue(), property.getSignature());
		} else {
			return new Property(property.getValue(), property.getName());
		}
	}

}
