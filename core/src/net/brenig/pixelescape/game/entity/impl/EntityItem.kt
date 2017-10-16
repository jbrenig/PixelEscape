package net.brenig.pixelescape.game.entity.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.GL20
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.lib.debug
import net.brenig.pixelescape.render.WorldRenderer

/**
 * Entity that contains one [net.brenig.pixelescape.game.player.Item]
 *
 *
 * when the player collects the item effects will be handled by [net.brenig.pixelescape.game.player.Item.onCollect]
 *
 */
class EntityItem : Entity() {

    var item: net.brenig.pixelescape.game.player.Item? = null

    override var isDead = false
        private set(value) {
            field = value
        }
        get() {
            return field || super.isDead
        }

    override val minX: Float
        get() = xPos - RADIUS

    override val minY: Float
        get() = yPos - RADIUS

    override val maxX: Float
        get() = xPos + RADIUS

    override val maxY: Float
        get() = yPos + RADIUS

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        if (isDead || item == null) {
            return
        }
        game.renderManager.begin()
        Gdx.gl.glEnable(GL20.GL_BLEND)
        renderer.renderSimpleAnimationWorld(game.gameAssets.itemAnimatedBackground, minX, minY, SIZE.toFloat(), SIZE.toFloat(), delta)
        renderer.renderDrawableWorld(item!!.getItemDrawable(game.gameAssets), xPos - ITEM_RADIUS, yPos - ITEM_RADIUS, ITEM_SIZE.toFloat(), ITEM_SIZE.toFloat())
    }

    override fun update(delta: Float, inputManager: InputManager, gameMode: GameMode): Boolean {
        if (!isDead && doesEntityIntersectWithEntity(world.player)) {
            if (item != null && item!!.onCollect(world.player)) {
                this.isDead = true
                debug("Player collected item: " + item!!)
            }
        }
        return false
    }

    override fun reset() {
        super.reset()
        isDead = false
        item = null
    }

    companion object {

        private const val SIZE = 32 * 2
        private const val RADIUS = SIZE / 2

        private const val ITEM_SIZE = 18 * 2
        private const val ITEM_RADIUS = ITEM_SIZE / 2
    }
}
