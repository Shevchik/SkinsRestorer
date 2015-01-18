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

package skinsrestorer.bukkit.listeners.v_s_1_8;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.v1_8_R1.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_8_R1.PlayerInfoData;

import org.bukkit.event.Listener;

import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.bukkit.listeners.IListener;
import skinsrestorer.shared.format.SkinProfile;

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
					profiles.write(0, ProfileUtils.addSkinToProfile(profile, skinprofile));
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(loginsuccListener);
		listeners.add(loginsuccListener);
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
							ProfileUtils.addSkinToProfile(data.a(), skinprofile),
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
		registerLoginOutSuccUUIDListener();
		registerTabListItemSkinlistener();
	}

	@Override
	public void unregister() {
		for (PacketListener listener : listeners) {
			ProtocolLibrary.getProtocolManager().removePacketListener(listener);
		}
		listeners.clear();
	}

}
