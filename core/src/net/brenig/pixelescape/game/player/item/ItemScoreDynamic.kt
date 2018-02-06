package net.brenig.pixelescape.game.player.item

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.entity.impl.particle.EntityFadingText
import net.brenig.pixelescape.game.player.Item

/**
 * Item that increases the players score by a flat
 *
 * @param scoreMod amount of score added (factor based on player score --> 0.05F : 5% of current player score
 */
class ItemScoreDynamic(private val scoreMod: Float) : Item {


    override fun getItemDrawable(assets: GameAssets): Drawable {
        return assets.itemScore
    }

    override fun onCollect(player: EntityPlayer): Boolean {
        val score = (scoreMod * player.score.toFloat()).toInt()
        player.addBonusScore(score)
        val entity = player.world.createEntity(EntityFadingText::class.java)
        entity.setText("+" + score, 0.8f)
        entity.setPosition(player.xPos, player.yPos)
        player.world.spawnEntity(entity)
        return true
    }

    companion object {

        /**
         * default Item, giving a 5% bonus score
         */
        val ITEM = ItemScoreDynamic(0.05f)
    }
}
