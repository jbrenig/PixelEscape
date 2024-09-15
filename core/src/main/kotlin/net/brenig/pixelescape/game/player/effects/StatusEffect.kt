package net.brenig.pixelescape.game.player.effects

import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.render.GameRenderManager
import net.brenig.pixelescape.render.WorldRenderer

abstract class StatusEffect(protected var player: EntityPlayer) {

    /**
     * @return time remaining, until this effect ends (return 0 or less if not applicable)
     */
    open val scaledTime: Float
        get() = 0f

    abstract fun render(game: PixelEscape, renderer: WorldRenderer, player: EntityPlayer, delta: Float)

    abstract fun update(delta: Float)

    abstract fun effectActive(): Boolean

    /**
     * called when effect gets removed from the player
     */
    open fun onEffectRemove(player: EntityPlayer) {}

    /**
     * will get called when player collides
     *
     * @return false when player doesn't collide due to this statuseffect
     */
    open fun onPlayerCollide(): Boolean {
        return true
    }

    /**
     * called when the effect gets added to the player
     *
     *
     * make changes to [EntityPlayer] here
     *
     */
    open fun onEffectAdded(player: EntityPlayer) {}

    /**
     * updates shaperenderer to a custom color if needed
     *
     *
     * used for rendering remaining duration
     *
     *
     * @param renderer current renderer instance
     */
    open fun updateRenderColor(renderer: GameRenderManager) {}
}
