package net.brenig.pixelescape.game.player;

import net.brenig.pixelescape.game.entity.*;
import net.brenig.pixelescape.game.entity.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;

/**
 * Player path entity
 */
public class PlayerPathEntity implements IMovingEntity {
	private float yVelocity = 0;
	private float yPos = Reference.GAME_RESOLUTION_Y / 2;
	private int xPosScreen = 0;

	public PlayerPathEntity(float yPos, int xPosScreen) {
		this.yPos = yPos;
		this.xPosScreen = xPosScreen;
	}

	public void update(IMovingEntity e, float delta, EntityPlayer playerEntity) {
		this.yPos += yVelocity * delta;
		this.yVelocity = (e.getYPos() - this.getYPos()) * Reference.PATH_ENTITY_ACCELERATION_MOD * playerEntity.getXVelocity();
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

	@SuppressWarnings("SameReturnValue")
	public int getSize() {
		return Reference.PATH_ENTITY_SIZE;
	}

	public int getSizeRadius() {
		return getSize() / 2;
	}
}
