package net.brenig.pixelescape.render;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.PlayerEntity;
import net.brenig.pixelescape.game.entity.PlayerPathEntity;
import net.brenig.pixelescape.lib.Reference;

/**
 * Helper class for rendering a {@link World}
 * Created by Jonas Brenig on 02.08.2015.
 */
public class WorldRenderer {

	private World world;
	private final PixelEscape game;

	private int xPos = 0;
	private int yPos = 0;


	public WorldRenderer(final PixelEscape game, World world) {
		this.world = world;
		this.game = game;
	}

	/**
	 * Renders the World
	 */
	public void render(float delta) {
		renderPlayerEntity(world.player);
		renderWorld();
		renderEntities(delta);
	}

	private void renderEntities(float delta) {
		for(Entity e : world.getEntityList()) {
			e.render(game, delta);
		}
	}

	private void renderPlayerEntity(PlayerEntity player) {
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// Draw Background color
		game.shapeRenderer.setColor(0, 0, 0, 1);
		game.shapeRenderer.rect(xPos + player.getXPosScreen() - player.getPlayerSize() / 2, player.getYPos() - player.getPlayerSize() / 2 + yPos, player.getPlayerSize(), player.getPlayerSize());

		for (PlayerPathEntity e : player.getPathEntities()) {
			game.shapeRenderer.rect(xPos + e.getXPosScreen() - e.getSizeRadius(), yPos + e.getYPos() - e.getSizeRadius(), e.getSize(), e.getSize());
		}

		// End ShapeRenderer
		game.shapeRenderer.end();
	}

	private void renderWorld() {
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		game.shapeRenderer.setColor(0, 0, 0, 1);

		int index = 0;
		while(!isBlockVisible(index)) {
			index++;
		}
		while (isBlockVisible(index) && index < world.getBlockBufferSize()) {
			game.shapeRenderer.rect(xPos + getBlockPosition(index), yPos, Reference.BLOCK_WIDTH, world.getTopBlockHeight(index) * Reference.BLOCK_WIDTH);
			game.shapeRenderer.rect(xPos + getBlockPosition(index), yPos + world.getWorldHeight(), Reference.BLOCK_WIDTH, world.getBottomBlockHeight(index) * Reference.BLOCK_WIDTH * -1);
			index++;
		}

		game.shapeRenderer.end();

		for(int i = 0; i < world.obstacles.size(); i++) {
			world.obstacles.get(i).render(xPos, yPos, world.player, game.shapeRenderer);
		}

	}

	/**
	 * @return returns the onscreen position of the given block
	 */
	private int getBlockPosition(int index) {
		return (int) ((world.getBlocksGenerated() - index) * Reference.BLOCK_WIDTH - world.player.getXPos() + world.player.getXPosScreen());
	}

	/**
	 * @return returns true if the given block is currently visible
	 */
	private boolean isBlockVisible(int index) {
		return true;
		/*
		int blockPosition = getBlockPosition(index);
		if(blockPosition > playerRenderX + Reference.BLOCK_WIDTH) {
			return false;
		} else if(blockPosition < -playerRenderX) {
			return false;
		}
		return true;
		*/
	}

	public void setPosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
}
