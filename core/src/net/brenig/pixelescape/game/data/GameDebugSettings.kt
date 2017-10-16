package net.brenig.pixelescape.game.data

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import net.brenig.pixelescape.PixelEscape
import java.util.*

/**
 * Manages debug settings
 */
class GameDebugSettings {
    private val prefs: Preferences

    init {
        prefs = Gdx.app.getPreferences(PREF_DEBUG_SETTINGS)
    }

    fun saveToDisk() {
        prefs.flush()
    }

    fun getBoolean(s: String): Boolean {
        val def = defaults[s]
        if (!PixelEscape.INSTANCE.gameConfig.debugSettingsAvailable) {
            //return default if debug settings are deactivated
            return def == true
        }
        return if (def == null) {
            prefs.getBoolean(s)
        } else prefs.getBoolean(s, def)
    }

    fun setBoolean(s: String, b: Boolean) {
        prefs.putBoolean(s, b)
    }

    companion object {
        private const val PREF_DEBUG_SETTINGS = "PixelEscape_Debug_Preferences"

        private val defaults = createDefaults()

        private fun createDefaults(): Map<String, Boolean> {
            val result = HashMap<String, Boolean>()
            result.put("SHOW_FPS", false)
            result.put("AUTO_PAUSE", true)
            result.put("DEBUG_MODE_COORDS", false)
            result.put("DEBUG_WORLD_GEN_VALIDATE", false)
            result.put("DEBUG_SCREEN_BOUNDS", false)
            result.put("DEBUG_UI", false)
            result.put("DEBUG_LOGGING", false)
            result.put("DEBUG_GOD_MODE", false)
            result.put("DEBUG_MUSIC", false)
            result.put("SCREEN_SHAKE", true)
            result.put("FORCE_TUTORIALS", false)
            return Collections.unmodifiableMap(result)
        }

        operator fun get(s: String): Boolean {
            return PixelEscape.INSTANCE.gameDebugSettings.getBoolean(s)
        }

        operator fun set(s: String, b: Boolean) {
            PixelEscape.INSTANCE.gameDebugSettings.setBoolean(s, b)
        }
    }
}
