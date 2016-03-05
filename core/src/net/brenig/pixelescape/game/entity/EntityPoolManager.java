package net.brenig.pixelescape.game.entity;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.Constructor;
import com.badlogic.gdx.utils.reflect.ReflectionException;

import net.brenig.pixelescape.game.World;

/**
 * Manages Pools for Entities
 *
 * @see {@link com.badlogic.gdx.utils.Pools}
 */
public class EntityPoolManager {

	private final World world;
	private final ObjectMap<Class<? extends Entity>, EntityPool> typePools = new ObjectMap<Class<? extends Entity>, EntityPool>();

	public EntityPoolManager(World world) {
		this.world = world;
	}


	/**
	 * Obtain an instance of the given Entity
	 */
	public <T extends Entity> T obtain(Class<T> type) {
		return getPool(type).obtain();
	}

	/**
	 * Returns a new or existing pool for the specified type, stored in a Class to {@link Pool} map.
	 * The max size of the pool used is 100.
	 */
	public <T extends Entity> EntityPool<T> getPool(Class<T> type) {
		return getPool(type, 4, 100);
	}

	/**
	 * Returns a new or existing pool for the specified type, stored in a Class to {@link Pool} map.
	 */
	@SuppressWarnings("unchecked")
	public <T extends Entity> EntityPool<T> getPool(Class<T> type, int initialCapacity, int max) {
		EntityPool<T> pool = typePools.get(type);
		if (pool == null) {
			pool = new EntityPool<T>(type, initialCapacity, max);
			typePools.put(type, pool);
		}
		return pool;
	}

	/**
	 * Frees the specified objects from the {@link #getPool(Class)}  pool}. Null objects within the array are silently ignored. Objects
	 * don't need to be from the same pool.
	 */
	public void freeAll(Array<? extends Entity> objects) {
		freeAll(objects, false);
	}

	/**
	 * Frees the specified objects from the {@link #getPool(Class) pool}. Null objects within the array are silently ignored.
	 *
	 * @param samePool If true, objects don't need to be from the same pool but the pool must be looked up for each object.
	 */
	@SuppressWarnings("unchecked")
	public void freeAll(Array<? extends Entity> objects, boolean samePool) {
		if (objects == null) throw new IllegalArgumentException("Objects cannot be null.");
		EntityPool pool = null;
		for (int i = 0, n = objects.size; i < n; i++) {
			Entity object = objects.get(i);
			if (object == null) continue;
			if (pool == null) {
				pool = typePools.get(object.getClass());
				if (pool == null) continue; // Ignore freeing an object that was never retained.
			}
			pool.free(object);
			if (!samePool) pool = null;
		}
	}

	public void allocateObjects(Class<? extends Entity> type, int amount) {
		getPool(type, amount, Math.max(amount, 100)).allocateObjects(amount);
	}

	/**
	 * Frees an object from the {@link #getPool(Class)}  pool}.
	 */
	@SuppressWarnings("unchecked")
	public void free(Entity object) {
		if (object == null) throw new IllegalArgumentException("Object cannot be null.");
		EntityPool pool = typePools.get(object.getClass());
		if (pool == null) return; // Ignore freeing an object that was never retained.
		pool.free(object);
	}


	public class EntityPool<T extends Entity> extends Pool<T> {

		private final Constructor constructor;

		public EntityPool(Class<T> type) {
			this(type, 16, Integer.MAX_VALUE);
		}

		public EntityPool(Class<T> type, int initialCapacity) {
			this(type, initialCapacity, Integer.MAX_VALUE);
		}

		public EntityPool(Class<T> type, int initialCapacity, int max) {
			super(initialCapacity, max);
			constructor = findConstructor(type);
			if (constructor == null)
				throw new RuntimeException("Class cannot be created (missing world-arg constructor): " + type.getName());
		}

		private Constructor findConstructor(Class<T> type) {
			try {
				return ClassReflection.getConstructor(type, World.class);
			} catch (Exception ex1) {
				try {
					Constructor constructor = ClassReflection.getDeclaredConstructor(type, World.class);
					constructor.setAccessible(true);
					return constructor;
				} catch (ReflectionException ex2) {
					return null;
				}
			}
		}

		@SuppressWarnings("unchecked")
		@Override
		protected T newObject() {
			try {
				return (T) constructor.newInstance(world);
			} catch (Exception ex) {
				throw new GdxRuntimeException("Unable to create new instance: " + constructor.getDeclaringClass().getName(), ex);
			}
		}

		public void allocateObjects(int amount) {
			for (int i = 0; i < amount; i++) {
				free(newObject());
			}
		}
	}
}
