package net.brenig.pixelescape.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.lib.log
import java.util.*

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

    var musicVolume: Float = Defaults.musicVolume
        set(musicVolume) {
            field = musicVolume
            prefs.putFloat(Keys.musicVolume, musicVolume)
        }

    var soundVolume: Float = Defaults.soundVolume
        set(soundVolume) {
            field = soundVolume
            prefs.putFloat(Keys.soundVolume, soundVolume)
        }

    var shortCountdownEnabled: Boolean
        get() = prefs.getBoolean(Keys.shortCountdown, Defaults.shortCountdown)
        set(countdownEnabled) { prefs.putBoolean(Keys.shortCountdown, countdownEnabled) }

    var showHighscoreInWorld: Boolean
        get() = prefs.getBoolean(Keys.highscoreInWorld, Defaults.highscoreInWorld)
        set(b) { prefs.putBoolean(Keys.highscoreInWorld, b) }

    var language: String
        get() = prefs.getString(Keys.language, Defaults.language)
        set(language) { prefs.putString(Keys.language, language)}

    init {
        prefs = Gdx.app.getPreferences(PREF_MAIN_SETTINGS)

        musicVolume = prefs.getFloat(Keys.musicVolume, Defaults.musicVolume)
        soundVolume = prefs.getFloat(Keys.soundVolume, Defaults.soundVolume)
    }

    fun getLanguageWithDefault(defLanguage: Locale): Locale {
        val langStr = prefs.getString(Keys.language, defLanguage.language)
        return Locale(langStr)
    }

    fun setLanguage(value: Locale) {
        language = value.language
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
        const val language = "LANGUAGE"
    }

    private object Defaults {
        const val musicEnabled = true
        const val soundEnabled = true
        const val musicVolume = 0.5f
        const val soundVolume = 0.5f
        const val shortCountdown = false
        const val highscoreInWorld = true
        const val language = "en"
    }

    companion object {
        private const val PREF_MAIN_SETTINGS = "PixelEscape_User_Preferences"
    }
}
