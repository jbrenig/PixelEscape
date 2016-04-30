package net.brenig.pixelescape.game.entity;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.lib.Reference;

public abstract class EntityMoving extends Entity {

	protected float xVel, yVel;

	public EntityMoving(World world) {
		super(world);
	}

	protected void checkMaxVelocity() {
		xVel = Math.min(xVel, Reference.MAX_ENTITY_SPEED);
		yVel = Math.min(yVel, Reference.MAX_ENTITY_SPEED);
	}

	public void setVelocity(float xVel, float yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
	}

	/**
	 * mvoes the entity
	 *
	 * note: does note check velocity limits
	 * @param delta time passed
	 */
	protected void move(float delta) {
		progress += xVel * delta;
		yPos += yVel * delta;
	}

	@Override
	public void reset() {
		super.reset();
		xVel = 0;
		yVel = 0;
	}
}
