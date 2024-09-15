package net.brenig.pixelescape.game.player.effects

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.player.Item
import net.brenig.pixelescape.render.WorldRenderer

/**
 * effect that causes barricades to be smaller for some time
 */
class EffectSmallBarricades(player: EntityPlayer) : StatusEffectTimed(player, 10F) {

    override fun render(game: PixelEscape, renderer: WorldRenderer, player: EntityPlayer, delta: Float) {

    }

    override fun onEffectAdded(player: EntityPlayer) {
        player.world.worldGenerator.obstacleSizeModifier = player.world.worldGenerator.obstacleSizeModifier - 0.2f
    }

    override fun onEffectRemove(player: EntityPlayer) {
        player.world.worldGenerator.obstacleSizeModifier = player.world.worldGenerator.obstacleSizeModifier + 0.2f
    }

    companion object {

        val ITEM: Item = object : Item {
            override fun getItemDrawable(assets: GameAssets): Drawable {
                return assets.itemSmallBarricades
            }

            override fun onCollect(player: EntityPlayer): Boolean {
                player.addOrUpdateEffect(EffectSmallBarricades(player))
                return true
            }
        }
    }
}
