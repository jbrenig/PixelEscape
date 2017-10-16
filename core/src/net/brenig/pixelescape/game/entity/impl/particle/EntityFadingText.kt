package net.brenig.pixelescape.game.entity.impl.particle

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.lib.utils.Utils
import net.brenig.pixelescape.render.WorldRenderer

/**
 * Entity that renders a given string for a given duration (default: 0.6F)
 */
class EntityFadingText : Entity() {

    private var fadeDuration: Float = 0.toFloat()
    private var timePassed: Float = 0.toFloat()
    private var text: String? = null

    private var color_r = 0f
    private var color_g = 0f
    private var color_b = 0f


    override val isDead: Boolean
        get() = timePassed > fadeDuration || super.isDead


    fun setColor(color_r: Float, color_g: Float, color_b: Float) {
        this.color_r = color_r
        this.color_g = color_g
        this.color_b = color_b
    }

    fun setColor(color: Color) {
        setColor(color.r, color.g, color.b)
    }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        renderer.renderManager.begin()


        Gdx.gl.glEnable(GL20.GL_BLEND)
        val currentAlpha = 1 - Utils.easeOut(timePassed, fadeDuration, 2)
        renderer.renderManager.font.setColor(color_r, color_g, color_b, currentAlpha)
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
