package net.brenig.pixelescape.game.worldgen;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * obstacles that spawn in the level
 */
public class Barricade extends Entity {

	public boolean moved = false;

	public static final int sizeX = 20;
	public static final int sizeY = 80;

	public Barricade(World world) {
		super(world);
	}

	@SuppressWarnings("SameReturnValue")
	public static int getSizeX() {
		return sizeX;
	}

	@SuppressWarnings("SameReturnValue")
	public static int getSizeY() {
		return sizeY;
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		game.getRenderManager().beginFilledShape();
		game.getShapeRenderer().setColor(0, 0, 0, 1);
		renderer.renderRectWorld(xPos - sizeX / 2, yPos - sizeY / 2, sizeX, sizeY);
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public CollisionType doesAreaCollideWithEntity(float x1, float y1, float x2, float y2) {
		if(doesAreaIntersectWithEntity(x1, y1, x2, y2)) {
			return CollisionType.ENTITY;
		}
		return CollisionType.NONE;
	}

	@Override
	public float getMinX() {
		return this.xPos - Barricade.sizeX / 2;
	}

	@Override
	public float getMaxX() {
		return this.xPos + Barricade.sizeX / 2;
	}

	@Override
	public float getMinY() {
		return this.yPos - Barricade.sizeY / 2;
	}

	@Override
	public float getMaxY() {
		return this.yPos + Barricade.sizeY / 2;
	}

	public void setXPos(float xPos) {
		this.xPos = xPos;
	}

	public void setYPos(float yPos) {
		this.yPos = yPos;
	}
}
