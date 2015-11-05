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

	private Preferences prefs;

	private boolean musicEnabled = Defaults.musicEnabled;
	private boolean soundEnabled = Defaults.soundEnabled;

	private float musicVolume = Defaults.soundVolume;
	private float soundVolume = Defaults.musicVolume;

	/**
	 * fullscreen, setting does not persist atm
	 */
	public boolean fullscreen = false;

	public GameSettings() {
		prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS);
	}

	public void saveToDisk() {
		prefs.flush();
	}

	public boolean isMusicEnabled() {
		return prefs.getBoolean(Keys.musicEnabled, Defaults.musicEnabled);
	}

	public void setMusicEnabled(boolean musicEnabled) {
		prefs.putBoolean(Keys.musicEnabled, musicEnabled);
	}

	public boolean isSoundEnabled() {
		return prefs.getBoolean(Keys.soundEnabled, Defaults.soundEnabled);
	}

	public void setSoundEnabled(boolean soundEnabled) {
		prefs.putBoolean(Keys.soundEnabled, soundEnabled);
	}

	public float getMusicVolume() {
		return prefs.getFloat(Keys.musicVolume, Defaults.musicVolume);
	}

	public void setMusicVolume(float musicVolume) {
		prefs.putFloat(Keys.musicVolume, musicVolume);
	}

	public float getSoundVolume() {
		return prefs.getFloat(Keys.soundVolume, Defaults.soundVolume);
	}

	public void setSoundVolume(float soundVolume) {
		prefs.putFloat(Keys.soundVolume, soundVolume);
	}

}
