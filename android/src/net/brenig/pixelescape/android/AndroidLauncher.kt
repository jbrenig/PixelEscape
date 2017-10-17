package net.brenig.pixelescape.android

import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration

import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameConfiguration

class AndroidLauncher : AndroidApplication() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false
        initialize(PixelEscape(AndroidConfiguration()), config)
    }

    class AndroidConfiguration : GameConfiguration() {
        override val useBiggerButtons = true

        override val debugSettingsAvailable = BuildConfig.DEBUG
    }
}
