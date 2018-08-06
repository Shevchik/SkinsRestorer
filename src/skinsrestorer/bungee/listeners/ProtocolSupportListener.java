package skinsrestorer.bungee.listeners;

import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import protocolsupport.api.events.PlayerProfileCompleteEvent;
import protocolsupport.api.utils.ProfileProperty;
import skinsrestorer.bukkit.SkinsRestorer;
import skinsrestorer.shared.format.SkinProfile;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.SkinFetchUtils.SkinFetchFailedException;

public class ProtocolSupportListener implements Listener {

	@EventHandler
	public void onSkinResolve(PlayerProfileCompleteEvent event) {
		String name = event.getConnection().getProfile().getOriginalName();
		SkinProfile skinprofile = SkinStorage.getInstance().getOrCreateSkinData(name);
		try {
			skinprofile.attemptUpdate();
		} catch (SkinFetchFailedException e) {
			SkinsRestorer.getInstance().logInfo("Skin fetch failed for player "+name+": "+e.getMessage());
		}
		skinprofile.applySkin(property -> {
			String propertyname = property.getName();
			if (event.getProperties(propertyname).isEmpty() || skinprofile.isForced()) {
				event.removeProperties(propertyname);
				event.addProperty(new ProfileProperty(propertyname, property.getValue(), property.getSignature()));
			}
		});
	}

}
