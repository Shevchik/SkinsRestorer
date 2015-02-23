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
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;

import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.utils.SkinFetchUtils;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class LoginListener implements Listener {

	//load skin data on async prelogin event
	@EventHandler
	public void onAsyncPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		if (SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name).isValid()) {
			SkinsRestorer.getInstance().logInfo("Skin for player "+name+" is already cached");
			return;
		}
		try {
			SkinProfile profile = SkinFetchUtils.fetchSkinProfile(name);
			SkinsRestorer.getInstance().getSkinStorage().addSkinData(name, profile);
			SkinsRestorer.getInstance().logInfo("Skin for player "+name+" was succesfully fetched and cached");
		} catch (SkinFetchFailedException e) {
			SkinsRestorer.getInstance().logInfo("Skin fetch failed for player "+name+": "+e.getMessage());
		}
	}

	//fix skin on player login
	@EventHandler
	public void onLoginEvent(PlayerLoginEvent event) {
		Player player = event.getPlayer();
		if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(player.getName())) {
			SkinProperty skinproperty = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(player.getName()).getPlayerSkinProperty();
			WrappedGameProfile wrappedprofile = WrappedGameProfile.fromPlayer(player);
			WrappedSignedProperty wrappedproperty = WrappedSignedProperty.fromValues(skinproperty.getName(), skinproperty.getValue(), skinproperty.getSignature());
			if (!wrappedprofile.getProperties().containsKey(wrappedproperty.getName())) {
				wrappedprofile.getProperties().put(wrappedproperty.getName(), wrappedproperty);
			}
		}
	}

}
