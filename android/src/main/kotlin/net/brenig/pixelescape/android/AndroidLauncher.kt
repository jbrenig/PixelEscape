package net.brenig.pixelescape.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle

import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameConfiguration
import de.golfgl.gdxgamesvcs.GpgsClient

/** Launches the Android application. */
class AndroidLauncher : AndroidApplication() {
    lateinit var configuration : AndroidConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.useImmersiveMode = true // Recommended, but not required.
        config.useAccelerometer = false
        config.useCompass = false
        configuration = AndroidConfiguration(this)
        initialize(PixelEscape(configuration), config)
    }

    class AndroidConfiguration(val activity: AndroidApplication) : GameConfiguration() {
        override val gameService by lazy {
            GpgsClient()
                .setGpgsAchievementIdMapper(PlayServicesMapper.Companion::mapAchievement)
                .setGpgsLeaderboardIdMapper(PlayServicesMapper.Companion::mapLeaderboard)!!
        }
        override val gameServiceAvailable = true
        override val canQuitGame = false

        override val debugSettingsAvailable = BuildConfig.DEBUG

        override fun initGameServices() {
            gameService.initialize(activity, false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        configuration.gameService.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
