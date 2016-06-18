package net.brenig.pixelescape.game.worldgen;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.lib.Reference;

/**
 * Used to store terrain
 */
public class TerrainPair {

	private int bot;
	private int top;

	public TerrainPair(int bot, int top) {
		this.setBot(bot);
		this.setTop(top);
	}

	public int getBot() {
		return bot;
	}

	public void setBot(int bot) {
		this.bot = bot;
	}

	public void setTop(int top) {
		this.top = top;
	}

	public int getTop() {
		return top;
	}

	public void render(PixelEscape game, World world, float x, float y, float yTranslation, float delta) {
		game.getRenderManager().begin();
		//Draw Bottom (y=0) blocks
		game.getRenderManager().rect(x, y, Reference.BLOCK_WIDTH, getBot() * Reference.BLOCK_WIDTH + yTranslation);
		//Draw Top (y=worldHeight) blocks
		game.getRenderManager().rect(x, y + world.getWorldHeight(), Reference.BLOCK_WIDTH, (getTop() * Reference.BLOCK_WIDTH - yTranslation) * -1);

	}
}
