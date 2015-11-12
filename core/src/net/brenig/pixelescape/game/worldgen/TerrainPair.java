package net.brenig.pixelescape.game.worldgen;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.lib.Reference;

/**
 * Created by Jonas Brenig on 03.08.2015.
 */
public class TerrainPair {

	private int top;
	private int bottom;

	public TerrainPair(int top, int bottom) {
		this.setTop(top);
		this.setBottom(bottom);
	}

	public int getTop() {
		return top;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public void setBottom(int bottom) {
		this.bottom = bottom;
	}

	public int getBottom() {
		return bottom;
	}

	public void render(PixelEscape game, World world, float x, float y, float yTranslation, float delta) {
		//Draw Bottom (y=0) blocks
		game.shapeRenderer.rect(x, y, Reference.BLOCK_WIDTH, getTop() * Reference.BLOCK_WIDTH + yTranslation);
		//Draw Top (y=worldHeight) blocks
		game.shapeRenderer.rect(x, y + world.getWorldHeight(), Reference.BLOCK_WIDTH, (getBottom() * Reference.BLOCK_WIDTH - yTranslation) * -1);

	}
}
