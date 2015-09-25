package net.brenig.pixelescape.render.overlay;

import net.brenig.pixelescape.screen.GameScreen;

/**
 * An Overlay is used to draw UI or similar on top of the main game
 * Created by Jonas Brenig on 06.08.2015.
 */
public abstract class Overlay {
	/**
	 * Parent GameScreen instance
	 */
	protected GameScreen screen;

	public Overlay(GameScreen screen) {
		this.screen = screen;
	}

	/**
	 * Called when the Overly gets displayed to the player
	 */
	public void show() {}

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
	 * @return true if the score widget should be hidden
	 */
	public boolean shouldHideScore() {
		return false;
	}

}
