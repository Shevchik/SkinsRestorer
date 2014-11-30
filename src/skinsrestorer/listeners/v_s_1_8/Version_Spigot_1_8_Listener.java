package skinsrestorer.listeners.v_s_1_8;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PlayerInfoData;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

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

	private PacketListener spawnListener;
	private PacketListener tablistListener;

	//fix uuid on entity spawn packet
	private void registerPlayerSkinListener() {
		spawnListener = new PacketAdapter(
			PacketAdapter
			.params(SkinsRestorer.getInstance(), PacketType.Play.Server.NAMED_ENTITY_SPAWN)
			.listenerPriority(ListenerPriority.HIGHEST)
		) {
			@Override
			public void onPacketSending(PacketEvent event) {
				StructureModifier<UUID> uuids = event.getPacket().getSpecificModifier(UUID.class);
				UUID uuid = uuids.read(0);
				Player player = Bukkit.getPlayer(uuid);
				SkinsRestorer.getInstance().logDebug("[V_S_1_8]: Checking NameEntitySpawn packet for player "+player.getName());
				if (player != null && SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(player.getName())) {
					SkinsRestorer.getInstance().logDebug("[V_S_1_8]: Modifying NameEntitySpawn packet for player "+player.getName());
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(player.getName());
					uuids.write(0, skinprofile.getUUID());
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(spawnListener);
	}

	//fix skin on tab list add packet
	private void registerTabListItemSkinlistener() {
		tablistListener = new PacketAdapter(
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
					SkinsRestorer.getInstance().logDebug("[V_S_1_8]: Checking PlayerInfo packet for player "+name);
					if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
						SkinsRestorer.getInstance().logDebug("[V_S_1_8]: Modifying PlayerInfo packet for player "+name);
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

	//fix spawned head item game profile
	@EventHandler
	public void onHeadItemSpawn(ItemSpawnEvent event) {
		ItemStack itemstack = event.getEntity().getItemStack();
		if ((itemstack.getType() == Material.SKULL_ITEM) && (itemstack.getDurability() == 3)) {
			fixHeadSkin(itemstack);
			event.getEntity().setItemStack(itemstack);
		}
	}

	private void fixHeadSkin(ItemStack itemstack) {
		try {
			SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
			if (meta == null) {
				meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
			}
			if (meta.hasOwner()) {
				String name = meta.getOwner();
				if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
					Field profileField = meta.getClass().getDeclaredField("profile");
					profileField.setAccessible(true);
					profileField.set(meta, ProfileUtils.recreateProfile((GameProfile) profileField.get(meta), skinprofile));
					itemstack.setItemMeta(meta);
				}
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void register() {
		Bukkit.getPluginManager().registerEvents(this, SkinsRestorer.getInstance());
		registerPlayerSkinListener();
		registerTabListItemSkinlistener();
	}

	@Override
	public void unregister() {
		ProtocolLibrary.getProtocolManager().removePacketListener(spawnListener);
		ProtocolLibrary.getProtocolManager().removePacketListener(tablistListener);
		HandlerList.unregisterAll(this);
	}

}
