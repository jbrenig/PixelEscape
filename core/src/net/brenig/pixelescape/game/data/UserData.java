package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Used to store user data, such as highscore
 */
public class UserData {


	private static final class Keys {
		public static final String highScore = "highscore";
	}

	private static final class Defaults {
		private static final int highScore = 0;
	}

	private static final String PREF_MAIN_DATA = "PixelEscape_User_Data";

	private int lastHighScore;

	private final Preferences prefs;

	public UserData() {
		prefs = Gdx.app.getPreferences(PREF_MAIN_DATA);
		lastHighScore = getHighScore();
	}

	public void saveToDisk() {
		prefs.flush();
	}

	public boolean updateHighscore(int score) {
		int highScore = getHighScore();
		if(score >= highScore) {
			lastHighScore = highScore;
			setHighScore(score);
			return highScore > lastHighScore;
		}
		return false;
	}

	public int getHighScore() {
		return prefs.getInteger(Keys.highScore, Defaults.highScore);
	}

	public void setHighScore(int highScore) {
		prefs.putInteger(Keys.highScore, highScore);
		saveToDisk();
	}

}
