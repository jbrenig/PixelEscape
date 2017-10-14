package net.brenig.pixelescape.render.ui.ingame

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.screen.GameScreen

/**
 * Widget that displays current score<br></br>
 * gets draw on a white background texture
 */
class ScoreWidget(private val player: EntityPlayer, private val fontLayout: GlyphLayout, private val game: PixelEscape) : Widget() {
    private var lastScoreScreenWidth = 0f

    constructor(screen: GameScreen) : this(screen.world.getPlayer(), screen.fontLayout, screen.game) {}

    override fun draw(batch: Batch, parentAlpha: Float) {
        //validate layout
        super.draw(batch, parentAlpha)

        //Background
        batch.setColor(1f, 1f, 1f, 1f)
        game.buttonNinePatch.draw(batch, x, y, width, height)

        //Score text
        game.font.setColor(0f, 0f, 0f, 1f)
        setScoreText()
        game.font.draw(batch, fontLayout, x + width / 2 - fontLayout.width / 2, y + height / 2 + fontLayout.height / 2)
    }

    private fun setScoreText() {
        val score = SCORE_TEXT + player.score
        //		game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
        fontLayout.setText(game.font, score)
        if (fontLayout.width > lastScoreScreenWidth || lastScoreScreenWidth - fontLayout.width > Reference.GAME_UI_SCORE_SCREEN_SIZE_BUFFER) {
            lastScoreScreenWidth = fontLayout.width
            invalidateHierarchy()
        }
    }

    override fun getPrefWidth(): Float {
        setScoreText()
        return game.buttonNinePatch.padLeft + game.buttonNinePatch.padRight + lastScoreScreenWidth
    }

    override fun getPrefHeight(): Float {
        setScoreText()
        return game.buttonNinePatch.padBottom + game.buttonNinePatch.padTop + fontLayout.height
    }

    companion object {
        private const val SCORE_TEXT = "Score: "
    }
}
