package net.brenig.pixelescape.game.entity

import com.badlogic.gdx.utils.Pool
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.CollisionType
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.render.WorldRenderer

/**
 * Entity that can be spawned into the world
 */
abstract class Entity : Pool.Poolable {

    open lateinit var world: World

    /**
     * @return the xCoordinate of the left corner of this entity
     */
    open var xPos: Float = 0F
        protected set

    /**
     * @return the yCoordinate of the upper corner of this entity
     */
    open var yPos: Float = 0F
        protected set

    /**
     * the xCoordinate of the left corner of this entity
     */
    open val minX: Float
        get() = xPos

    /**
     * the xCoordinate of the upper corner of this entity
     */
    open val minY: Float
        get() = yPos

    /**
     * the xCoordinate of the right corner of this entity
     */
    open val maxX: Float
        get() = xPos

    /**
     * the xCoordinate of the bottom corner of this entity
     */
    open val maxY: Float
        get() = yPos

    /**
     * checks whether the entity is dead (--> should be removed from the world)
     * <br></br>
     * default implementation checks if the entities right edge left the screen (to the left)
     *
     * @return true if the entity should not be used anymore, and is ready to be removed
     */
    open val isDead: Boolean
        get() = maxX < world.currentScreenStart

    /**
     * sets the position of this entity
     */
    fun setPosition(xPos: Float, yPos: Float) {
        this.xPos = xPos
        this.yPos = yPos
    }

    /**
     * renders the entity in the background
     *
     *
     * gets called before terrain is rendered
     *
     * @param game     game instance
     * @param renderer renderer instance
     * @param gameMode current gamemode
     * @param delta    time passed since last frame
     */
    open fun renderBackground(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {}

    /**
     * renders the entity
     *
     * @param game     game instance
     * @param renderer renderer instance
     * @param gameMode current gamemode
     * @param delta    time passed since last frame
     */
    open fun render(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {}

    /**
     * update the entity, gets called every frame
     *
     * @return true if game update should be cancelled (eg. gameover)
     */
    open fun update(delta: Float, inputManager: InputManager, gameMode: GameMode): Boolean {
        return false
    }

    open fun removeEntityOnDeath() {

    }

    override fun reset() {
        xPos = 0f
        yPos = 0f
    }

    /**
     * checks whether the given area collides with this entity
     *
     *
     * used to check for collision (with world or rigid objects)
     *
     * @param x1 left x coordinate
     * @param y1 bottom y coordinate
     * @param x2 right x coordinate
     * @param y2 upper y coordinate
     * @return whether the area collides with this entity
     */
    open fun doesAreaCollideWithEntity(x1: Float, y1: Float, x2: Float, y2: Float): CollisionType {
        return CollisionType.NONE
    }

    /**
     * returns whether this entity intersects with the given area
     *
     * @param x1 left x coordinate
     * @param y1 bottom y coordinate
     * @param x2 right x coordinate
     * @param y2 upper y coordinate
     * @return whether the area intersects with this entity
     */
    open fun doesAreaIntersectWithEntity(x1: Float, y1: Float, x2: Float, y2: Float): Boolean {
        if (minX < x2 && maxX > x1) {
            if (minY < y2 && maxY > y1) {
                return true
            }
        }
        return false
    }

    /**
     * returns whether this entity intersects with the given entity
     *
     * @param other the entity to intersect with
     * @return whether the area intersects with this entity
     */
    open fun doesEntityIntersectWithEntity(other: Entity): Boolean {
        return other.doesAreaIntersectWithEntity(minX, minY, maxX, maxY)
    }

    /**
     * returns whether this entity intersects with the given entity
     *
     * @param other the entity to intersect with
     * @return whether the area intersects with this entity
     */
    open fun doesEntityCollideWithEntity(other: Entity): CollisionType {
        return other.doesAreaCollideWithEntity(minX, minY, maxX, maxY)
    }
}
