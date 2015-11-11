package net.brenig.pixelescape.render.overlay;

import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Created by Jonas Brenig on 15.08.2015.
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
		if (!screen.isFirstUpdate() && GameDebugSettings.get("AUTO_PAUSE")) {
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
