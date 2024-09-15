package net.brenig.pixelescape.game.player

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityItem
import net.brenig.pixelescape.game.entity.impl.EntityPlayer


/**
 * Interface to be implemented by all kinds of items
 *
 * used to let the player collect items via [EntityItem]
 *
 * classes implementing this interface should usually ba singletons
 *
 */
interface Item {

    /**
     * @param assets game assets
     * @return the [Drawable] that should be used to display the item in world
     */
    fun getItemDrawable(assets: GameAssets): Drawable

    /**
     * gets called when the player tries to collect the item
     *
     *
     * note: when false is returned this method may get called every tick in which the player still collides with the [EntityItem]
     *
     * @return true if item was collected, false otherwise
     */
    fun onCollect(player: EntityPlayer): Boolean
}
