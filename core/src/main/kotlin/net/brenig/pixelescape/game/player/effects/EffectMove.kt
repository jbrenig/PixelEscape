package net.brenig.pixelescape.game.player.effects

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.player.Item
import net.brenig.pixelescape.render.GameRenderManager
import net.brenig.pixelescape.render.WorldRenderer

/**
 * effect that increases player vertical movement
 */
class EffectMove(player: EntityPlayer) : StatusEffectTimed(player, 10F) {

    override fun render(game: PixelEscape, renderer: WorldRenderer, player: EntityPlayer, delta: Float) {

    }

    override fun onEffectAdded(player: EntityPlayer) {
        player.addYVelocityFactor(PLAYER_VERTICAL_VELOCITY_MOD)
    }

    override fun onEffectRemove(player: EntityPlayer) {
        player.addYVelocityFactor(-PLAYER_VERTICAL_VELOCITY_MOD)
    }

    override fun updateRenderColor(renderer: GameRenderManager) {
        renderer.setColor(0.7f, 0.6f, 0.1f, 1f)
    }

    companion object {

        private const val PLAYER_VERTICAL_VELOCITY_MOD = 1f

        val ITEM: Item = object : Item {
            override fun getItemDrawable(assets: GameAssets): Drawable {
                return assets.itemMove
            }

            override fun onCollect(player: EntityPlayer): Boolean {
                player.addOrUpdateEffect(EffectMove(player))
                return true
            }
        }
    }
}
