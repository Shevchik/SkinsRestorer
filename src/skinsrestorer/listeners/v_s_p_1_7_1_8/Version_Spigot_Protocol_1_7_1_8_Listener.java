/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 *
 */

package skinsrestorer.listeners.v_s_p_1_7_1_8;

import java.lang.reflect.Field;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import net.minecraft.util.com.mojang.authlib.GameProfile;
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

public class Version_Spigot_Protocol_1_7_1_8_Listener implements IListener, Listener {

	private PacketListener spawnListener;
	private PacketListener tablistListener;

	//fix player skin data on named entity spawn packet
	private void registerPlayerSkinListener() {
		spawnListener = new PacketAdapter(
			PacketAdapter
			.params(SkinsRestorer.getInstance(), PacketType.Play.Server.NAMED_ENTITY_SPAWN)
			.listenerPriority(ListenerPriority.HIGHEST)
		) {
			@Override
			public void onPacketSending(PacketEvent event) {
				StructureModifier<GameProfile> profiles = event.getPacket().getSpecificModifier(GameProfile.class);
				GameProfile profile = profiles.read(0);
				String name = profile.getName();
				SkinsRestorer.getInstance().logDebug("[V_S_P_1_7_1_8]: Checking NameEntitySpawn packet for player "+name);
				if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
					SkinsRestorer.getInstance().logDebug("[V_S_P_1_7_1_8]: Modifying NameEntitySpawn packet for player "+name);
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
					profiles.write(0, ProfileUtils.recreateProfile(profile, skinprofile));
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
			@Override
			public void onPacketSending(PacketEvent event) {
				StructureModifier<GameProfile> profiles = event.getPacket().getSpecificModifier(GameProfile.class);
				GameProfile profile = profiles.read(0);
				String name = profile.getName();
				SkinsRestorer.getInstance().logDebug("[V_S_P_1_7_1_8]: Checking PlayerInfo packet for player "+name);
				if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
					SkinsRestorer.getInstance().logDebug("[V_S_P_1_7_1_8]: Modifying PlayerInfo packet for player "+name);
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
					profiles.write(0, ProfileUtils.recreateProfile(profile, skinprofile));
				}
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
