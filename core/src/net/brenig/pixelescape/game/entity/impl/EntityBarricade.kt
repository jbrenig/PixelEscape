package net.brenig.pixelescape.game.entity.impl

import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.CollisionType
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.render.WorldRenderer

/**
 * obstacles that spawn in the level
 */
class EntityBarricade : Entity() {

    val sizeX = defaultSizeX
    var sizeY = defaultSizeY
        private set

    override val isDead: Boolean
        get() = maxX < world.currentScreenStart

    override val minX: Float
        get() = this.xPos - sizeX / 2

    override val minY: Float
        get() = this.yPos - sizeY / 2

    override val maxX: Float
        get() = this.xPos + sizeX / 2

    override val maxY: Float
        get() = this.yPos + sizeY / 2

    @Suppress("RedundantVisibilityModifier", "RedundantSetter")
    override var xPos = 0F
        public set

    @Suppress("RedundantVisibilityModifier", "RedundantSetter")
    override var yPos = 0F
        public set

    override fun render(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        game.renderManager.begin()
        game.renderManager.setColor(0f, 0f, 0f, 1f)
        renderer.renderRectWorld(xPos - sizeX / 2, yPos - sizeY / 2, sizeX.toFloat(), sizeY.toFloat())
    }

    override fun doesAreaCollideWithEntity(x1: Float, y1: Float, x2: Float, y2: Float): CollisionType {
        return if (doesAreaIntersectWithEntity(x1, y1, x2, y2)) {
            if (minX > x1) {
                CollisionType.TERRAIN_RIGHT
            } else {
                CollisionType.TERRAIN_LEFT
            }
        } else CollisionType.NONE
    }

    /**
     * sets the y-size of the barricade to the default multiplied by the given value
     */
    fun applyWorldGenSizeModifier(mod: Float) {
        sizeY = (mod * defaultSizeY).toInt()
    }

    companion object {

        val defaultSizeX = 20
        val defaultSizeY = 80
    }


}
