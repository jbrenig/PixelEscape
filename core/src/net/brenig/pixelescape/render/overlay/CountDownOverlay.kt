package net.brenig.pixelescape.render.overlay

import net.brenig.pixelescape.lib.utils.Utils
import net.brenig.pixelescape.lib.error
import net.brenig.pixelescape.screen.GameScreen

/**
 * Overlay that renders a simple countdown (from 3)<br></br>
 * pauses the game until countdown is finished
 */
class CountDownOverlay(screen: GameScreen) : Overlay(screen) {

    private val isShort: Boolean = screen.game.gameSettings.shortCountdownEnabled
    private var startedAt: Long = 0

    override fun show() {
        super.show()
        startedAt = System.currentTimeMillis()
    }

    override fun render(delta: Float) {
        val timePassed = if (screen.isScreenPaused) 0 else System.currentTimeMillis() - startedAt
        val secondsPassed = timePassed / 1000L
        val secondsRemaining = if (isShort) 1 - secondsPassed else COUNT_FROM - secondsPassed
        if (secondsRemaining <= -1L) {
            //end
            screen.resetToEmptyOverlay()
            return
        }

        var fractionOfCurrentSecond = (timePassed % 1000L).toInt() + 1 //fraction of the current second
        if (fractionOfCurrentSecond <= 0) {
            error("Unknown error when calculating passed time!!, / by zero")
            error("timePassed: " + timePassed)
            error("secondsPassed: " + secondsPassed)
            error("secondsRemaining: " + secondsRemaining)
            error("fractionOfCurrentSecond: " + fractionOfCurrentSecond)
            //restore time
            startedAt = System.currentTimeMillis()
            fractionOfCurrentSecond = 1
        }
        var fontScale = 5f
        var alpha = 1f
        if (fractionOfCurrentSecond < 200) {
            // big "entry" font scale (15 to 5)
            val anim = Utils.easeOut(fractionOfCurrentSecond.toFloat(), 200f)
            fontScale = 15 - anim * 10
            alpha = 0.75f + anim / 4f
        } else if (fractionOfCurrentSecond > 700) {
            // small "vanish" font scale (5 to 2)
            val anim = Utils.easeIn((fractionOfCurrentSecond - 700).toFloat(), 300f)
            fontScale = 5 - anim * 3
            alpha = 1 - anim / 2f
        }
        if (isShort) {
            fontScale *= 0.7f
        }

        screen.game.renderManager.begin()
        screen.game.font.setColor(0.2f, 0.8f, 0f, alpha)
        screen.game.font.data.setScale(fontScale)

        if (secondsRemaining <= 0) {
            screen.fontLayout.setText(screen.game.font, GO_TEXT)
        } else if (!isShort) {
            screen.fontLayout.setText(screen.game.font, "" + secondsRemaining)
        } else {
            screen.fontLayout.setText(screen.game.font, READY_TEXT)
        }

        val xPos = screen.world.worldWidth / 2 - screen.fontLayout.width / 2
        val yPos = (screen.world.worldHeight / 2).toFloat() + screen.fontLayout.height / 2 + screen.uiPos.toFloat()

        screen.game.font.draw(screen.game.batch, screen.fontLayout, xPos, yPos)
    }

    override fun doesPauseGame(): Boolean {
        return COUNT_FROM - (System.currentTimeMillis() - startedAt).toInt() / 1000 > 0
    }

    override fun shouldHideGameUI(): Boolean {
        return false
    }

    override fun shouldPauseOnEscape(): Boolean {
        return true
    }

    companion object {

        private const val COUNT_FROM = 3
        private const val GO_TEXT = "GO!"
        /**
         * only used for short countdown
         */
        private const val READY_TEXT = "Ready!"
    }
}
