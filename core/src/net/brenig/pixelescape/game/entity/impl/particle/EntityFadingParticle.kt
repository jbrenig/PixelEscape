package net.brenig.pixelescape.game.entity.impl.particle

import com.badlogic.gdx.graphics.Color
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.lib.utils.AnimationUtils
import net.brenig.pixelescape.render.WorldRenderer

class EntityFadingParticle : Entity() {

    private var xVel = 0f
    private var yVel = 0f

    private var fadeDuration = 0.5f
    private var fadeTimePassed = 0f

    private var xAccelerationFactor = 1f
    private var yAccelerationFactor = 1f

    private var colorRed = 0f
    private var colorGreen = 0f
    private var colorBlue = 0f

    override val isDead: Boolean
        get() = fadeTimePassed >= fadeDuration

    fun setColor(colorRed: Float, colorGreen: Float, colorBlue: Float) {
        this.colorRed = colorRed
        this.colorGreen = colorGreen
        this.colorBlue = colorBlue
    }

    fun setColor(color: Color) {
        setColor(color.r, color.g, color.b)
    }

    fun setFadeDuration(fadeDuration: Float) {
        this.fadeDuration = fadeDuration
    }

    fun setAccelerationFactor(xAcc: Float, yAcc: Float) {
        this.xAccelerationFactor = xAcc
        this.yAccelerationFactor = yAcc
    }

    fun setVelocity(xVel: Float, yVel: Float) {
        this.xVel = xVel
        this.yVel = yVel
    }

    override fun render(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        if (isDead) {
            return
        }
        //Move
        xPos += xVel * delta
        yPos += yVel * delta

        xVel = Math.min(gameMode.maxEntitySpeed, xVel * xAccelerationFactor)
        yVel = Math.min(gameMode.maxEntitySpeed, yVel * yAccelerationFactor)

        fadeTimePassed += delta

        val currentAlpha = 1 - AnimationUtils.easeInAndOut(fadeTimePassed, fadeDuration)

        game.renderManager.begin()
        game.renderManager.setColor(colorRed, colorGreen, colorBlue, currentAlpha)
        renderer.renderRectWorld(xPos - radius, yPos - radius, size.toFloat(), size.toFloat())
    }


    override fun reset() {
        super.reset()
        colorRed = 0f
        colorGreen = 0f
        colorBlue = 0f
        xVel = 0f
        yVel = 0f
        fadeDuration = 0.5f
        fadeTimePassed = 0f
        xAccelerationFactor = 1f
        yAccelerationFactor = 1f
    }

    companion object {

        private const val size = 4
        private const val radius = size / 2
    }
}
