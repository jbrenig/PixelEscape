package net.brenig.pixelescape.game.entity;

import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.worldgen.Barricade;
import net.brenig.pixelescape.game.worldgen.TerrainPair;
import net.brenig.pixelescape.lib.CycleIntArray;
import net.brenig.pixelescape.lib.Reference;

/**
 * Created by Jonas Brenig on 02.08.2015.
 */
public class PlayerEntity implements IMovingEntity {

	private double velocity;
	private float yVelocity;

	private int xPosScreen;

	private float yPos;


	private double progress;

	private PlayerPathEntity[] pathEntities = new PlayerPathEntity[4];

	private boolean lastTouched = false;

	private CycleIntArray lastYPositions;
	private int lastXPosition = 0;
	private float YVelocity;

	public PlayerEntity() {
		lastYPositions = new CycleIntArray(20, (int) yPos);
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i] = new PlayerPathEntity(yPos, xPosScreen);
		}
		reset();
	}

	public void update(float deltaTick, InputManager inputManager) {
		progress += deltaTick * velocity;
		yPos += deltaTick * yVelocity;

		//speed update
		if (inputManager.isTouched() || inputManager.isSpaceDown()) {
			if(!lastTouched) {
				yVelocity += Reference.CLICK_ACCELERATION * deltaTick;
				lastTouched = true;
			} else {
				yVelocity += Reference.TOUCH_ACCELERATION * deltaTick;
				lastTouched = true;
			}
		} else {
			yVelocity += Reference.GRAVITIY_ACCELERATION * deltaTick;
			lastTouched = false;
		}
		velocity += Reference.SPEED_MODIFIER * deltaTick;

		yVelocity = Math.min(Reference.MAX_ENTITY_SPEED, yVelocity);
		velocity = Math.min(Reference.MAX_ENTITY_SPEED, velocity);

		for (int i = 0; i < pathEntities.length; i++) {
			if (i == 0) {
				pathEntities[i].update(this, deltaTick, this, lastYPositions.getFromNewest(4));
			} else {
				pathEntities[i].update(pathEntities[i - 1], deltaTick, this, lastYPositions.getFromNewest(4 + i * 5));
			}
		}

		if(getXPos() - lastXPosition > Reference.PATH_ENTITY_OFFSET / 5) {
			lastYPositions.add((int) yPos);
			lastXPosition = getXPos();
		}
	}

	/**
	 * resets this player entity to starting position
	 */
	public void reset() {
		progress = 0;
		yPos = Reference.GAME_RESOLUTION_Y / 2;
		velocity = Reference.STARTING_SPEED;
		yVelocity = 0;
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i].reset(yPos, xPosScreen - Reference.PATH_ENTITY_OFFSET * (i + 1));
		}
		lastYPositions.fill((int) yPos);
	}

	public double getProgress() {
		return progress;
	}

	/**
	 * player progress in pixels
	 */
	public int getXPos() {
		return (int) progress;
	}

	public double getVelocity() {
		return velocity;
	}

	public float getYPos() {
		return yPos;
	}

	public int getXPosScreen() {
		return xPosScreen;
	}

	public void setXPosScreen(int xPosScreen) {
		this.xPosScreen = xPosScreen;
		for(int i = 0; i < pathEntities.length; i++) {
			pathEntities[i].setXPosScreen(xPosScreen - (Reference.PATH_ENTITY_OFFSET * (i + 1)));
		}
	}

	public void collideWithWorld(World world) {
		TerrainPair back = world.getBlockForPosition(xPosScreen - getPlayerSizeRadius());
		TerrainPair front = world.getBlockForPosition(xPosScreen + getPlayerSizeRadius());
		if (yPos - getPlayerSizeRadius() < back.top * Reference.BLOCK_WIDTH || yPos - getPlayerSizeRadius() < front.top * Reference.BLOCK_WIDTH) {
			//collide
			world.onPlayerCollide();
		}
		if (yPos + getPlayerSizeRadius() > world.getWorldHeight() - back.bottom * Reference.BLOCK_WIDTH || yPos + getPlayerSizeRadius() > world.getWorldHeight() - front.bottom * Reference.BLOCK_WIDTH) {
			//collide
			world.onPlayerCollide();
		}
	}

	public void collideWithObstacle(net.brenig.pixelescape.game.worldgen.Barricade ob, World world) {
		if(ob.posX - Barricade.sizeX / 2 < getXPos() + getPlayerSizeRadius() && ob.posX + Barricade.sizeX / getPlayerSizeRadius() > getXPos() - getPlayerSizeRadius()) {
			if(ob.posY - Barricade.sizeY / 2 < getYPos() + getPlayerSizeRadius() && ob.posY + Barricade.sizeY / 2 > getYPos() - getPlayerSizeRadius()) {
				world.onPlayerCollide();
			}
		}
	}

	public PlayerPathEntity[] getPathEntities() {
		return pathEntities;
	}

	public int getPlayerSize() {
		return Reference.PLAYER_ENTITY_SIZE;
	}

	public int getPlayerSizeRadius() {
		return getPlayerSize() / 2;
	}

	public float getYVelocity() {
		return YVelocity;
	}
}
