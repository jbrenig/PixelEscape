package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;

/**
 * Contains information about user preferences and handles loading and saving of those
 */
public class GameSettings {


	private static final class Keys {
		public static final String musicEnabled = "ENABLE_MUSIC";
		public static final String soundEnabled = "ENABLE_SOUNDS";
		public static final String musicVolume = "VOLUME_MUSIC";
		public static final String soundVolume = "VOLUME_SOUND";
		public static final String shortCountdown = "SHORT_COUNTDOWN";
		public static final String highscoreInWorld = "HIGHSCORE_INWORLD";
	}

	private static final class Defaults {
		private static final boolean musicEnabled = true;
		private static final boolean soundEnabled = true;
		private static final float musicVolume = 0.5F;
		private static final float soundVolume = 0.5F;
		private static final boolean shortCountdown = false;
		private static final boolean highscoreInWorld = true;
	}

	private static final String PREF_MAIN_SETTINGS = "PixelEscape_User_Preferences";

	private final Preferences prefs;

	/**
	 * fullscreen, setting does not persist atm
	 */
	public boolean fullscreen = false;

	public GameSettings() {
		prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS);
	}

	public void saveToDisk() {
		prefs.flush();
		LogHelper.log("GameSettings", "Settings saved!");
	}

	@SuppressWarnings("ConstantConditions")
	public boolean isMusicEnabled() {
		return Reference.ENABLE_MUSIC && prefs.getBoolean(Keys.musicEnabled, Defaults.musicEnabled);
	}

	public void setMusicEnabled(boolean musicEnabled) {
		prefs.putBoolean(Keys.musicEnabled, musicEnabled);
		saveToDisk();
	}

	public boolean isSoundEnabled() {
		return prefs.getBoolean(Keys.soundEnabled, Defaults.soundEnabled);
	}

	public void setSoundEnabled(boolean soundEnabled) {
		prefs.putBoolean(Keys.soundEnabled, soundEnabled);
		saveToDisk();
	}

	public float getMusicVolume() {
		return prefs.getFloat(Keys.musicVolume, Defaults.musicVolume);
	}

	public void setMusicVolume(float musicVolume) {
		prefs.putFloat(Keys.musicVolume, musicVolume);
//		saveToDisk();
	}

	public float getSoundVolume() {
		return prefs.getFloat(Keys.soundVolume, Defaults.soundVolume);
	}

	public void setSoundVolume(float soundVolume) {
		prefs.putFloat(Keys.soundVolume, soundVolume);
//		saveToDisk();
	}


	public boolean shortCountdownEnabled() {
		return prefs.getBoolean(Keys.shortCountdown, Defaults.shortCountdown);
	}

	public void setCountdownEnabled(boolean countdownEnabled) {
		prefs.putBoolean(Keys.shortCountdown, countdownEnabled);
	}


	public boolean showHighScoreInWorld() {
		return prefs.getBoolean(Keys.highscoreInWorld, Defaults.highscoreInWorld);
	}

	public void setShowHighScoreInWorld(boolean b) {
		prefs.putBoolean(Keys.highscoreInWorld, b);
	}
}
