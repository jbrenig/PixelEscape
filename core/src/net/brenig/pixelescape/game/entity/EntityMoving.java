package net.brenig.pixelescape.game.entity;

import net.brenig.pixelescape.game.GameMode;
import net.brenig.pixelescape.game.World;

public abstract class EntityMoving extends Entity {

	protected float xVel, yVel;

	public EntityMoving(World world) {
		super(world);
	}

	protected void checkMaxVelocity(GameMode gameMode) {
		xVel = Math.min(xVel, gameMode.getMaxEntitySpeed());
		yVel = Math.min(yVel, gameMode.getMaxEntitySpeed());
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
		xPos += xVel * delta;
		yPos += yVel * delta;
	}

	@Override
	public void reset() {
		super.reset();
		xVel = 0;
		yVel = 0;
	}
}
