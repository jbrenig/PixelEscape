package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.screen.GameScreen;

/**
 * An Overlay is used to draw UI or similar on top of the main game
 */
public abstract class Overlay {
	/**
	 * Parent GameScreen instance
	 */
	protected final GameScreen screen;

	public Overlay(GameScreen screen) {
		this.screen = screen;
	}

	/**
	 * Called when the Overly gets displayed to the player
	 */
	public void show() {}

	/**
	 * method to render effects on the world<br></br>
	 * (gets called before game ui is rendered)
	 * @param delta time passed between frames in seconds
	 */
	public void renderFirst(float delta) {}

	/**
	 * renders the overlay
	 *
	 * @param delta time passed between frames in seconds
	 */
	public abstract void render(float delta);

	/**
	 * Called when the game window is resized<br>
	 * Use this to update UI-Elements
	 *
	 * @param width  new width
	 * @param height new height
	 */
	public void onResize(int width, int height) {
		if (!screen.isFirstUpdate()) {
			screen.showGamePausedOverlay();
		}
	}

	/**
	 * Called when the GameScreen gets paused
	 */
	public void pause() {
		screen.showGamePausedOverlay();
	}

	/**
	 * Gets called when the GameScreen gets resumed
	 */
	public void resume() {
		screen.showGamePausedOverlay();
	}

	/**
	 * Gets called when the Overlay is destroyed
	 */
	public void dispose() {}

	/**
	 * @return true if the default game-ui should be hidden
	 */
	public boolean shouldHideGameUI() {
		return true;
	}

	/**
	 * gets called every tick to allow dynamic changing of paused vs unpaused
	 *
	 * @return true if the game should be paused
	 */
	public boolean doesPauseGame() {
		return false;
	}

	/**
	 * @return true if GameScreen should hide the cursor after time, (GameScreen InputManager needs focus)
	 */
	public boolean canHideCursor() {
		return true;
	}

	/**
	 * Renders a black, transparent overlay
	 */
	protected void renderScreenTint() {
		renderScreenTint(0.3F);
	}

	/**
	 * Renders a black, transparent overlay
	 */
	protected void renderScreenTint(float alpha) {
		renderScreenTint(0, 0, 0, alpha);
	}

	/**
	 * Renders a coloured overlay in the given color
	 */
	protected void renderScreenTint(float r, float g, float b, float a) {
		screen.game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		screen.game.shapeRenderer.setColor(r, g, b, a);
		screen.game.shapeRenderer.rect(0, 0, screen.game.gameSizeX, screen.game.gameSizeY);
		screen.game.shapeRenderer.end();
		Gdx.gl.glDisable(GL20.GL_BLEND);
	}

	/**
	 * called whenever the music status gets updated (might get called without anything changed)
	 * @param play whether the music is playing or not
	 */
	@SuppressWarnings("EmptyMethod")
	public void updateMusic(boolean play) {}

	/**
	 * @return whether the game should open the game paused overlay when the escape key is pressed
	 */
	public boolean shouldPauseOnEscape() {
		return false;
	}
}
