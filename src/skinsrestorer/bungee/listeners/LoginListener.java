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

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;

import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.utils.SkinFetchUtils;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {

	private static final MethodHandle profileFieldSetter = getProfileField();
	private static MethodHandle getProfileField() {
		try {
			Field profileField = InitialHandler.class.getDeclaredField("loginProfile"); 
			profileField.setAccessible(true);
			return MethodHandles.lookup().unreflectSetter(profileField);
		} catch (Throwable t) {
			System.err.println("Failed to get method handle for initial handel loginProfile field");
			t.printStackTrace();
		}
		return null;
	}

	//load skin data on login
	@EventHandler
	public void onPreLogin(final LoginEvent event) {
		final String name = event.getConnection().getName();
		final SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
		if (skinprofile.isValid()) {
			SkinsRestorer.getInstance().logInfo("Skin for player " + name + " is already cached");
			return;
		}
		event.registerIntent(SkinsRestorer.getInstance());
		ProxyServer.getInstance().getScheduler().runAsync(SkinsRestorer.getInstance(), new Runnable() {
			@Override
			public void run() {
				try {
					SkinProfile profile = SkinFetchUtils.fetchSkinProfile(name, skinprofile.getUUID());
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
		SkinProfile skinprofile = SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name);
		if (skinprofile.isValid()) {
			try {
				SkinProperty skinproperty = skinprofile.getPlayerSkinProperty();
				InitialHandler handler = (InitialHandler) event.getPlayer().getPendingConnection();
				Property[] properties = new Property[1];
				properties[0] = new Property(skinproperty.getName(), skinproperty.getValue(), skinproperty.getSignature());
				LoginResult profile = new LoginResult(event.getPlayer().getUniqueId().toString(), properties);
				profileFieldSetter.invokeExact(handler, profile);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

}
