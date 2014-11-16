package skinsrestorer.utils;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;

import skinsrestorer.storage.SkinProfile;

public class ProfileUtils {

	public static void addTexturesData(GameProfile profile, SkinProfile skinprofile) {
		PropertyMap properties = profile.getProperties();
		Property skinProperty = skinprofile.getPlayerSkinProperty();
		if (properties.containsKey(skinProperty.getName())) {
			return;
		} else {
			properties.put(skinProperty.getName(), skinProperty);
		}
	}

}
