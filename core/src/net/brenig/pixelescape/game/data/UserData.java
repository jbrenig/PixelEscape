package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by Jonas Brenig on 20.10.2015.
 */
public class UserData {


	private static final class Keys {
		public static final String highScore = "highscore";
	}

	private static final class Defaults {
		public static final int highScore = 0;
	}

	public static final String PREF_MAIN_DATA = "PixelEscape_User_Data";

	public int lastHighScore;

	private Preferences prefs;

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
