package net.brenig.pixelescape.desktop

import com.badlogic.gdx.backends.lwjgl.LwjglApplication
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration

import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameConfiguration
import net.brenig.pixelescape.game.data.constants.Reference

object DesktopLauncher {
    @JvmStatic
    fun main(arg: Array<String>) {
        val config = LwjglApplicationConfiguration()
        config.title = "PixelEscape"
        config.width = Reference.TARGET_RESOLUTION_X
        config.height = Reference.TARGET_RESOLUTION_Y
        config.foregroundFPS = 0
        LwjglApplication(PixelEscape(DesktopConfiguration()), config)
    }

    class DesktopConfiguration : GameConfiguration() {
        override val canGoFullScreen = true
        override val canHideCursor = true
    }
}
