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
}
