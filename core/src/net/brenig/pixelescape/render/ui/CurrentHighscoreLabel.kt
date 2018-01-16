package net.brenig.pixelescape.render.ui

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.ui.Widget
import com.badlogic.gdx.utils.Align
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.lib.LangKeys
import net.brenig.pixelescape.lib.error
import net.brenig.pixelescape.lib.translate
import net.brenig.pixelescape.lib.utils.AnimationUtils
import java.util.*

/**
 * Label that displays current highscore (animated, no frame)
 */
class CurrentHighscoreLabel(private var gameMode: GameMode?) : Widget() {

    private val fontLayout: GlyphLayout
    private val game: PixelEscape = PixelEscape.INSTANCE

    private var state: Animations = Animations.WAIT

    private var animationTimer = 0f
    private var animationDuration = 0f
    private var animationData = 0

    private var text: String? = null

    init {
        state = Animations.WAIT
        updateText()
        fontLayout = GlyphLayout(game.font, text)
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        //validate layout
        super.draw(batch, parentAlpha)

        if (animationTimer >= animationDuration) {
            updateAnimation()
        }

        val oldFontSizeX = game.font.scaleX
        val oldFontSizeY = game.font.scaleY
        var fontSizeX = font_size_x
        var fontSizeY = font_size_y
        var offsetX = 0f
        var offsetY = 0f
        var alpha = 1f
        when (state) {
            CurrentHighscoreLabel.Animations.BLEND -> {
                val part = animationDuration / 3
                alpha = when {
                    animationTimer < part -> 1 - AnimationUtils.easeOut(animationTimer, part, 2)
                    animationTimer < part * 2 -> 0f
                    else -> AnimationUtils.easeInAndOut(animationTimer - part * 2, part)
                }
                Gdx.gl.glEnable(GL20.GL_BLEND)
            }
            CurrentHighscoreLabel.Animations.MOVE_X -> offsetX = (Math.sin((animationTimer / animationDuration).toDouble() * animationData.toDouble() * Math.PI * 2.0) * 10f).toFloat()
            CurrentHighscoreLabel.Animations.MOVE_Y -> offsetY = (Math.sin((animationTimer / animationDuration).toDouble() * animationData.toDouble() * Math.PI * 2.0) * 10f).toFloat()
            CurrentHighscoreLabel.Animations.WAIT -> {
            }
            CurrentHighscoreLabel.Animations.SIZE -> {
                val part = animationTimer / animationDuration
                if (part < 0.5) {
                    val ease = AnimationUtils.easeInAndOut(part, 0.5f) * font_scaling_strength
                    fontSizeX = font_size_x - ease * font_size_x
                    fontSizeY = font_size_y - ease * font_size_y
                } else {
                    val ease = AnimationUtils.easeInAndOut(part - 0.5f, 0.5f) * font_scaling_strength
                    fontSizeX = font_size_x - font_scaling_strength * font_size_x + ease * font_size_x
                    fontSizeY = font_size_y - font_scaling_strength * font_size_y + ease * font_size_y
                }
            }
            CurrentHighscoreLabel.Animations.GM_BLEND_IN -> {
                if (animationTimer == 0f) {
                    updateText()
                }
                alpha = animationTimer / animationDuration
                Gdx.gl.glEnable(GL20.GL_BLEND)
            }
            CurrentHighscoreLabel.Animations.GM_BLEND_OUT -> {
                alpha = 1 - animationTimer / animationDuration
                Gdx.gl.glEnable(GL20.GL_BLEND)
            }
        }
        //Score text
        setColor(0f, 0f, 0f, alpha)
        if (fontSizeX <= 0) {
            error("Invalid text scale in score widget animation")
            fontSizeX = font_size_x
        }
        if (fontSizeY <= 0) {
            error("Invalid text scale in score widget animation")
            fontSizeY = font_size_y
        }
        game.font.data.setScale(fontSizeX, fontSizeY)
        fontLayout.setText(game.font, text!!, color, 0f, Align.center, false)
        game.font.draw(batch, fontLayout, x + padding_side + offsetX + width / 2, y + fontLayout.height + padding_height + offsetY)

        val delta = Gdx.graphics.deltaTime
        animationTimer += delta
        //reset font size
        game.font.data.setScale(oldFontSizeX, oldFontSizeY)

    }

    fun setGameMode(mode: GameMode) {
        gameMode = mode
        if (state == Animations.GM_BLEND_IN) {
            state = Animations.GM_BLEND_OUT
        } else if (state != Animations.GM_BLEND_OUT) {
            state = Animations.GM_BLEND_OUT
            animationTimer = 0f
            animationData = 0
            animationDuration = state.minDuration
        }
    }

    private fun updateText() {
        text = LangKeys.MainMenu.HIGHSCORE.translate(game.userData.getHighScore(gameMode!!))
    }

    private fun updateAnimation() {
        @Suppress("LiftReturnOrAssignment", "CascadeIf")
        if (state == Animations.GM_BLEND_OUT) {
            state = Animations.GM_BLEND_IN
        } else if (PixelEscape.rand.nextInt(10) < 4) {
            state = Animations.values()[PixelEscape.rand.nextInt(Animations.values().size)]
        } else {
            state = Animations.WAIT
        }
        animationTimer = 0f
        animationData = 0
        animationDuration = state.getDuration(PixelEscape.rand)

        when (state) {
            CurrentHighscoreLabel.Animations.MOVE_X -> animationData = 1 + PixelEscape.rand.nextInt(4)
            CurrentHighscoreLabel.Animations.MOVE_Y -> animationData = 1 + PixelEscape.rand.nextInt(3)
            CurrentHighscoreLabel.Animations.BLEND -> {
                //				Gdx.gl.glDisable(GL20.GL_BLEND);
            }
            else -> { }
        }
    }

    override fun getPrefWidth(): Float {
        val oldFontSizeX = game.font.scaleX
        val oldFontSizeY = game.font.scaleY
        game.font.data.setScale(font_size_x, font_size_y)
        val v = fontLayout.width + padding_side * 2
        game.font.data.setScale(oldFontSizeX, oldFontSizeY)
        return v

    }

    override fun getPrefHeight(): Float {
        val oldFontSizeX = game.font.scaleX
        val oldFontSizeY = game.font.scaleY
        game.font.data.setScale(font_size_x, font_size_y)
        val v = fontLayout.height + padding_height * 2
        game.font.data.setScale(oldFontSizeX, oldFontSizeY)
        return v
    }

    private enum class Animations(val minDuration: Float, private val maxDuration: Float) {
        WAIT(2F, 8F), BLEND(0.4f, 2f), MOVE_X(0.4f, 4f), MOVE_Y(0.4f, 8F), SIZE(0.5f, 0.5f), GM_BLEND_OUT(0.5f, 0.5f), GM_BLEND_IN(0.5f, 0.5f);

        fun getDuration(random: Random): Float {
            return minDuration + random.nextFloat() * (maxDuration - minDuration)
        }
    }

    companion object {
        private const val padding_side = 4f
        private const val padding_height = 4f

        private const val font_size_x = 1f
        private const val font_size_y = 1f

        private const val font_scaling_strength = 0.5f
    }
}
