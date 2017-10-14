package net.brenig.pixelescape.screen

import com.badlogic.gdx.Screen
import net.brenig.pixelescape.PixelEscape

/**
 * Abstract GameScreen providing shared functionality for all Screens of PixelEscape
 */
abstract class PixelScreen(val game: PixelEscape) : Screen {

    open fun updateMusic(play: Boolean) {
        if (play) {
            game.gameMusic.play(true)
        }
    }
}
