package net.brenig.pixelescape.render;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.render.background.IBackgroundLayer;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class for rendering a {@link World}
 * Renders with y-Up
 */
public class WorldRenderer {

	private World world;
	private final PixelEscape game;

	private float rendererYOffset = 0;

	private float xOffset = 0;
	private float targetX = 0;
	private float movementSpeedX = 0;

	private float screenShakeX = 0;
	private float screenShakeY = 0;

	private float screenShakeForceX = 0;
	private float screenShakeForceY = 0;

	private float screenShakeTimerX = 0;
	private float screenShakeTimerY = 0;

	//gets updated every frame
	//total offsets
	private float currentTotalXOffset = 0;
	private float currentTotalYOffset = 0;

	private static final float screenShakeSpeed = 8;
	private static final float screenShakeLengthMod = 7;
	private static final float screenShakeForceMult = 6;
	private static final float screenShakeNoise = 1.4F;

	private List<IBackgroundLayer> backgroundLayers = new ArrayList<IBackgroundLayer>();

	public WorldRenderer(final PixelEscape game, World world) {
		this.world = world;
		this.game = game;
	}

	public void addBackgroundLayer(IBackgroundLayer layer) {
		backgroundLayers.add(layer);
	}

	/**
	 * calculate screen shake effect
	 *
	 * @param delta time since last tick
	 */
	private void shakeScreen(float delta) {
		if (screenShakeForceX > 0) {
			screenShakeTimerX += delta * (screenShakeLengthMod + PixelEscape.Companion.getRand().nextFloat());
		}
		if (screenShakeForceY > 0) {
			screenShakeTimerY += delta * (screenShakeLengthMod + PixelEscape.Companion.getRand().nextFloat());
		}
		if (screenShakeTimerX >= screenShakeForceX) {
			screenShakeX = screenShakeForceX = screenShakeTimerX = 0;
		} else {
			float difX = screenShakeForceX - screenShakeTimerX;
			screenShakeX = (float) (Math.sin(screenShakeTimerX * screenShakeSpeed + world.getRandom().nextFloat() * screenShakeNoise) * difX) * screenShakeForceMult;
		}
		if (screenShakeTimerY >= screenShakeForceY) {
			screenShakeY = screenShakeForceY = screenShakeTimerY = 0;
		} else {
			float difY = screenShakeForceY - screenShakeTimerY;
			screenShakeY = (float) (Math.sin(screenShakeTimerY * screenShakeSpeed + world.getRandom().nextFloat() * screenShakeNoise) * difY) * screenShakeForceMult;
		}
	}

	/**
	 * initiates a screen shake effect
	 *
	 * @param x force on x axis
	 * @param y force on y axis
	 */
	public void applyForceToScreen(float x, float y) {
		if (x * Math.PI > screenShakeForceX) {
			screenShakeForceX = (float) (x * Math.PI);
		}
		if (y * Math.PI > screenShakeForceY) {
			screenShakeForceY = (float) (y * Math.PI);
		}
	}

	/**
	 * move the camera to the specified world coordinate (at the specified speed)
	 */
	public void moveScreenTo(float x, float movementSpeedX) {
		this.targetX = x;
		this.movementSpeedX = movementSpeedX;
	}

	public float getXPos() {
		return xOffset;
	}

	public float getRendererYOffset() {
		return rendererYOffset;
	}

	private void moveScreen(float delta) {
		if (movementSpeedX != 0 && xOffset != targetX) {
			if (targetX < xOffset) {
				xOffset -= Math.min(xOffset - targetX, movementSpeedX * delta);
			} else {
				xOffset += Math.min(targetX - xOffset, movementSpeedX * delta);
			}
		}
	}

	/**
	 * Renders the World
	 */
	public void render(float delta) {
		if (game.getGameDebugSettings().getBoolean("SCREEN_SHAKE")) {
			shakeScreen(delta);
		}
		moveScreen(delta);
		currentTotalXOffset = xOffset + screenShakeX;
		currentTotalYOffset = rendererYOffset + screenShakeY;

		renderWorldBackground();
		renderEntitiesBackground(delta);
		renderWorld(delta);
		renderEntities(delta);
	}

	/**
	 * renders terrain background
	 */
	private void renderWorldBackground() {
		for (IBackgroundLayer layer : backgroundLayers) {
			layer.draw(this);
		}
	}

	/**
	 * renders entities in background
	 */
	private void renderEntitiesBackground(float delta) {
		for (Entity e : world.getEntityList()) {
			e.renderBackground(game, this, world.getScreen().getGameMode(), delta);
		}
	}

	/**
	 * renders entities
	 */
	private void renderEntities(float delta) {
		for (Entity e : world.getEntityList()) {
			e.render(game, this, world.getScreen().getGameMode(), delta);
		}
	}

