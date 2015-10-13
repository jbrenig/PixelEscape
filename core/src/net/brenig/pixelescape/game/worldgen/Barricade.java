package net.brenig.pixelescape.game.worldgen;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.game.entity.EntityPlayer;

/**
 * Created by Jonas Brenig on 06.08.2015.
 */
public class Barricade {
	public int posX;
	public int posY;

	public static final int sizeX = 20;
	public static final int sizeY = 80;

	public Barricade(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}

	public void render(int x, int y, EntityPlayer player, ShapeRenderer render) {
		render.begin(ShapeRenderer.ShapeType.Filled);
		render.setColor(0, 0, 0, 1);
		render.rect(x + posX - sizeX / 2 - player.getXPos() + player.getXPosScreen(), y + posY - sizeY / 2, sizeX, sizeY);
		render.end();
	}
}
