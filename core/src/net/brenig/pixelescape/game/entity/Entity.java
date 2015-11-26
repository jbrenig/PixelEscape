package net.brenig.pixelescape.game.entity;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;

/**
 * Entity that can be spawned into the world
 */
public abstract class Entity {

	protected World worldObj;

	public Entity(World world) {
		worldObj = world;
	}

	public abstract void render(PixelEscape game, float delta, float x, float y);

	@SuppressWarnings("EmptyMethod")
	public void update(float delta) {}

	public abstract boolean isDead();

	@SuppressWarnings("EmptyMethod")
	public void removeEntityOnDeath() {

	}
}
