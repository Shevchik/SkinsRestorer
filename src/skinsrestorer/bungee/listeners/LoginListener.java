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

package skinsrestorer.bungee.listeners;

import java.lang.reflect.Field;

import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.utils.SkinGetUtils;
import skinsrestorer.shared.utils.SkinGetUtils.SkinFetchFailedException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {

	//load skin data on login
	@EventHandler
	public void onPreLogin(final LoginEvent event) {
		final String name = event.getConnection().getName();
		if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name) && !SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name).isTooDamnOld()) {
			SkinsRestorer.getInstance().logInfo("Skin for player " + name + " is already cached");
			return;
		}
		event.registerIntent(SkinsRestorer.getInstance());
		ProxyServer.getInstance().getScheduler().runAsync(SkinsRestorer.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					SkinProfile profile = SkinGetUtils.getSkinProfile(name);
					SkinsRestorer.getInstance().getSkinStorage().addSkinData(name, profile);
					SkinsRestorer.getInstance().logInfo("Skin for player " + name + " was succesfully fetched and cached");
				} catch (SkinFetchFailedException e) {
					SkinsRestorer.getInstance().logInfo("Skin fetch failed for player " + name + ": " + e.getMessage());
				} finally {
					event.completeIntent(SkinsRestorer.getInstance());
				}
			}
		});
	}

	//fix profile on login
	@EventHandler
	public void onPostLogin(PostLoginEvent event) {
		String name = event.getPlayer().getName();
		if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name)) {
			try {
				SkinProperty skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name).getPlayerSkinProperty();
				InitialHandler handler = (InitialHandler) event.getPlayer().getPendingConnection();
				Property[] properties = new Property[1];
				properties[0] = new Property(skinprofile.getName(), skinprofile.getValue(), skinprofile.getSignature());
				LoginResult profile = new LoginResult(event.getPlayer().getUniqueId().toString(), properties);
				getProfileField().set(handler, profile);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	private Field profileField = null;
	private Field getProfileField() throws NoSuchFieldException, SecurityException {
		this.profileField = InitialHandler.class.getDeclaredField("loginProfile");
		this.profileField.setAccessible(true);
		return this.profileField;
	}

}
