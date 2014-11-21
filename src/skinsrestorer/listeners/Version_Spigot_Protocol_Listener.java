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

package skinsrestorer.listeners;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import skinsrestorer.SkinsRestorer;
import skinsrestorer.storage.SkinProfile;
import skinsrestorer.utils.ProfileUtils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.reflect.StructureModifier;

public class Version_Spigot_Protocol_Listener implements IListener {

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
				if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
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
				if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
					profiles.write(0, ProfileUtils.recreateProfile(profile, skinprofile));
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(tablistListener);
	}

	@Override
	public void register() {
		registerPlayerSkinListener();
		registerTabListItemSkinlistener();
	}

	@Override
	public void unregister() {
		ProtocolLibrary.getProtocolManager().removePacketListener(spawnListener);
		ProtocolLibrary.getProtocolManager().removePacketListener(tablistListener);
	}

}
