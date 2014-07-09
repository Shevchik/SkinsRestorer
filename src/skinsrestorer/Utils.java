package skinsrestorer;

import net.minecraft.server.v1_7_R3.MinecraftServer;
import net.minecraft.util.com.google.common.collect.Iterables;
import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;
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

	public static Property getProp(String id) {
		try {
			GameProfile profile = MinecraftServer.getServer().av().fillProfileProperties(new GameProfile(UUIDTypeAdapter.fromString(id), ""), true);
			Property textures = Iterables.getFirst(profile.getProperties().get("textures"), null);
            return textures;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
