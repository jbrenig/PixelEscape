package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import net.brenig.pixelescape.PixelEscape;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Manages debug settings
 */
public class GameDebugSettings {


	private static final String PREF_DEBUG_SETTINGS = "PixelEscape_Debug_Preferences";

	private static final Map<String, Boolean> defaults = createDefaults();



	private final Preferences prefs;

	public GameDebugSettings() {
		prefs = Gdx.app.getPreferences(PREF_DEBUG_SETTINGS);
	}

	private static Map<String, Boolean> createDefaults() {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		result.put("SHOW_FPS", false);
		result.put("AUTO_PAUSE", true);
		result.put("PLAYER_EXPLOSION_RED", false);
		result.put("DEBUG_MODE_COORDS", false);
		result.put("DEBUG_WORLD_GEN_VALIDATE", false);
		result.put("DEBUG_SCREEN_BOUNDS", false);
		result.put("DEBUG_UI", false);
		result.put("DEBUG_LOGGING", false);
		result.put("DEBUG_GOD_MODE", false);
		result.put("DEBUG_MUSIC", false);
		result.put("SCREEN_SHAKE", true);
		return Collections.unmodifiableMap(result);
	}

	public void saveToDisk() {
		prefs.flush();
	}

	public boolean getBoolean(String s) {
		Boolean def = defaults.get(s);
		if(!PixelEscape.getPixelEscape().gameConfig.debugSettingsAvailable()) {
			//return default if debug settings are deactivated
			return def == null ? false : def;
		}
		if(def == null) {
			return prefs.getBoolean(s);
		}
		return prefs.getBoolean(s, def);
	}

	public void setBoolean(String s, boolean b) {
		prefs.putBoolean(s, b);
	}

	public static boolean get(String s) {
		return PixelEscape.getPixelEscape().gameDebugSettings.getBoolean(s);
	}

	public static void set(String s, boolean b) {
		PixelEscape.getPixelEscape().gameDebugSettings.setBoolean(s, b);
	}
}
