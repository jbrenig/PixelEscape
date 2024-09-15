package net.brenig.pixelescape.render.overlay

import net.brenig.pixelescape.game.data.GameDebugSettings
import net.brenig.pixelescape.screen.GameScreen

/**
 * Empty Overlay used to avoid null in GameScreen
 */
class EmptyOverlay(screen: GameScreen) : Overlay(screen) {

    override fun show() {
        screen.resetInputManager()
    }

    override fun render(delta: Float) {}

    override fun pause() {
        screen.showGamePausedOverlay()
    }

    override fun resume() {
        screen.showGamePausedOverlay()
    }

    override fun onResize(width: Int, height: Int) {
        if (screen.isInitialized && GameDebugSettings["AUTO_PAUSE"]) {
            screen.showGamePausedOverlay()
        }
    }

    override fun shouldHideGameUI(): Boolean {
        return false
    }

    override fun shouldPauseOnEscape(): Boolean {
        return true
    }
}
