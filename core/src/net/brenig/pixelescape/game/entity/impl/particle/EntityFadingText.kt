package net.brenig.pixelescape.game.entity.impl.particle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.lib.utils.AnimationUtils
import net.brenig.pixelescape.render.WorldRenderer

/**
 * Entity that renders a given string for a given duration (default: 0.6F)
 */
class EntityFadingText : Entity() {

    private var fadeDuration: Float = 0.toFloat()
    private var timePassed: Float = 0.toFloat()
    private var text: String? = null

    private var colorRed = 0f
    private var colorGreen = 0f
    private var colorBlue = 0f


    override val isDead: Boolean
        get() = timePassed > fadeDuration || super.isDead


    fun setColor(colorRed: Float, colorGreen: Float, colorBlue: Float) {
        this.colorRed = colorRed
        this.colorGreen = colorGreen
        this.colorBlue = colorBlue
    }

    fun setColor(color: Color) {
        setColor(color.r, color.g, color.b)
    }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        renderer.renderManager.begin()


        Gdx.gl.glEnable(GL20.GL_BLEND)
        val currentAlpha = 1 - AnimationUtils.easeOut(timePassed, fadeDuration, 2)
        renderer.renderManager.font.setColor(colorRed, colorGreen, colorBlue, currentAlpha)
        renderer.renderManager.setFontScale(0.5f)
        renderer.renderTextWorld(text!!, xPos, yPos)

        timePassed += delta
    }

    /**
     * sets the text to be displayed
     */
    fun setText(text: String) {
        this.text = text
    }

    /**
     * sets lifetime of this entity
     *
     * @param fadeDuration time in seconds
     */
    fun setFadeDuration(fadeDuration: Float) {
        this.fadeDuration = fadeDuration
    }

    /**
     * sets the text to be displayed and life time of this entity
     *
     * @param text         text to be displayed
     * @param fadeDuration time in seconds
     * @see .setFadeDuration
     */
    fun setText(text: String, fadeDuration: Float) {
        this.text = text
        this.fadeDuration = fadeDuration
    }

    override fun reset() {
        text = null
        timePassed = 0f
        fadeDuration = 0.6f
        setColor(Color.DARK_GRAY)
    }
}
