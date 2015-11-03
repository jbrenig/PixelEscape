package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Contains information about user preferences and handles loading and saving of those
 * Created by Jonas Brenig on 26.09.2015.
 */
public class GameSettings {


	private static final class Keys {
		public static final String musicEnabled = "ENABLE_MUSIC";
		public static final String soundEnabled = "ENABLE_SOUNDS";
		public static final String musicVolume = "VOLUME_MUSIC";
		public static final String soundVolume = "VOLUME_SOUND";
	}

	private static final class Defaults {
		public static final boolean musicEnabled = true;
		public static final boolean soundEnabled = true;
		public static final float musicVolume = 0.5F;
		public static final float soundVolume = 0.5F;
	}

	public static final String PREF_MAIN_SETTINGS = "PixelEscape_User_Preferences";

	public boolean musicEnabled = Defaults.musicEnabled;
	public boolean soundEnabled = Defaults.soundEnabled;

	public float musicVolume = Defaults.soundVolume;
	public float soundVolume = Defaults.musicVolume;

	/**
	 * fullscreen, setting does not persist atm
	 */
	public boolean fullscreen = false;

	public GameSettings() {

	}

	public Preferences loadFromDisk() {
		Preferences prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS);

		//Sound
		musicEnabled = prefs.getBoolean(Keys.musicEnabled, Defaults.musicEnabled);
		soundEnabled = prefs.getBoolean(Keys.soundEnabled, Defaults.soundEnabled);

		musicVolume = prefs.getFloat(Keys.musicVolume, Defaults.musicVolume);
		soundVolume = prefs.getFloat(Keys.soundVolume, Defaults.soundVolume);
		return prefs;
	}

	public void saveToDisk() {
		save().flush();
	}

	public Preferences save() {
		Preferences prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS);

		//Sound
		prefs.putBoolean(Keys.musicEnabled, musicEnabled);
		prefs.putBoolean(Keys.soundEnabled, soundEnabled);

		prefs.putFloat(Keys.musicVolume, musicVolume);
		prefs.putFloat(Keys.soundVolume, soundVolume);
		return prefs;
	}

}
