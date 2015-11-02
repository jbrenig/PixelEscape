package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * Created by Jonas Brenig on 20.10.2015.
 */
public class UserData {

	public int lastHighScore;

	private static final class Keys {
		public static final String highScore = "highscore";
	}

	private static final class Defaults {
		public static final int highScore = 0;
	}

	public static final String PREF_MAIN_DATA = "PixelEscape_User_Data";

	public int highScore = Defaults.highScore;

	public UserData() {

	}

	public Preferences loadFromDisk() {
		Preferences prefs = Gdx.app.getPreferences(PREF_MAIN_DATA);

		//Sound
		lastHighScore = highScore = prefs.getInteger(Keys.highScore, Defaults.highScore);
		return prefs;
	}

	public void saveToDisk() {
		save().flush();
	}

	public Preferences save() {
		Preferences prefs = Gdx.app.getPreferences(PREF_MAIN_DATA);

		//Sound
		prefs.putInteger(Keys.highScore, highScore);
		return prefs;
	}

	public boolean updateHighscore(int score) {
		if(score > highScore) {
			lastHighScore = highScore;
			highScore = score;
			saveToDisk();
			return true;
		}
		return false;
	}
}
