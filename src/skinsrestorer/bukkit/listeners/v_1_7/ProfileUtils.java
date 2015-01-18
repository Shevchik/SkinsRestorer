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

package skinsrestorer.bukkit.listeners.v_1_7;

import net.minecraft.util.com.mojang.authlib.GameProfile;
import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.authlib.properties.PropertyMap;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;

class ProfileUtils {

	public static GameProfile addSkinToProfile(GameProfile profile, SkinProfile skinprofile) {
		PropertyMap properties = profile.getProperties();
		Property skinProperty = convertProperty(skinprofile.getPlayerSkinProperty());
		if (!properties.containsKey(skinProperty.getName())) {
			properties.put(skinProperty.getName(), skinProperty);
		}
		return profile;
	}

	private static Property convertProperty(SkinProperty property) {
		if (property.hasSignature()) {
			return new Property(property.getName(), property.getValue(), property.getSignature());
		} else {
			return new Property(property.getValue(), property.getName());
		}
	}

}
