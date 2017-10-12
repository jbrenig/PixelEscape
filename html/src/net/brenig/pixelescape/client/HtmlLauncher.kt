package net.brenig.pixelescape.client

import com.badlogic.gdx.ApplicationListener
import com.badlogic.gdx.backends.gwt.GwtApplication
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration

import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameConfiguration
import net.brenig.pixelescape.lib.Reference

class HtmlLauncher : GwtApplication() {

    override fun getConfig(): GwtApplicationConfiguration {
        return GwtApplicationConfiguration(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y)
    }

    override fun createApplicationListener(): ApplicationListener {
        return PixelEscape(HtmlConfig())
    }

    class HtmlConfig : GameConfiguration() {
        override val canQuitGame = false
        override val canGoFullScreen = true
        override val canHideCursor = false
        override val loggingEnabled = Reference.DEBUG_SETTINGS_AVAILABLE
    }
}