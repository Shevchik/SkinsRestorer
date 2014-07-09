package skinsrestorer;

import java.lang.reflect.Field;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.mojang.api.profiles.Profile;

public class SkinsRestorer extends JavaPlugin implements Listener {

	private ConcurrentHashMap<String, SkinProfile> skins = new ConcurrentHashMap<String, SkinProfile>();

	@EventHandler
	public void onPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		Profile prof = Utils.getProfile(name);
		if (prof == null) {
			return;
		}
		Property prop = Utils.getProp(prof.getId());
		if (prop == null) {
			return;
		}
		skins.put(name.toLowerCase(), new SkinProfile(UUID.fromString(Utils.getUUIDString(prof.getId())), prop));
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		skins.remove(event.getPlayer().getName().toLowerCase());
	}

	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		ProtocolLibrary.getProtocolManager().addPacketListener(
			new PacketAdapter(
				PacketAdapter.params(this, PacketType.Play.Server.NAMED_ENTITY_SPAWN)
			) {
				@Override
				public void onPacketSending(PacketEvent event) {
					WrappedGameProfile origprofile = event.getPacket().getGameProfiles().getValues().get(0);
					String name = origprofile.getName();
					if (skins.containsKey(name.toLowerCase())) {
						SkinProfile skinprofile = skins.get(name.toLowerCase());
						Object packet = event.getPacket().getHandle();
						try {
							GameProfile profile = new GameProfile(skinprofile.getUUID(), name);
							profile.getProperties().put(skinprofile.getProperty().getName(), skinprofile.getProperty());
							Field gameProfileField = packet.getClass().getDeclaredField("b");
							gameProfileField.setAccessible(true);
							gameProfileField.set(packet, profile);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		);
	}

}
