package net.brenig.pixelescape.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.lib.log

/**
 * Contains information about user preferences and handles loading and saving of those
 */
class GameSettings {

    private val prefs: Preferences

    /**
     * fullscreen, setting does not persist atm
     */
    var fullscreen = false

    var isMusicEnabled: Boolean
        get() = Reference.ENABLE_MUSIC && prefs.getBoolean(Keys.musicEnabled, Defaults.musicEnabled)
        set(musicEnabled) {
            prefs.putBoolean(Keys.musicEnabled, musicEnabled)
            saveToDisk()
        }

    var isSoundEnabled: Boolean
        get() = prefs.getBoolean(Keys.soundEnabled, Defaults.soundEnabled)
        set(soundEnabled) {
            prefs.putBoolean(Keys.soundEnabled, soundEnabled)
            saveToDisk()
        }

    var musicVolume: Float
        get() = prefs.getFloat(Keys.musicVolume, Defaults.musicVolume)
        set(musicVolume) {
            prefs.putFloat(Keys.musicVolume, musicVolume)
        }

    var soundVolume: Float
        get() = prefs.getFloat(Keys.soundVolume, Defaults.soundVolume)
        set(soundVolume) {
            prefs.putFloat(Keys.soundVolume, soundVolume)
        }

    var shortCountdownEnabled: Boolean
        get() = prefs.getBoolean(Keys.shortCountdown, Defaults.shortCountdown)
        set(countdownEnabled) { prefs.putBoolean(Keys.shortCountdown, countdownEnabled) }

    var showHighscoreInWorld: Boolean
        get() = prefs.getBoolean(Keys.highscoreInWorld, Defaults.highscoreInWorld)
        set(b) { prefs.putBoolean(Keys.highscoreInWorld, b) }

    init {
        prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS)
    }

    fun saveToDisk() {
        prefs.flush()
        log("GameSettings", "Settings saved!")
    }

    private object Keys {
        const val musicEnabled = "ENABLE_MUSIC"
        const val soundEnabled = "ENABLE_SOUNDS"
        const val musicVolume = "VOLUME_MUSIC"
        const val soundVolume = "VOLUME_SOUND"
        const val shortCountdown = "SHORT_COUNTDOWN"
        const val highscoreInWorld = "HIGHSCORE_INWORLD"
    }

    private object Defaults {
        const val musicEnabled = true
        const val soundEnabled = true
        const val musicVolume = 0.5f
        const val soundVolume = 0.5f
        const val shortCountdown = false
        const val highscoreInWorld = true
    }

    companion object {
        private const val PREF_MAIN_SETTINGS = "PixelEscape_User_Preferences"
    }
}
