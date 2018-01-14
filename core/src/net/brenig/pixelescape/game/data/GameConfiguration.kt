package net.brenig.pixelescape.game.data

import de.golfgl.gdxgamesvcs.IGameServiceClient
import de.golfgl.gdxgamesvcs.NoGameServiceClient
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.lib.gameservices.DebugGameServiceClient

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
     * @return whether this platform has a cursor that can be hidden
     */
    open val canHideCursor = false

    /**
     * @return whether debug settings should be enabled
     */
    open val debugSettingsAvailable = Reference.DEBUG_SETTINGS_AVAILABLE

    open val loggingEnabled = true

    @Suppress("ConstantConditionIf")
    open val gameService : IGameServiceClient = if (Reference.DEBUG_SETTINGS_AVAILABLE) DebugGameServiceClient() else NoGameServiceClient()

    open val gameServiceAvailable = Reference.DEBUG_SETTINGS_AVAILABLE

    open fun initGameServices() {}

    open val musicAvailable = Reference.ENABLE_MUSIC
}
