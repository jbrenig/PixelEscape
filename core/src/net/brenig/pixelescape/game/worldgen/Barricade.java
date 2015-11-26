package net.brenig.pixelescape.game.worldgen;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.game.World;

/**
 * obstacles that spawn in the level
 */
public class Barricade {
	public int posX;
	public int posY;

	public boolean moved = false;

	public static final int sizeX = 20;
	public static final int sizeY = 80;

	public Barricade() {
		this.posX = 0;
		this.posY = 0;
	}

	public Barricade(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public void render(World world, float x, float y, ShapeRenderer render) {
		render.begin(ShapeRenderer.ShapeType.Filled);
		render.setColor(0, 0, 0, 1);
		render.rect(x + world.convertWorldCoordToScreenCoord(posX) - sizeX / 2, y + posY - sizeY / 2, sizeX, sizeY);
		render.end();
	}

	@SuppressWarnings("SameReturnValue")
	public static int getSizeX() {
		return sizeX;
	}

	@SuppressWarnings("SameReturnValue")
	public static int getSizeY() {
		return sizeY;
	}
}