	/**
	 * renders terrain
	 */
	private void renderWorld(float delta) {
		game.getRenderManager().disableBlending();
		game.getRenderManager().begin();
		game.getRenderManager().setColor(0, 0, 0, 1);

		for (int index = world.getCameraLeftLocalIndex(); index < world.getCameraRightLocalIndex() + 1; index++) {
			world.getTerrainPairForIndex(index).render(game, world, currentTotalXOffset + getBlockPositionFromLocalIndex(index), rendererYOffset, screenShakeY, delta);
		}
		game.getRenderManager().enableBlending();
	}

	/**
	 * renders a rectangle using {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer} and {@link GameRenderManager}
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderRect(float x, float y, float width, float height) {
		game.getRenderManager().rect(currentTotalXOffset + x, currentTotalYOffset + y, width, height);
	}

	/**
	 * same as {@link #renderRect(float, float, float, float)}, but using global coordinates
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderRectWorld(float x, float y, float width, float height) {
		renderRect(world.convertWorldCoordToScreenCoord(x), y, width, height);
	}


	/**
	 * renders a {@link Drawable} using {@link GameRenderManager}
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderDrawable(Drawable drawable, float x, float y, float width, float height) {
		drawable.draw(game.getRenderManager().getBatch(), currentTotalXOffset + x, currentTotalYOffset + y, width, height);
	}

	/**
	 * same as {@link #renderDrawable(Drawable, float, float, float, float)}, but using global coordinates
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderDrawableWorld(Drawable drawable, float x, float y, float width, float height) {
		renderDrawable(drawable, world.convertWorldCoordToScreenCoord(x), y, width, height);
	}

	/**
	 * renders a {@link TextureRegion} using {@link GameRenderManager}
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderTextureRegion(TextureRegion region, float x, float y, float width, float height) {
		game.getRenderManager().getBatch().draw(region, currentTotalXOffset + x, currentTotalYOffset + y, width, height);
	}

	/**
	 * same as {@link #renderTextureRegion(TextureRegion, float, float, float, float)}, but using global coordinates
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderTextureRegionWorld(TextureRegion region, float x, float y, float width, float height) {
		renderTextureRegion(region, world.convertWorldCoordToScreenCoord(x), y, width, height);
	}

	/**
	 * renders an animation in world
	 */
	public void renderSimpleAnimationWorld(SimpleAnimation animation, float x, float y, float width, float height, float delta) {
		renderTextureRegionWorld(animation.getFrameAfterTimePassed(delta), x, y, width, height);
	}

	/**
	 * draws the given String
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderText(String text, float x, float y) {
		getRenderManager().draw(text, currentTotalXOffset + x, currentTotalYOffset + y);
	}

	/**
	 * draws the given String
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderText(String text, Color color, float x, float y) {
		getRenderManager().draw(text, color, currentTotalXOffset + x, currentTotalYOffset + y);
	}

	/**
	 * draws the given String
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderText(String text, Color color, float x, float y, float size) {
		getRenderManager().draw(text, color, currentTotalXOffset + x, currentTotalYOffset + y, size);
	}

	/**
	 * same as {@link #renderText(String, float, float)}, but using global coordinates
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderTextWorld(String text, float x, float y) {
		renderText(text, world.convertWorldCoordToScreenCoord(x), y);
	}

	/**
	 * same as {@link #renderText(String, Color, float, float)}, but using global coordinates
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderTextWorld(String text, Color color, float x, float y) {
		renderText(text, color, world.convertWorldCoordToScreenCoord(x), y);
	}

	/**
	 * same as {@link #renderText(String, Color, float, float, float)}, but using global coordinates
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderTextWorld(String text, Color color, float x, float y, float size) {
		renderText(text, color, world.convertWorldCoordToScreenCoord(x), y, size);
	}

	public GameRenderManager getRenderManager() {
		return game.getRenderManager();
	}

	/**
	 * @return global coordinate of this worldrenderer (left screen edge)
	 */
	public float getWorldCameraXPos() {
		return -xOffset - screenShakeX + world.getPlayer().getProgress();
	}

	private float getBlockPositionFromLocalIndex(int index) {
		return world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(index));
	}

	/**
	 * sets current position of the world renderer (world view)
	 */
	public void setCameraXPosition(float x) {
		this.xOffset = x;
	}

	public float getScreenShakeX() {
		return screenShakeX;
	}

	public float getScreenShakeY() {
		return screenShakeY;
	}

	/**
	 * @return the target x position of the worldrenderer
	 */
	public float getTargetX() {
		return targetX;
	}

	/**
	 * sets the camera position of the world renderer (also gets set as target position)
	 */
	public void setXCameraOffsetAbsolute(float x) {
		setCameraXPosition(x);
		targetX = x;
		movementSpeedX = 0;
	}

	/**
	 * sets the y offset of the world renderer (x offset is not supported)
	 */
	public void setWorldRendererYOffset(float yOffset) {
		rendererYOffset = yOffset;
	}

	public float getCurrentTotalXOffset() {
		return currentTotalXOffset;
	}

	public float getCurrentTotalYOffset() {
		return currentTotalYOffset;
	}

	public World getWorld() {
		return world;
	}

	public void onResize() {
		for (IBackgroundLayer layer : backgroundLayers) {
			layer.onResize(this);
		}
	}
}
