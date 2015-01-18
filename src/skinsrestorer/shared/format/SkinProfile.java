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

package skinsrestorer.shared.format;

import skinsrestorer.bukkit.SkinsRestorer;

public class SkinProfile {

	private long timestamp;
	private SkinProperty playerSkinData;

	public SkinProfile(SkinProperty skinData) {
		timestamp = System.currentTimeMillis();
		playerSkinData = skinData;
	}

	public SkinProfile(SkinProperty skinData, long creationTime) {
		this(skinData);
		timestamp = creationTime;
	}

	public boolean isTooDamnOld() {
		return (System.currentTimeMillis() - timestamp) > (SkinsRestorer.getInstance().getConfiguration().getSkinCacheTime() * 1000);
	}

	public long getCreationDate() {
		return timestamp;
	}

	public SkinProperty getPlayerSkinProperty() {
		return playerSkinData;
	}

}
