package net.brenig.pixelescape.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Contains information about user preferences and handles loading and saving of those
 * Created by Jonas Brenig on 26.09.2015.
 */
public class GameSettings {


	private static final class Keys {
		public static final String soundEnabled = "ENABLE_SOUNDS";
		public static final String musicEnabled = "ENABLE_MUSIC";
	}

	private static final class Defaults {
		public static final boolean soundEnabled = true;
		public static final boolean musicEnabled = true;
	}

	public static final String PREF_MAIN_SETTINGS = "PixelEscape_User_Preferences";

	public boolean soundEnabled = Defaults.soundEnabled;
	public boolean musicEnabled = Defaults.musicEnabled;

	/**
	 * fullscreen, setting does not persist atm
	 */
	public boolean fullscreen = false;

	public GameSettings() {

	}

	public Preferences loadFromDisk() {
		Preferences prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS);

		//Sound
		soundEnabled = prefs.getBoolean(Keys.soundEnabled, Defaults.soundEnabled);
		musicEnabled = prefs.getBoolean(Keys.musicEnabled, Defaults.musicEnabled);
		return prefs;
	}

	public void saveToDisk() {
		save().flush();
	}

	public Preferences save() {
		Preferences prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS);

		//Sound
		prefs.putBoolean(Keys.soundEnabled, soundEnabled);
		prefs.putBoolean(Keys.musicEnabled, musicEnabled);
		return prefs;
	}

}
