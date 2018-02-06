package net.brenig.pixelescape.game.entity

import com.badlogic.gdx.utils.Array
import com.badlogic.gdx.utils.Pools
import net.brenig.pixelescape.game.World

/**
 * Manages Pools for Entities
 *
 * @see com.badlogic.gdx.utils.Pools
 */
class EntityPoolManager(private val world: World) {


    /**
     * Obtain an instance of the given Entity
     */
    fun <T : Entity> obtain(type: Class<T>): T {
        val entity = Pools.obtain(type)
        entity.world = world
        return entity
    }

    /**
     * Frees the specified objects.
     *
     * @see Pools.freeAll
     */
    fun freeAll(objects: Array<out Entity>) {
        Pools.freeAll(objects)
    }

    /**
     * Frees the specified objects.
     *
     * @see Pools.freeAll
     */
    fun freeAll(objects: Array<out Entity>, samePool: Boolean) {
        Pools.freeAll(objects, samePool)
    }

    /**
     * Frees an object.
     *
     * @see Pools.free
     */
    fun free(entity: Entity) {
        Pools.free(entity)
    }

    fun <T : Entity> preallocate(type: Class<T>, initCount: Int, maxCount: Int = 100) {
        val pool = Pools.get(type, maxCount)
        (1..initCount).forEach {
            val instance = pool.obtain()
            pool.free(instance)
        }
    }

}
