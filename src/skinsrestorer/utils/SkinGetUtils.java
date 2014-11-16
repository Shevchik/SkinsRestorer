package skinsrestorer.utils;

import org.json.simple.parser.ParseException;

import net.minecraft.util.com.mojang.authlib.properties.Property;
import net.minecraft.util.com.mojang.util.UUIDTypeAdapter;

import com.mojang.api.profiles.Profile;

import skinsrestorer.storage.SkinProfile;

public class SkinGetUtils {

	public static SkinProfile getSkinProfile(String name) throws SkinFetchFailedException {
		try {
			Profile prof = DataUtils.getProfile(name);
			if (prof == null) {
				throw new SkinFetchFailedException(SkinFetchFailedException.Reason.NO_PREMIUM_PLAYER);
			}
			Property prop = DataUtils.getProp(prof.getId());
			if (prop == null) {
				throw new SkinFetchFailedException(SkinFetchFailedException.Reason.NO_SKIN_DATA);
			}
			try {
				return new SkinProfile(UUIDTypeAdapter.fromString(prof.getId()), prop);
			} catch (ParseException e) {
				throw new SkinFetchFailedException(SkinFetchFailedException.Reason.SKIN_RECODE_FAILED, e);
			}
		} catch (Exception e) {
			throw new SkinFetchFailedException(SkinFetchFailedException.Reason.GENERIC_ERROR, e);
		}
	}

	public static class SkinFetchFailedException extends Exception {

		private static final long serialVersionUID = -7597517818949217019L;

		public SkinFetchFailedException(Reason reason) {
			super(reason.getExceptionCause()); 
		}

		public SkinFetchFailedException(Reason reason, Exception exception) {
			super(reason.getExceptionCause(), exception); 
		}

		public static enum Reason {
			NO_PREMIUM_PLAYER("Can't find a valid premium player with that name"),
			NO_SKIN_DATA("No skin data found for player with that name"),
			SKIN_RECODE_FAILED("Can't decode skin data"),
			GENERIC_ERROR("An error has ouccured");

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
