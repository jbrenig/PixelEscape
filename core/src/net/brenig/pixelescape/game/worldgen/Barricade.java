package net.brenig.pixelescape.game.worldgen;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * obstacles that spawn in the level
 */
public class Barricade extends Entity {

	public int posX;
	public int posY;

	public boolean moved = false;

	public static final int sizeX = 20;
	public static final int sizeY = 80;

	public Barricade(World world) {
		super(world);
		this.posX = 0;
		this.posY = 0;
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
	public void render(PixelEscape game, WorldRenderer renderer, float x, float y, float delta) {
		game.getRenderManager().beginFilledShape();
		game.getRenderManager().getShapeRenderer().setColor(0, 0, 0, 1);
		game.getRenderManager().getShapeRenderer().rect(x + worldObj.convertWorldCoordToScreenCoord(posX) - sizeX / 2, y + posY - sizeY / 2, sizeX, sizeY);
	}

	@Override
	public boolean isDead() {
		return false;
	}

	@Override
	public CollisionType doesAreaCollideWithEntity(float x1, float y1, float x2, float y2) {
		if (this.posX - Barricade.sizeX / 2 < x2 && this.posX + Barricade.sizeX / 2 > x1) {
			if (this.posY - Barricade.sizeY / 2 < y2 && this.posY + Barricade.sizeY / 2 > y1) {
				return CollisionType.ENTITY;
			}
		}
		return CollisionType.NONE;
	}
}
