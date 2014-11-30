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

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;

import skinsrestorer.SkinsRestorer;
import skinsrestorer.storage.SkinProfile;
import skinsrestorer.utils.SkinGetUtils;
import skinsrestorer.utils.SkinGetUtils.SkinFetchFailedException;

public class LoginListener implements Listener {

	//load skin data on async prelogin event
	@EventHandler
	public void onPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name) && !SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name).isTooDamnOld()) {
			SkinsRestorer.getInstance().logInfo("Skin for player "+name+" is already cached");
			return;
		}
		try {
			SkinProfile profile = SkinGetUtils.getSkinProfile(name);
			SkinsRestorer.getInstance().getSkinStorage().addSkinData(name, profile);
			SkinsRestorer.getInstance().logInfo("Skin for player "+name+" was succesfully fetched and cached");
		} catch (SkinFetchFailedException e) {
			SkinsRestorer.getInstance().logInfo("Skin fetch failed for player "+name+": "+e.getMessage());
		}
	}

}
