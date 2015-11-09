package net.brenig.pixelescape.game.entity;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;

/**
 * Created by Jonas Brenig on 07.10.2015.
 */
public abstract class Entity {

	protected World worldObj;

	public Entity(World world) {
		worldObj = world;
	}

	public abstract void render(PixelEscape game, float delta, float x, float y);

	public void update(float delta) {};

	public abstract boolean isDead();

	public void removeEntityOnDeath() {

	}
}
