package net.brenig.pixelescape.render.overlay;

import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Empty Overlay used to avoid null in GameScreen
 */
public final class EmptyOverlay extends Overlay {

	public EmptyOverlay(final GameScreen screen) {
		super(screen);
	}

	@Override
	public void show() {
		screen.resetInputManager();
	}

	@Override
	public void render(float delta) {
	}

	@Override
	public void pause() {
		screen.showGamePausedOverlay();
	}

	@Override
	public void resume() {
		screen.showGamePausedOverlay();
	}

	@Override
	public void onResize(int width, int height) {
		if (screen.isInitialized() && GameDebugSettings.Companion.get("AUTO_PAUSE")) {
			screen.showGamePausedOverlay();
		}
	}

	@Override
	public boolean shouldHideGameUI() {
		return false;
	}

	@Override
	public boolean shouldPauseOnEscape() {
		return true;
	}
}
