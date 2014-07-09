package skinsrestorer;

import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;

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
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
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
		skins.put(name.toLowerCase(), new SkinProfile(UUIDTypeAdapter.fromString(prof.getId()), prop));
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
						WrappedGameProfile newprofile = new WrappedGameProfile(skinprofile.getUUID(), origprofile.getName());
						WrappedSignedProperty wprop = WrappedSignedProperty.fromHandle(skinprofile.getProperty());
						newprofile.getProperties().clear();
						newprofile.getProperties().put(skinprofile.getProperty().getName(), wprop);
						event.getPacket().getGameProfiles().write(0, newprofile);
					}
				}
			}
		);
	}

}
