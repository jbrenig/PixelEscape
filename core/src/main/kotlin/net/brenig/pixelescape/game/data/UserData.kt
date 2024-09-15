package net.brenig.pixelescape.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.data.constants.ScoreboardNames
import net.brenig.pixelescape.lib.log

/**
 * Used to store user data, such as highscore
 */
class UserData {

    private val prefs: Preferences


    /**
     * @return the latest selected gamemode
     */
    var lastGameMode: Int
        get() = prefs.getInteger(Keys.gameMode, Defaults.gameMode)
        set(id) {
            prefs.putInteger(Keys.gameMode, id)
            saveToDisk()
        }

    init {
        prefs = Gdx.app.getPreferences(PREF_MAIN_DATA)
    }

    fun saveToDisk() {
        prefs.flush()
        log("UserData", "User progress saved!")
    }

    /**
     * updates the HighScore
     *
     * @param gameMode the current GameMode
     * @param score    the new score
     * @return true if the score got updated (--> new highscore)
     */
    fun updateHighscore(gameMode: GameMode, score: Int): Boolean {
        val lastHighScore = getHighScore(gameMode)
        if (score > lastHighScore) {
            setHighScore(gameMode, score)
            return true
        }
        return false
    }

    /**
     * @param gameMode the current GameMode
     * @return current HighScore for the given GameMode
     */
    fun getHighScore(gameMode: GameMode): Int {
        return prefs.getInteger(Keys.highScore + gameMode.scoreboardName, Defaults.highScore)
    }

    /**
     * Sets the new HighScore for the given GameMode
     */
    fun setHighScore(gameMode: GameMode, highScore: Int) {
        prefs.putInteger(Keys.highScore + gameMode.scoreboardName, highScore)
        saveToDisk()
    }

    fun tutorialSeen(gameMode: GameMode): Boolean {
        return if (GameDebugSettings["FORCE_TUTORIALS"]) {
            true
        } else {
            !Reference.ENABLE_TUTORIALS || prefs.getBoolean(Keys.tutorial + gameMode.gameModeName, Defaults.tutorial)
        }
    }

    fun setTutorialSeen(gameMode: GameMode, value: Boolean) {
        prefs.putBoolean(Keys.tutorial + gameMode.gameModeName, value)
    }

    /**
     * updates existing data to new format if necessary
     */
    fun updateSaveGames() {
        val savedDataVersion = prefs.getInteger(Keys.savesRevision, -1)
        if (savedDataVersion < Reference.PREFS_REVISION) {
            log("UserData", "converting old save data!")
            when (savedDataVersion) {
                -1 -> {
                    val oldHighScore = prefs.getInteger(Keys.highScore, Defaults.highScore)
                    prefs.putInteger(Keys.highScore + ScoreboardNames.SCOREBOARD_CLASSIC, oldHighScore)
                    prefs.putInteger(Keys.highScore + ScoreboardNames.SCOREBOARD_ARCADE, 0)
                }
                1 -> prefs.putInteger(Keys.highScore + ScoreboardNames.SCOREBOARD_ARCADE, 0)
            }
            prefs.putInteger(Keys.savesRevision, Reference.PREFS_REVISION)
            saveToDisk()
        }
    }

    private object Keys {
        const val savesRevision = "data_version"

        const val highScore = "highscore"
        const val gameMode = "gamemode"

        const val tutorial = "tutorial"
    }

    private object Defaults {
        const val highScore = 0
        const val gameMode = 0
        const val tutorial = false
    }

    companion object {
        private const val PREF_MAIN_DATA = "PixelEscape_User_Data"
    }

}
