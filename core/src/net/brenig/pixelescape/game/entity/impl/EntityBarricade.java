package net.brenig.pixelescape.game.entity.impl;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * obstacles that spawn in the level
 */
public class EntityBarricade extends Entity {

	public static final int defaultSizeX = 20;
	public static final int defaultSizeY = 80;

	private int sizeX = defaultSizeX;
	private int sizeY = defaultSizeY;

	public int getSizeX() {
		return sizeX;
	}

	public int getSizeY() {
		return sizeY;
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		game.getRenderManager().begin();
		game.getRenderManager().setColor(0, 0, 0, 1);
		renderer.renderRectWorld(xPos - sizeX / 2, yPos - sizeY / 2, sizeX, sizeY);
	}

	@Override
	public boolean isDead() {
		return getMaxX() < world.getCurrentScreenStart();
	}

	@Override
	public CollisionType doesAreaCollideWithEntity(float x1, float y1, float x2, float y2) {
		if (doesAreaIntersectWithEntity(x1, y1, x2, y2)) {
			if (getMinX() > x1) {
				return CollisionType.TERRAIN_RIGHT;
			} else {
				return CollisionType.TERRAIN_LEFT;
			}
		}
		return CollisionType.NONE;
	}

	@Override
	public float getMinX() {
		return this.xPos - sizeX / 2;
	}

	@Override
	public float getMaxX() {
		return this.xPos + sizeX / 2;
	}

	@Override
	public float getMinY() {
		return this.yPos - sizeY / 2;
	}

	@Override
	public float getMaxY() {
		return this.yPos + sizeY / 2;
	}

	public void setXPos(float xPos) {
		this.xPos = xPos;
	}

	public void setYPos(float yPos) {
		this.yPos = yPos;
	}

	/**
	 * sets the y-size of the barricade to the default multiplied by the given value
	 */
	public void applyWorldGenSizeModifier(float mod) {
		sizeY = (int) (mod * defaultSizeY);
	}


}
