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

import java.lang.reflect.Field;
import java.util.logging.Level;

import net.minecraft.util.com.mojang.authlib.GameProfile;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import skinsrestorer.SkinsRestorer;
import skinsrestorer.storage.SkinProfile;
import skinsrestorer.utils.SkinGetUtils;
import skinsrestorer.utils.SkinGetUtils.SkinFetchFailedException;

public class UnifiedListener implements Listener {

	//load skin data on async prelogin event
	@EventHandler
	public void onPreLoginEvent(AsyncPlayerPreLoginEvent event) {
		String name = event.getName();
		if (SkinsRestorer.getInstance().getSkinStorage().hasLoadedSkinData(name) && !SkinsRestorer.getInstance().getSkinStorage().getLoadedSkinData(name).isTooDamnOld()) {
			return;
		}
		try {
			SkinProfile profile = SkinGetUtils.getSkinProfile(name);
			SkinsRestorer.getInstance().getSkinStorage().addSkinData(name, profile);
		} catch (SkinFetchFailedException e) {
			SkinsRestorer.getInstance().getLog().log(Level.INFO, "Skin fetch failed for player "+name+": "+e.getMessage());
		}
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
					GameProfile newprofile = new GameProfile(skinprofile.getUUID(), name);
					newprofile.getProperties().clear();
					newprofile.getProperties().put(skinprofile.getHeadSkinProperty().getName(), skinprofile.getHeadSkinProperty());
					Field profileField = meta.getClass().getDeclaredField("profile");
					profileField.setAccessible(true);
					profileField.set(meta, newprofile);
					itemstack.setItemMeta(meta);
				}
			}
		} catch (Exception e) {
		}
	}

}
