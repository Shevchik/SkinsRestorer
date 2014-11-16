package skinsrestorer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	private ServerVersion serverversion = ServerVersion.VERSION_SPIGOT_PROTOCOL;

	public ServerVersion getServerVersion() {
		return serverversion;
	}

	private ArrayList<String> allPossibleVersions = new ArrayList<String>();
	{
		for (ServerVersion version : ServerVersion.values()) {
			allPossibleVersions.add(version.toString());
		}
	}

	public void loadConfig() {
		File conffile = new File(SkinsRestorer.getInstance().getDataFolder(), "config.yml");
		FileConfiguration conf = YamlConfiguration.loadConfiguration(conffile);
		ServerVersion version = ServerVersion.valueOf(conf.getString("server_version", serverversion.toString()));
		if (version != null) {
			serverversion = version;
		}
		conf = new YamlConfiguration();
		conf.set("server_version", serverversion.toString());
		conf.set("all_possible_versions", allPossibleVersions);
		try {
			conf.save(conffile);
		} catch (IOException e) {
		}
	}

	public static enum ServerVersion {
		VERSION_1_7, VERSION_SPIGOT_PROTOCOL, VERSION_1_8;
	}

}
