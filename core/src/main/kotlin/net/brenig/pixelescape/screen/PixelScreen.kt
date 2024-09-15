package net.brenig.pixelescape.screen

import com.badlogic.gdx.Screen
import de.golfgl.gdxgamesvcs.IGameServiceListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.lib.LogLevel
import net.brenig.pixelescape.lib.log

/**
 * Abstract GameScreen providing shared functionality for all Screens of PixelEscape
 */
abstract class PixelScreen(val game: PixelEscape) : Screen, IGameServiceListener {

    open fun updateMusic(play: Boolean) {
        if (play) {
            game.gameMusic.play(true)
        }
    }

    override fun gsShowErrorToUser(et: IGameServiceListener.GsErrorType?, msg: String?, t: Throwable?) {
        log(LogLevel.WARNING, "GS", "Error: ${et.toString()} - $msg")
    }

    override fun gsOnSessionInactive() {
    }

    override fun gsOnSessionActive() {
    }
}
