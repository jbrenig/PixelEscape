package net.brenig.pixelescape.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import net.brenig.pixelescape.PixelEscape;

/**
 * Handles Player input for the game
 */
public class InputManager implements InputProcessor {

	private boolean isTouched = false;
	private boolean isSpaceDown = false;
	private boolean isEscapeDown = false;

	/**
	 * Cursor timer
	 * -1 if idle
	 * 0 if not idle
	 * >0 if idle for n seconds
	 */
	private float cursorIdleTimer = 0;
	private static final float cursorIdleTime = 3;

	public void updateMouseVisibility(float delta, boolean canHide) {
		if (canHide && cursorIdleTimer >= 0) {
			cursorIdleTimer += delta;
		}
		if (cursorIdleTimer > cursorIdleTime) {
			cursorIdleTimer = -1;
			updateMouseVisibility();
		}
	}


	private void updateMouseVisibility() {
		if (PixelEscape.getPixelEscape().getGameConfig().canHideCursor()) {
			Gdx.input.setCursorCatched(cursorIdleTimer < 0);
		}
	}

	public void resetMouseVisibility() {
		if (PixelEscape.getPixelEscape().getGameConfig().canHideCursor()) {
			cursorIdleTimer = 0;
			Gdx.input.setCursorCatched(false);
		}
	}

	@Override
	public boolean keyDown(int keycode) {
		boolean changed = false;
		if (keycode == Input.Keys.ESCAPE) {
			isEscapeDown = true;
			changed = true;
		}
		if (keycode == Input.Keys.SPACE) {
			isSpaceDown = true;
			changed = true;
		}
		return changed;
	}

	@Override
	public boolean keyUp(int keycode) {
		boolean changed = false;
		if (keycode == Input.Keys.ESCAPE) {
			isEscapeDown = false;
			changed = true;
		}
		if (keycode == Input.Keys.SPACE) {
			isSpaceDown = false;
			changed = true;
		}
		return changed;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		isTouched = true;
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		isTouched = false;
		return true;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		if(isTouched) return true;
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		cursorIdleTimer = 0;
		updateMouseVisibility();
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		return false;
	}

	public boolean isTouched() {
		return isTouched;
	}

	public boolean isSpaceDown() {
		return isSpaceDown;
	}

	public void resetTouchedState() {
		isTouched = false;
		isSpaceDown = false;
		isEscapeDown = false;
	}

	public void refreshButtonState() {
		isTouched = Gdx.input.isTouched();
		isSpaceDown = Gdx.input.isKeyPressed(Input.Keys.SPACE);
		isEscapeDown = Gdx.input.isKeyPressed(Input.Keys.ESCAPE);
	}

	public boolean isEscapeDown() {
		return isEscapeDown;
	}
}
