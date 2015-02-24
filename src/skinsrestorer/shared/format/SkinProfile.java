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

import java.util.UUID;

import skinsrestorer.shared.utils.UUIDUtil;

public class SkinProfile {

	public static final SkinProfile NONE = new SkinProfile() {
		@Override
		public boolean isValid() {
			return false;
		}

		@Override
		public UUID getUUID() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public long getCreationDate() {
			return 0;
		}

		@Override
		public SkinProperty getPlayerSkinProperty() {
			return null;
		}

		@Override
		public boolean isForced() {
			return false;
		}
	};

	private long timestamp;
	private boolean isForced;
	private SkinProperty playerSkinData;
	private Profile profile;

	private SkinProfile() {
	}

	public SkinProfile(Profile profile, SkinProperty skinData) {
		this.timestamp = System.currentTimeMillis();
		this.profile = profile;
		this.playerSkinData = skinData;
	}

	public SkinProfile(Profile profile, SkinProperty skinData, long creationTime) {
		this(profile, skinData);
		this.timestamp = creationTime;
	}

	public boolean isValid() {
		return (System.currentTimeMillis() - timestamp) <= (2 * 60 * 60 * 1000) || isForced();
	}

	public boolean isForced() {
		return isForced;
	}

	public void setForced() {
		isForced = true;
	}

	public long getCreationDate() {
		return timestamp;
	}

	public SkinProperty getPlayerSkinProperty() {
		return playerSkinData;
	}

	public UUID getUUID() {
		return UUIDUtil.fromDashlessString(profile.getId());
	}

	public String getName() {
		return profile.getName();
	}

	public SkinProfile clone() {
		return new SkinProfile(profile, playerSkinData, timestamp);
	}

}
