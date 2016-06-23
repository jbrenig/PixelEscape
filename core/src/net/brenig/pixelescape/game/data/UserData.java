package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Names;
import net.brenig.pixelescape.lib.Reference;

/**
 * Used to store user data, such as highscore
 */
public class UserData {

	private static final class Keys {
		public static final String savesRevision = "data_version";

		public static final String highScore = "highscore";
		public static final String gameMode = "gamemode";

		public static final String tutorial = "tutorial";
	}

	private static final class Defaults {
		private static final int highScore = 0;
		private static final int gameMode = 0;
		private static final boolean tutorial = false;
	}

	private static final String PREF_MAIN_DATA = "PixelEscape_User_Data";


	private final Preferences prefs;

	public UserData() {
		prefs = Gdx.app.getPreferences(PREF_MAIN_DATA);
	}

	public void saveToDisk() {
		prefs.flush();
		LogHelper.log("UserData", "User progress saved!");
	}

	/**
	 * updates the HighScore
	 * @param gameMode the current GameMode
	 * @param score the new score
	 * @return true if the score got updated (--> new highscore)
	 */
	public boolean updateHighscore(GameMode gameMode, int score) {
		int lastHighScore = getHighScore(gameMode);
		if(score > lastHighScore) {
			setHighScore(gameMode, score);
			return true;
		}
		return false;
	}

	/**
	 * @param gameMode the current GameMode
	 * @return current HighScore for the given GameMode
	 */
	public int getHighScore(GameMode gameMode) {
		return prefs.getInteger(Keys.highScore + gameMode.getScoreboardName(), Defaults.highScore);
	}

	/**
	 * Sets the new HighScore for the given GameMode
	 */
	public void setHighScore(GameMode gameMode, int highScore) {
		prefs.putInteger(Keys.highScore + gameMode.getScoreboardName(), highScore);
		saveToDisk();
	}


	/**
	 * @return the latest selected gamemode
	 */
	public int getLastGameMode() {
		return prefs.getInteger(Keys.gameMode, Defaults.gameMode);
	}

	public void setLastGameMode(int id) {
		prefs.putInteger(Keys.gameMode, id);
		saveToDisk();
	}

	public boolean tutorialSeen(GameMode gameMode) {
		if(GameDebugSettings.get("FORCE_TUTORIALS")) {
			return true;
		}
		//noinspection PointlessBooleanExpression,ConstantConditions
		return Reference.SUSPRESS_TUTORIALS || prefs.getBoolean(Keys.tutorial + gameMode.getGameModeName(), Defaults.tutorial);
	}

	public void setTutorialSeen(GameMode gameMode, boolean value) {
		prefs.putBoolean(Keys.tutorial + gameMode.getGameModeName(), value);
	}

	/**
	 * updates exsisting data to new format if necessary
	 */
	public void updateSaveGames() {
		int savedDataVersion = prefs.getInteger(Keys.savesRevision, -1);
		if(savedDataVersion < Reference.PREFS_REVISION) {
			LogHelper.log("UserData", "converting old save data!");
			switch (savedDataVersion) {
				case -1:
					int oldHighScore = prefs.getInteger(Keys.highScore, Defaults.highScore);
					prefs.putInteger(Keys.highScore + Names.SCOREBOARD_CLASSIC, oldHighScore);
				case 1:
					prefs.putInteger(Keys.highScore + Names.SCOREBOARD_ARCADE, 0);
			}
			prefs.putInteger(Keys.savesRevision, Reference.PREFS_REVISION);
			saveToDisk();
		}
	}

}
