package net.brenig.pixelescape.game.player.effects

import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.render.GameRenderManager

/**
 * Base class for status effects that have a timer
 */
abstract class StatusEffectTimed(player: EntityPlayer, private val duration: Float) : StatusEffect(player) {
    protected var timeRemaining: Float = 0.toFloat()

    override val scaledTime: Float
        get() = timeRemaining / duration

    init {
        this.timeRemaining = duration
    }

    override fun update(delta: Float) {
        timeRemaining -= delta
    }

    override fun effectActive(): Boolean {
        return timeRemaining > 0
    }

    override fun updateRenderColor(renderer: GameRenderManager) {
        renderer.setColor(0.4f, 0.4f, 0.4f, 1f)
    }
}
