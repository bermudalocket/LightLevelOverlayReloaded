package com.mmyzd.llor;

import net.minecraft.client.util.InputMappings;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {

	private static final Yaml YAML = new Yaml();

	public enum DisplayMode {
		STANDARD("Show green (safe) and red (spawnable) areas."),
		ADVANCED("Show green (safe), red (always spawnable) and yellow (currently safe, but will be spawnable at night) areas."),
		MINIMAL("Do not show green area. For other areas, use standard mode when not counting sky light, or advanced mode otherwise.");

		private String _description;

		DisplayMode(String desc) {
			_description = desc;
		}

		public String getDescription() {
			return _description;
		}

		public DisplayMode getNext() {
			switch (this) {
				default:
				case MINIMAL:  return ConfigManager.DisplayMode.STANDARD;
				case STANDARD: return ConfigManager.DisplayMode.ADVANCED;
				case ADVANCED: return ConfigManager.DisplayMode.MINIMAL;
			}
		}
	}

	public static boolean useSkyLight = false;
	public static DisplayMode displayMode = DisplayMode.STANDARD;
	public static int chunkRadius = 3;
	public static int pollingInterval = 1000;
	public static int blockRenderDistance = 32;

	private final File CONFIG_FILE;

	public ConfigManager(File configDir) {
		CONFIG_FILE = new File(configDir, "LightLevelOverlayReloaded.yml");
		reload();
	}

	void reload() {
		try (InputStream stream = new FileInputStream(CONFIG_FILE)) {
			Map<String, String> yaml = YAML.load(stream);
			useSkyLight = Boolean.valueOf(yaml.getOrDefault("useSkyLight", "false"));
			displayMode = DisplayMode.valueOf(yaml.getOrDefault("displayMode", "STANDARD"));
			chunkRadius = Integer.valueOf(yaml.getOrDefault("chunkRadius", "3"));
			pollingInterval = Integer.valueOf(yaml.getOrDefault("pollingInterval", "1000"));
			String hotkey = String.valueOf(yaml.getOrDefault("hotkey", "F4"));
			try {
				InputMappings.Input input = InputMappings.getInputByName(hotkey);
				LightLevelOverlayReloaded.HOTKEY.bind(InputMappings.getInputByName(hotkey));
			} catch (Exception e) {

			}
		} catch (IOException e) {
			e.printStackTrace();
			useSkyLight = false;
			displayMode = DisplayMode.STANDARD;
			chunkRadius = 3;
			pollingInterval = 1000;
		}
		save();
	}

	public void save() {
		Map<String, String> config = new HashMap<>();
		config.put("useSkyLight", Boolean.toString(useSkyLight));
		config.put("displayMode", displayMode.toString());
		config.put("chunkRadius", Integer.toString(chunkRadius));
		config.put("pollingInterval", Integer.toString(pollingInterval));
		config.put("hotkey", LightLevelOverlayReloaded.HOTKEY.func_197978_k());
		try {
			FileWriter writer = new FileWriter(CONFIG_FILE);
			YAML.dump(config, writer);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
