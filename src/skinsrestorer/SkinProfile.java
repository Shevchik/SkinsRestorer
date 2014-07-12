package skinsrestorer;

import java.util.UUID;

import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.org.apache.commons.codec.Charsets;
import net.minecraft.util.org.apache.commons.codec.binary.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class SkinProfile {

	private UUID uuid;
	private Property playerSkinData;
	private Property headSkinData;
	public SkinProfile(UUID uuid, Property skinData) throws ParseException {
		this.uuid = uuid;
		this.playerSkinData = skinData;
		this.headSkinData = recodePlayerSkinDataToHeadSkinData(skinData);
	}

	public UUID getUUID() {
		return uuid;
	}

	public Property getPlayerSkinData() {
		return playerSkinData;
	}

	public Property getHeadSkinData() {
		return headSkinData;
	}

	private Property recodePlayerSkinDataToHeadSkinData(Property playerskindata) throws ParseException {
		String oldvalue = playerskindata.getValue();
		JSONObject skindata = (JSONObject) new JSONParser().parse(new String(Base64.decodeBase64(oldvalue), Charsets.UTF_8));
		skindata.remove("isPublic");
		return new Property(playerskindata.getName(), Base64.encodeBase64String(skindata.toJSONString().getBytes(Charsets.UTF_8)), "");
	}

}
