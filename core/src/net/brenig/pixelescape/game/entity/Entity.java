package net.brenig.pixelescape.game.entity;

import com.badlogic.gdx.utils.Pool;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;

/**
 * Entity that can be spawned into the world
 */
public abstract class Entity implements Pool.Poolable {

	protected World worldObj;

	public Entity(World world) {
		worldObj = world;
	}

	public abstract void render(PixelEscape game, float delta, float x, float y);

	/**
	 * update the entity, gets called every frame
	 *
	 * @return true if game update should be cancelled (eg. gameover)
	 */
	@SuppressWarnings("EmptyMethod")
	public boolean update(float delta, InputManager inputManager) {return false;}

	public abstract boolean isDead();

	@SuppressWarnings("EmptyMethod")
	public void removeEntityOnDeath() {

	}

	@Override
	public void reset() {

	}

	public CollisionType doesAreaCollideWithEntity(float x1, float y1, float x2, float y2) {
		return CollisionType.NONE;
	}
}
