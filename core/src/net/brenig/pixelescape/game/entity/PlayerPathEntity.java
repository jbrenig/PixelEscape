package net.brenig.pixelescape.game.entity;

import net.brenig.pixelescape.lib.Reference;

/**
 * Created by Jonas Brenig on 06.08.2015.
 */
public class PlayerPathEntity implements IMovingEntity {
	private float yVelocity = 0;
	private float yPos = Reference.GAME_RESOLUTION_Y / 2;
	private int xPosScreen = 0;

	public PlayerPathEntity(float yPos, int xPosScreen) {
		this.yPos = yPos;
		this.xPosScreen = xPosScreen;
	}

	public void update(IMovingEntity e, float delta, PlayerEntity playerEntity, int lastYPosition) {
		this.yPos += yVelocity + delta;
//		this.yVelocity = (float) ((lastYPosition - this.getYPos()) * Reference.PATH_ENTITY_ACCELERATION_MOD * playerEntity.getVelocity());
		this.yVelocity = (float) ((e.getYPos() - this.getYPos()) * Reference.PATH_ENTITY_ACCELERATION_MOD * playerEntity.getVelocity());
	}

	public float getYPos() {
		return yPos;
	}

	public int getXPosScreen() {
		return xPosScreen;
	}

	public void reset(float yPos, int xPosScreen) {
		this.yPos = yPos;
		this.yVelocity = 0;
		this.xPosScreen = xPosScreen;
	}

	public void setXPosScreen(int xPosScreen) {
		this.xPosScreen = xPosScreen;
	}

	public int getSize() {
		return Reference.PATH_ENTITY_SIZE;
	}

	public int getSizeRadius() {
		return getSize() / 2;
	}
}
