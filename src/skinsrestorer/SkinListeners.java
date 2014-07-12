package skinsrestorer;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;
import com.mojang.api.profiles.Profile;

public class SkinListeners implements Listener {

	private SkinsRestorer plugin;
	public SkinListeners(SkinsRestorer plugin) {
		this.plugin = plugin;
	}

	//map to hold skin data
	private ConcurrentHashMap<String, SkinProfile> skins = new ConcurrentHashMap<String, SkinProfile>();
	private boolean hasLoadedSkinData(String name) {
		return skins.containsKey(name.toLowerCase());
	}
	private SkinProfile getLoadedSkinData(String name) {
		return skins.get(name.toLowerCase());
	}

	//load skin data on async prelogin event
	@EventHandler
	public void onPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		if (hasLoadedSkinData(name) && !getLoadedSkinData(name).isTooDamnOld()) {
			return;
		}
		Profile prof = DataUtils.getProfile(name);
		if (prof == null) {
			return;
		}
		Property prop = DataUtils.getProp(prof.getId());
		if (prop == null) {
			return;
		}
		try {
			SkinProfile profile = new SkinProfile(UUIDTypeAdapter.fromString(prof.getId()), prop);
			skins.put(name.toLowerCase(), profile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	//fix player skin data on named entity spawn packet
	public void registerPlayerSkinListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(
			new PacketAdapter(
				PacketAdapter.params(plugin, PacketType.Play.Server.NAMED_ENTITY_SPAWN)
			) {
				@Override
				public void onPacketSending(PacketEvent event) {
					WrappedGameProfile origprofile = event.getPacket().getGameProfiles().getValues().get(0);
					String name = origprofile.getName();
					if (hasLoadedSkinData(name)) {
						SkinProfile skinprofile = getLoadedSkinData(name);
						WrappedGameProfile newprofile = new WrappedGameProfile(skinprofile.getUUID(), origprofile.getName());
						WrappedSignedProperty wprop = WrappedSignedProperty.fromHandle(skinprofile.getPlayerSkinData());
						newprofile.getProperties().clear();
						newprofile.getProperties().put(skinprofile.getPlayerSkinData().getName(), wprop);
						event.getPacket().getGameProfiles().write(0, newprofile);
					}
				}
			}
		);
	}

	//fix picked up skull game profile
	@EventHandler
	public void onPickupHead(PlayerPickupItemEvent event) {
		try {
			ItemStack itemstack = event.getItem().getItemStack();
			if (itemstack.getType() == Material.SKULL_ITEM && (itemstack.getDurability() == 3)) {
				SkullMeta meta = (SkullMeta) itemstack.getItemMeta();
				if (meta == null) {
					meta = (SkullMeta) Bukkit.getItemFactory().getItemMeta(Material.SKULL_ITEM);
				}
				String name = meta.getOwner();
				if (hasLoadedSkinData(name)) {
					SkinProfile skinprofile = getLoadedSkinData(name);
					GameProfile newprofile = new GameProfile(skinprofile.getUUID(), name);
					newprofile.getProperties().clear();
					newprofile.getProperties().put(skinprofile.getHeadSkinData().getName(), skinprofile.getHeadSkinData());
					Field profileField = meta.getClass().getDeclaredField("profile");
					profileField.setAccessible(true);
					profileField.set(meta, newprofile);
					itemstack.setItemMeta(meta);
					event.getItem().setItemStack(itemstack);
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}

}
