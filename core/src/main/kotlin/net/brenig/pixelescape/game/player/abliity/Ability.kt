package net.brenig.pixelescape.game.player.abliity

import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.player.Item
import net.brenig.pixelescape.screen.GameScreen

import java.io.Serializable

abstract class Ability : Item, Serializable {


    /**
     * @return cooldown time between uses of this item
     */
    val cooldown: Float

    constructor() {
        this.cooldown = 0f
    }

    constructor(cooldown: Float) {
        this.cooldown = cooldown
    }

    /**
     * gets called when player tries to use ability
     *
     * @param world  the current world
     * @param player the current player entity
     * @return whether ability was executed (if true is returned the player will have "used" the ability and might lose his item)
     */
    abstract fun onActivate(screen: GameScreen, world: World, player: EntityPlayer): Boolean

    /**
     * @param assets game assets
     * @return ability icon
     */
    abstract fun getDrawable(assets: GameAssets): Drawable

    /**
     * called when player tries to collect this item
     *
     *
     * it will not yet be added to the players itemslot
     *
     *
     * @param player hte player that collected this item
     * @return true if this ability can be collected
     * @see Item
     */
    override fun onCollect(player: EntityPlayer): Boolean {
        player.addAbility(this, 1)
        return true
    }

    override fun getItemDrawable(assets: GameAssets): Drawable {
        return getDrawable(assets)
    }
}
