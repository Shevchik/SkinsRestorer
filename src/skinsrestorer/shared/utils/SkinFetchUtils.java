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

package skinsrestorer.shared.utils;

import skinsrestorer.libs.org.json.simple.parser.ParseException;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.format.SkinProperty;
import skinsrestorer.shared.utils.MojangAPI.Profile;

public class SkinFetchUtils {

	public static SkinProfile fetchSkinProfile(String name) throws SkinFetchFailedException {
		try {
			Profile profile = MojangAPI.getProfile(name);
			SkinProperty property = MojangAPI.getSkinProperty(profile.getId());
			return new SkinProfile(property);
		} catch (ParseException e) {
			throw new SkinFetchFailedException(SkinFetchFailedException.Reason.SKIN_RECODE_FAILED);
		} catch (SkinFetchFailedException sffe) {
			throw sffe;
		} catch (Throwable t) {
			throw new SkinFetchFailedException(t);
		}
	}

	public static class SkinFetchFailedException extends Exception {

		private static final long serialVersionUID = -7597517818949217019L;

		public SkinFetchFailedException(Reason reason) {
			super(reason.getExceptionCause()); 
		}

		public SkinFetchFailedException(Throwable exception) {
			super(Reason.GENERIC_ERROR.getExceptionCause()+": "+exception.getClass().getName()+": "+exception.getMessage(), exception); 
		}

		public static enum Reason {
			NO_PREMIUM_PLAYER("Can't find a valid premium player with that name"),
			NO_SKIN_DATA("No skin data found for player with that name"),
			SKIN_RECODE_FAILED("Can't decode skin data"),
			RATE_LIMITED("Rate limited"),
			GENERIC_ERROR("An error has occured");

			private String exceptionCause;

			private Reason(String exceptionCause) {
				this.exceptionCause = exceptionCause;
			}

			public String getExceptionCause() {
				return exceptionCause;
			}
		}
		
	}

}
