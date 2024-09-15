package net.brenig.pixelescape.game.player.effects

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.player.Item
import net.brenig.pixelescape.render.GameRenderManager
import net.brenig.pixelescape.render.WorldRenderer

class EffectSlow(player: EntityPlayer) : StatusEffectTimed(player, 10F) {

    private var velocityAmount: Float = 0.toFloat()

    override fun render(game: PixelEscape, renderer: WorldRenderer, player: EntityPlayer, delta: Float) {
        //TODO slow effect??
    }

    override fun onEffectAdded(player: EntityPlayer) {
        this.velocityAmount = Math.min(player.xVelocity * xVelocityFactor, maximumVelocityDecrease)
        player.addXVelocityModifier(-velocityAmount)
    }

    override fun onEffectRemove(player: EntityPlayer) {
        player.addXVelocityModifier(velocityAmount)
    }

    override fun updateRenderColor(renderer: GameRenderManager) {
        renderer.setColor(0.3f, 0.6f, 0.5f, 1f)
    }

    companion object {

        val ITEM: Item = object : Item {
            override fun getItemDrawable(assets: GameAssets): Drawable {
                return assets.itemSlow
            }

            override fun onCollect(player: EntityPlayer): Boolean {
                player.addEffect(EffectSlow(player))
                return true
            }
        }

        private const val xVelocityFactor = 0.2f
        private const val maximumVelocityDecrease = 100f
    }
}
