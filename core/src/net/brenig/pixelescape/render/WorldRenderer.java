package net.brenig.pixelescape.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.EntityPlayer;

/**
 * Helper class for rendering a {@link World}
 * Renders with y-Up
 * Created by Jonas Brenig on 02.08.2015.
 */
public class WorldRenderer {

	private World world;
	private final PixelEscape game;

	private float xPos = 0;
	private float yPos = 0;

	private float screenShakeX = 0;
	private float screenShakeY = 0;

	private float screenShakeForceX = 0;
	private float screenShakeForceY = 0;

	private float screenShakeTimerX = 0;
	private float screenShakeTimerY = 0;

	private static final float screenShakeSpeed = 8;
	private static final float screenShakeLengthMod = 4;
	private static final float screenShakeForceMult = 8;


	public WorldRenderer(final PixelEscape game, World world) {
		this.world = world;
		this.game = game;
	}

	private void shakeScreen(float delta) {
		if(screenShakeForceX > 0) {
			screenShakeTimerX += delta * (screenShakeLengthMod + PixelEscape.rand.nextFloat());
		}
		if(screenShakeForceY > 0) {
			screenShakeTimerY += delta * (screenShakeLengthMod + PixelEscape.rand.nextFloat());
		}
		if(screenShakeTimerX >= screenShakeForceX) {
			screenShakeX = screenShakeForceX = screenShakeTimerX = 0;
		} else {
			float difX = screenShakeForceX - screenShakeTimerX;
			screenShakeX = (float) (Math.sin(screenShakeTimerX * screenShakeSpeed) * difX) * screenShakeForceMult;
		}
		if(screenShakeTimerY >= screenShakeForceY) {
			screenShakeY = screenShakeForceY = screenShakeTimerY = 0;
		} else {
			float difY = screenShakeForceY - screenShakeTimerY;
			screenShakeY = (float) (Math.sin(screenShakeTimerY * screenShakeSpeed) * difY) * screenShakeForceMult;
		}
	}

	public void applyForceToScreen(float x, float y) {
		if(x * Math.PI > screenShakeForceX) {
			screenShakeForceX = (float) (x * Math.PI);
		}
		if(y * Math.PI > screenShakeForceY) {
			screenShakeForceY = (float) (y * Math.PI);
		}
	}

	/**
	 * Renders the World
	 */
	public void render(float delta) {
		if(game.gameDebugSettings.getBoolean("SCREEN_SHAKE")) {
			shakeScreen(delta);
		}
		renderPlayerEntity(world.player, delta);
		renderWorld();
		renderEntities(delta);
	}

	private void renderEntities(float delta) {
		for(Entity e : world.getEntityList()) {
			e.render(game, delta, xPos + screenShakeX, yPos + screenShakeY);
		}
	}

	private void renderPlayerEntity(EntityPlayer player, float delta) {
		player.render(game, delta, xPos + screenShakeX, yPos + screenShakeY);
	}

	private void renderWorld() {
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		game.shapeRenderer.setColor(0, 0, 0, 1);

		int index = 0;
		while(!isBlockVisible(index)) {
			index++;
		}
		while (isBlockVisible(index) && index < world.getBlockBufferSize()) {
			world.getTerrainPairForIndex(index).render(game, world, xPos + getBlockPositionFromLocalIndex(index) + screenShakeX, yPos, screenShakeY, Gdx.graphics.getDeltaTime());
			index++;
		}

		game.shapeRenderer.end();

		for(int i = 0; i < world.obstacles.size(); i++) {
			world.obstacles.get(i).render(world, xPos + screenShakeX, yPos + screenShakeY, game.shapeRenderer);
		}

	}


	private float getBlockPositionFromLocalIndex(int index) {
		return world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(index));
	}

	/**
	 * @return returns true if the given block is currently visible
	 */
	private boolean isBlockVisible(int index) {
		//is this needed?
		return world.isWorldCoordinateVisible(world.convertLocalBlockToWorldBlockCoordinate(index));
	}

	public void setPosition(int x, int y) {
		this.xPos = x;
		this.yPos = y;
	}
}
