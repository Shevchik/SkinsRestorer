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

public class Version_1_8_Listener implements IListener {

	private PacketListener listener;

	//fix skin on tab list add packet
	private void registerTabListItemSkinlistener() {
		listener = new PacketAdapter(
			PacketAdapter
			.params(SkinsRestorer.getInstance(), PacketType.Play.Server.PLAYER_INFO)
			.listenerPriority(ListenerPriority.HIGHEST)
		) {
			@Override
			public void onPacketSending(PacketEvent event) {
				GameProfile profile = event.getPacket().getSpecificModifier(GameProfile.class).read(0);
				String name = profile.getName();
				if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
					SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
					ProfileUtils.addTexturesData(profile, skinprofile);
				}
			}
		};
		ProtocolLibrary.getProtocolManager().addPacketListener(listener);
	}

	@Override
	public void register() {
		registerTabListItemSkinlistener();
	}

	@Override
	public void unregister() {
		ProtocolLibrary.getProtocolManager().removePacketListener(listener);
	}

}
