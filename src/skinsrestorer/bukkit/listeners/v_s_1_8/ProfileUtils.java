package skinsrestorer.bukkit.listeners.v_s_1_8;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;

import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;

class ProfileUtils {

	public static GameProfile addSkinToProfile(GameProfile profile, SkinProfile skinprofile) {
		PropertyMap properties = profile.getProperties();
		Property skinProperty = convertProperty(skinprofile.getPlayerSkinProperty());
		if (!properties.containsKey(skinProperty.getName())) {
			properties.put(skinProperty.getName(), skinProperty);
		}
		return profile;
	}

	private static Property convertProperty(SkinProperty property) {
		if (property.hasSignature()) {
			return new Property(property.getName(), property.getValue(), property.getSignature());
		} else {
			return new Property(property.getValue(), property.getName());
		}
	}

}
