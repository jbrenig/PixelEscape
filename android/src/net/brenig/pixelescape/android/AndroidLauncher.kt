package net.brenig.pixelescape.android

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import de.golfgl.gdxgamesvcs.GpgsClient
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameConfiguration

class AndroidLauncher : AndroidApplication() {
    lateinit var configuration : AndroidConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val config = AndroidApplicationConfiguration()
        config.useAccelerometer = false
        config.useCompass = false
        configuration = AndroidConfiguration(this)
        initialize(PixelEscape(configuration), config)
    }

    class AndroidConfiguration(activity: Activity) : GameConfiguration() {
        override val gameService = createGameService(activity)
        override val gameServiceAvailable = true

        override val useBiggerButtons = true

        override val debugSettingsAvailable = BuildConfig.DEBUG

        private fun createGameService(activity: Activity) : GpgsClient {
            return GpgsClient()
                    .setGpgsAchievementIdMapper(PlayServicesMapper::mapAchievement)
                    .setGpgsLeaderboardIdMapper(PlayServicesMapper::mapLeaderboard)
                    .initialize(activity, false)!!
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        configuration.gameService.onGpgsActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}
