package net.brenig.pixelescape.game.data

import net.brenig.pixelescape.lib.Reference

/**
 * Allows for platform specific game settings
 */
open class GameConfiguration {

    /**
     * @return the gamemodes playable on this platform
     */
    open val availableGameModes: List<GameMode> = GameMode.values().asList()

    /**
     * @return whether this platform should provide a button to quit the game
     */
    open val canQuitGame = true

    /**
     * @return whether this platform is able to switch to fullscreen
     */
    open val canGoFullScreen = false

    /**
     * @return whether this platform should use bigger buttons (eg. to be optimized for touchscreens)
     */
    open val useBiggerButtons = false

    /**
     * @return whether this platform has a cursor that can be hidden
     */
    open val canHideCursor = false

    /**
     * @return whether debug settings should be enabled
     */
    open val debugSettingsAvailable = Reference.DEBUG_SETTINGS_AVAILABLE

    open val loggingEnabled = true
}
