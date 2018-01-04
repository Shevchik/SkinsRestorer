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

package skinsrestorer.bukkit.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import skinsrestorer.bukkit.SkinsRestorer;

import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class LoginListener implements Listener {

	//load skin data on async prelogin event
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		if (event.getLoginResult() != AsyncPlayerPreLoginEvent.Result.ALLOWED) {
			return;
		}
		String name = event.getName();
		SkinProfile skinprofile = SkinStorage.getInstance().getOrCreateSkinData(name);
		try {
			skinprofile.attemptUpdate();
		} catch (SkinFetchFailedException e) {
			SkinsRestorer.getInstance().logInfo("Skin fetch failed for player "+name+": "+e.getMessage());
		}
	}

	//fix skin on player login
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onLoginEvent(PlayerLoginEvent event) {
		if (event.getResult() != PlayerLoginEvent.Result.ALLOWED) {
			return;
		}
		final Player player = event.getPlayer();
		SkinProfile skinprofile = SkinStorage.getInstance().getOrCreateSkinData(player.getName());
		skinprofile.applySkin(property -> {
			WrappedGameProfile wrappedprofile = WrappedGameProfile.fromPlayer(player);
			WrappedSignedProperty wrappedproperty = WrappedSignedProperty.fromValues(property.getName(), property.getValue(), property.getSignature());
			if (!wrappedprofile.getProperties().containsKey(wrappedproperty.getName()) || skinprofile.isForced()) {
				wrappedprofile.getProperties().put(wrappedproperty.getName(), wrappedproperty);
			}
		});
	}

}
