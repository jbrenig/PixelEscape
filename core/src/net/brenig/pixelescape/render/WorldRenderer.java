package net.brenig.pixelescape.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;

/**
 * Helper class for rendering a {@link World}
 * Renders with y-Up
 */
public class WorldRenderer {

	private World world;
	private final PixelEscape game;

	private float rendererYOffset = 0;

	private float xPos = 0;
	private float targetX = 0;
	private float movementSpeedX = 0;

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

	/**
	 * calculate screen shake effect
	 * @param delta time since last tick
	 */
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

	/**
	 * initiates a screen shake effect
	 * @param x force on x axis
	 * @param y force on y axis
	 */
	public void applyForceToScreen(float x, float y) {
		if(x * Math.PI > screenShakeForceX) {
			screenShakeForceX = (float) (x * Math.PI);
		}
		if(y * Math.PI > screenShakeForceY) {
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
		return xPos;
	}

	public float getRendererYOffset() {
		return rendererYOffset;
	}

	private void moveScreen(float delta) {
		if(movementSpeedX != 0 && xPos != targetX) {
			if(targetX < xPos) {
				xPos -= Math.min(xPos - targetX, movementSpeedX * delta);
			} else {
				xPos += Math.min(targetX - xPos, movementSpeedX * delta);
			}
		}
	}

	/**
	 * Renders the World
	 */
	public void render(float delta) {
		if(game.gameDebugSettings.getBoolean("SCREEN_SHAKE")) {
			shakeScreen(delta);
		}
		moveScreen(delta);
		renderWorld();
		renderEntities(delta);
	}

	/**
	 * renders entities
	 */
	private void renderEntities(float delta) {
		for(Entity e : world.getEntityList()) {
			e.render(game, this, xPos + screenShakeX, rendererYOffset + screenShakeY, delta);
		}
	}

	/**
	 * renders terrain
	 */
	private void renderWorld() {
		game.getRenderManager().beginFilledShape();
		game.getRenderManager().getShapeRenderer().setColor(0, 0, 0, 1);

		for (int index = 0; index < world.getBlockBufferSize(); index++) {
			world.getTerrainPairForIndex(index).render(game, world, xPos + getBlockPositionFromLocalIndex(index) + screenShakeX, rendererYOffset, screenShakeY, Gdx.graphics.getDeltaTime());
		}
	}

	/**
	 * renders a rectangel using {@link com.badlogic.gdx.graphics.glutils.ShapeRenderer} and {@link GameRenderManager}
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderRect(float x, float y, float width, float height) {
		game.getRenderManager().getShapeRenderer().rect(xPos + screenShakeX + x, rendererYOffset + screenShakeY + y, width, height);
	}

	/**
	 * same as {@link #renderRect(float, float, float, float)}, but using global coordinates
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderRectAbsolute(float x, float y, float width, float height) {
		renderRect(world.convertWorldCoordToScreenCoord(x), y, width, height);
	}


	/**
	 * renders a {@link Drawable} using {@link GameRenderManager}
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderDrawable(Drawable drawable, float x, float y, float width, float height) {
		drawable.draw(game.getRenderManager().getBatch(), xPos + screenShakeX + x, rendererYOffset + screenShakeY + y, width, height);
	}

	/**
	 * same as {@link #renderDrawable(Drawable, float, float, float, float)}, but using global coordinates
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderDrawableWorld(Drawable drawable, float x, float y, float width, float height) {
		renderDrawable(drawable, world.convertWorldCoordToScreenCoord(x), y, width, height);
	}

	/**
	 * renders a {@link TextureRegion} using {@link GameRenderManager}
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void renderTextureRegion(TextureRegion region, float x, float y, float width, float height) {
		game.getRenderManager().getBatch().draw(region, xPos + screenShakeX + x, rendererYOffset + screenShakeY + y, width, height);
	}

	/**
	 * same as {@link #renderTextureRegion(TextureRegion, float, float, float, float)}, but using global coordinates
	 *
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

	public GameRenderManager getRenderManager() {
		return game.getRenderManager();
	}

	/**
	 * @return global coordinate of this worldrenderer (left screen edge)
	 */
	public float getWorldCameraXPos() {
		return xPos + screenShakeX + world.getPlayer().getProgress();
	}

	private float getBlockPositionFromLocalIndex(int index) {
		return world.convertWorldIndexToScreenCoordinate(world.convertLocalBlockToWorldBlockIndex(index));
	}

	/**
	 * sets current position of the world renderer (world view)
	 */
	public void setCameraXPosition(float x) {
		this.xPos = x;
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
}
