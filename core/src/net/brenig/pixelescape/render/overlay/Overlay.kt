package net.brenig.pixelescape.render.overlay

import net.brenig.pixelescape.screen.GameScreen

/**
 * An Overlay is used to draw UI or similar on top of the main game
 */
abstract class Overlay(protected val screen: GameScreen) {

    /**
     * Called when the Overly gets displayed to the player
     */
    open fun show() {}

    /**
     * method to render effects on the world<br></br>
     * (gets called before game ui is rendered)
     *
     * @param delta time passed between frames in seconds
     */
    open fun renderFirst(delta: Float) {}

    /**
     * renders the overlay
     *
     * @param delta time passed between frames in seconds
     */
    abstract fun render(delta: Float)

    /**
     * Called when the game window is resized<br></br>
     * Use this to update UI-Elements
     *
     * @param width  new width
     * @param height new height
     */
    open fun onResize(width: Int, height: Int) {
        if (screen.isInitialized) {
            if (switchToPausedOverlayOnFocusChange()) {
                screen.showGamePausedOverlay()
            }
        }
    }

    /**
     * Called when the GameScreen gets paused
     */
    open fun pause() {
        if (switchToPausedOverlayOnFocusChange()) {
            screen.showGamePausedOverlay()
        }
    }

    /**
     * Gets called when the GameScreen gets resumed
     */
    open fun resume() {
        if (switchToPausedOverlayOnFocusChange()) {
            screen.showGamePausedOverlay()
        }
    }

    /**
     * Gets called when the Overlay is destroyed
     */
    open fun dispose() {}

    /**
     * @return true if the default game-ui should be hidden
     */
    open fun shouldHideGameUI(): Boolean {
        return true
    }

    /**
     * gets called every tick to allow dynamic changing of paused vs unpaused
     *
     * @return true if the game should be paused
     */
    open fun doesPauseGame(): Boolean {
        return false
    }

    /**
     * @return true if GameScreen should hide the cursor after time, (GameScreen InputManager needs focus)
     */
    open fun canHideCursor(): Boolean {
        return true
    }

    /**
     * Renders a black, transparent overlay
     */
    @JvmOverloads protected fun renderScreenTint(alpha: Float = 0.3f) {
        renderScreenTint(0f, 0f, 0f, alpha)
    }

    /**
     * Renders a coloured overlay in the given color
     */
    protected fun renderScreenTint(r: Float, g: Float, b: Float, a: Float) {
        screen.game.renderManager.begin()
        screen.game.renderManager.setColor(r, g, b, a)
        screen.game.renderManager.rect(0f, 0f, screen.game.gameSizeX.toFloat(), screen.game.gameSizeY.toFloat())
    }

    /**
     * called whenever the music status gets updated (might get called without anything changed)
     *
     * @param play whether the music is playing or not
     */
    open fun updateMusic(play: Boolean) {}

    /**
     * @return whether the game should open the game paused overlay when the escape key is pressed
     */
    open fun shouldPauseOnEscape(): Boolean {
        return false
    }

    protected open fun switchToPausedOverlayOnFocusChange(): Boolean {
        return false
    }
}
