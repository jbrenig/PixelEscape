package net.brenig.pixelescape.game.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pools;
import net.brenig.pixelescape.game.World;

/**
 * Manages Pools for Entities
 *
 * @see com.badlogic.gdx.utils.Pools
 */
public class EntityPoolManager {

	private final World world;

	public EntityPoolManager(World world) {
		this.world = world;
	}


	/**
	 * Obtain an instance of the given Entity
	 */
	public <T extends Entity> T obtain(Class<T> type) {
		T entity = Pools.obtain(type);
		entity.setWorld(world);
		return entity;
	}

	/**
	 * Frees the specified objects.
	 *
	 * @see Pools#freeAll(Array)
	 */
	public void freeAll(Array<? extends Entity> objects) {
		Pools.freeAll(objects);
	}

	/**
	 * Frees the specified objects.
	 *
	 * @see Pools#freeAll(Array, boolean)
	 */
	public void freeAll(Array<? extends Entity> objects, boolean samePool) {
		Pools.freeAll(objects, samePool);
	}

	/**
	 * Frees an object.
	 *
	 * @see Pools#free(Object)
	 */
	public void free(Entity object) {
		Pools.free(object);
	}

}
