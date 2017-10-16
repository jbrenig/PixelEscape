package net.brenig.pixelescape.game.player.item

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.player.Item

/**
 * Item that adds one life to the players remaining lives
 */
class ItemLife : Item {

    override fun getItemDrawable(assets: GameAssets): Drawable {
        return assets.heartDrawable
    }

    override fun onCollect(player: EntityPlayer): Boolean {
        player.extraLives = player.extraLives + 1
        return true
    }

    companion object {

        val ITEM = ItemLife()
    }
}
