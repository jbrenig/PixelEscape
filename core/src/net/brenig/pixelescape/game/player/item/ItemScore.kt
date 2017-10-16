package net.brenig.pixelescape.game.player.item

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.entity.impl.particle.EntityFadingText
import net.brenig.pixelescape.game.player.Item

/**
 * Item that increases the players score by a flat value
 */
class ItemScore
/**
 * @param score amount of score added
 */
(private val score: Int) : Item {


    override fun getItemDrawable(assets: GameAssets): Drawable {
        return assets.itemScore
    }

    override fun onCollect(player: EntityPlayer): Boolean {
        player.addBonusScore(score)
        val entity = player.world.createEntity(EntityFadingText::class.java)
        entity.setText("+" + score, 0.8f)
        entity.setPosition(player.xPos, player.yPos)
        player.world.spawnEntity(entity)
        return true
    }

    companion object {

        /**
         * default Item, giving 500 flat score
         */
        val ITEM = ItemScore(500)
    }
}
