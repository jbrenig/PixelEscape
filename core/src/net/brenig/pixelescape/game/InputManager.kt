package net.brenig.pixelescape.game

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.InputProcessor
import net.brenig.pixelescape.PixelEscape

/**
 * Handles Player input for the game
 */
class InputManager : InputProcessor {

    var isTouched = false
        private set
    var isSpaceDown = false
        private set
    var isEscapeDown = false
        private set

    /**
     * Cursor timer
     * -1 if idle
     * 0 if not idle
     * >0 if idle for n seconds
     */
    private var cursorIdleTimer = 0f

    var keyHandler : ((Char) -> (Unit))? = null

    fun updateMouseVisibility(delta: Float, canHide: Boolean) {
        if (canHide && cursorIdleTimer >= 0) {
            cursorIdleTimer += delta
        }
        if (cursorIdleTimer > cursorIdleTime) {
            cursorIdleTimer = -1f
            updateMouseVisibility()
        }
    }


    private fun updateMouseVisibility() {
        if (PixelEscape.INSTANCE.gameConfig.canHideCursor) {
            Gdx.input.isCursorCatched = cursorIdleTimer < 0
        }
    }

    fun resetMouseVisibility() {
        if (PixelEscape.INSTANCE.gameConfig.canHideCursor) {
            cursorIdleTimer = 0f
            Gdx.input.isCursorCatched = false
        }
    }

    override fun keyDown(keycode: Int): Boolean {
        var changed = false
        if (keycode == Input.Keys.ESCAPE) {
            isEscapeDown = true
            changed = true
        }
        if (keycode == Input.Keys.SPACE) {
            isSpaceDown = true
            changed = true
        }
        return changed
    }

    override fun keyUp(keycode: Int): Boolean {
        var changed = false
        if (keycode == Input.Keys.ESCAPE) {
            isEscapeDown = false
            changed = true
        }
        if (keycode == Input.Keys.SPACE) {
            isSpaceDown = false
            changed = true
        }
        return changed
    }

    override fun keyTyped(character: Char): Boolean {
        keyHandler?.invoke(character)
        return false
    }

    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        isTouched = true
        return true
    }

    override fun touchUp(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        isTouched = false
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        return isTouched
    }

    override fun mouseMoved(screenX: Int, screenY: Int): Boolean {
        cursorIdleTimer = 0f
        updateMouseVisibility()
        return false
    }

    override fun scrolled(amount: Int): Boolean {
        return false
    }

    fun resetTouchedState() {
        isTouched = false
        isSpaceDown = false
        isEscapeDown = false
    }

    fun refreshButtonState() {
        isTouched = Gdx.input.isTouched
        isSpaceDown = Gdx.input.isKeyPressed(Input.Keys.SPACE)
        isEscapeDown = Gdx.input.isKeyPressed(Input.Keys.ESCAPE)
    }

    companion object {
        private const val cursorIdleTime = 3f
    }
}
