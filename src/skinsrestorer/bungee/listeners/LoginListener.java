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
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.InitialHandler;
import net.md_5.bungee.connection.LoginResult;
import net.md_5.bungee.connection.LoginResult.Property;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;

public class LoginListener implements Listener {

	protected static final Field profileField = getProfileField();
	private static Field getProfileField() {
		try {
			Field profileField = InitialHandler.class.getDeclaredField("loginProfile"); 
			profileField.setAccessible(true);
			return profileField;
		} catch (Throwable t) {
			System.err.println("Failed to get initial handler loginProfile field");
			t.printStackTrace();
		}
		return null;
	}

	//load skin data on login
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPreLogin(final LoginEvent event) {
		if (event.isCancelled()) {
			return;
		}
		final String name = event.getConnection().getName();
		final SkinProfile skinprofile = SkinStorage.getInstance().getOrCreateSkinData(name);
		event.registerIntent(SkinsRestorer.getInstance());
		ProxyServer.getInstance().getScheduler().runAsync(SkinsRestorer.getInstance(), () -> {
			try {
				skinprofile.attemptUpdate();
			} catch (SkinFetchFailedException e) {
				SkinsRestorer.getInstance().logInfo("Skin fetch failed for player " + name + ": " + e.getMessage());
			} finally {
				event.completeIntent(SkinsRestorer.getInstance());
			}
		});
	}

	//fix profile on login
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(final PostLoginEvent event) {
		String name = event.getPlayer().getName();
		SkinProfile skinprofile = SkinStorage.getInstance().getOrCreateSkinData(name);
		skinprofile.applySkin(property -> {
			try {
				Property textures = new Property(property.getName(), property.getValue(), property.getSignature());
				InitialHandler handler = (InitialHandler) event.getPlayer().getPendingConnection();
				LoginResult profile = (LoginResult) profileField.get(handler);
				if (profile == null) {
					profile = new LoginResult(event.getPlayer().getUniqueId().toString(), name, new Property[] { textures });
				} else {
					Property[] present = profile.getProperties();
					boolean alreadyHasSkin = false;
					if (!skinprofile.isForced()) {
						for (Property prop : present) {
							if (prop.getName().equals(textures.getName())) {
								alreadyHasSkin = true;
							}
						}
					}
					if (!alreadyHasSkin) {
						Property[] newprops = new Property[present.length + 1];
						System.arraycopy(present, 0, newprops, 0, present.length);
						newprops[present.length] = textures;
						profile.setProperties(newprops);
					}
				}
				profileField.set(handler, profile);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		});
	}

}
