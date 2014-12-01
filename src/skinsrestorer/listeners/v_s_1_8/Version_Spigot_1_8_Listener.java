package skinsrestorer.listeners.v_s_1_8;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PlayerInfoData;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import skinsrestorer.SkinsRestorer;
import skinsrestorer.listeners.IListener;
import skinsrestorer.storage.SkinProfile;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.StructureModifier;

import com.mojang.authlib.GameProfile;

public class Version_Spigot_1_8_Listener implements IListener, Listener {

	private final ArrayList<PacketListener> listeners = new ArrayList<PacketListener>();

	//fix uuid on login succ packet
	private void registerLoginOutSuccUUIDListener() {
		PacketListener loginsuccListener = new PacketAdapter(
			PacketAdapter
			.params(SkinsRestorer.getInstance(), PacketType.Login.Server.SUCCESS)
			.listenerPriority(ListenerPriority.HIGHEST)
		) {
			@Override
			public void onPacketSending(PacketEvent event) {
				StructureModifier<GameProfile> profiles = event.getPacket().getSpecificModifier(GameProfile.class);
				GameProfile profile = profiles.read(0);
				String name = profile.getName();
				if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
					profiles.write(0, ProfileUtils.recreateProfile(profile, skinprofile));
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(loginsuccListener);
		listeners.add(loginsuccListener);
	}

	//fix uuid on entity spawn packet
	private void registerPlayerSkinListener() {
		PacketListener spawnListener = new PacketAdapter(
			PacketAdapter
			.params(SkinsRestorer.getInstance(), PacketType.Play.Server.NAMED_ENTITY_SPAWN)
			.listenerPriority(ListenerPriority.HIGHEST)
		) {
			@Override
			public void onPacketSending(PacketEvent event) {
				StructureModifier<UUID> uuids = event.getPacket().getSpecificModifier(UUID.class);
				UUID uuid = uuids.read(0);
				Player player = Bukkit.getPlayer(uuid);
				if (player != null && SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(player.getName())) {
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(player.getName());
					uuids.write(0, skinprofile.getUUID());
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(spawnListener);
	}

	//fix skin on tab list add packet
	private void registerTabListItemSkinlistener() {
		PacketListener tablistListener = new PacketAdapter(
			PacketAdapter
			.params(SkinsRestorer.getInstance(), PacketType.Play.Server.PLAYER_INFO)
			.listenerPriority(ListenerPriority.HIGHEST)
		) {
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void onPacketSending(PacketEvent event) {
				StructureModifier<List> lists = event.getPacket().getSpecificModifier(List.class);
				List<PlayerInfoData> datas = lists.read(0);
				List<PlayerInfoData> newdatas = new ArrayList<PlayerInfoData>();
				for (PlayerInfoData data : datas) {
					String name = data.a().getName();
					if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
						SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
						PlayerInfoData newdata = new PlayerInfoData(
							(PacketPlayOutPlayerInfo) event.getPacket().getHandle(),
							ProfileUtils.recreateProfile(data.a(), skinprofile),
							data.b(), data.c(), data.d()
						);
						newdatas.add(newdata);
					} else {
						newdatas.add(data);
					}
				}
				lists.write(0, newdatas);
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(tablistListener);
	}

	@Override
	public void register() {
		Bukkit.getPluginManager().registerEvents(this, SkinsRestorer.getInstance());
		registerLoginOutSuccUUIDListener();
		registerPlayerSkinListener();
		registerTabListItemSkinlistener();
	}

	@Override
	public void unregister() {
		for (PacketListener listener : listeners) {
			ProtocolLibrary.getProtocolManager().removePacketListener(listener);
		}
		listeners.clear();
		HandlerList.unregisterAll(this);
	}

}
