package net.brenig.pixelescape.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

/**
 * Created by Jonas Brenig on 20.08.2015.
 */
public class InputManager implements InputProcessor {

	private boolean isTouched = false;
	private boolean isSpaceDown = false;

	@Override
	public boolean keyDown(int keycode) {
		if(keycode == Input.Keys.SPACE) {
			isSpaceDown = true;
			return true;
		}
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		if(keycode == Input.Keys.SPACE) {
			isSpaceDown = false;
			return true;
		}
		return false;
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
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
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
	}

	public void refreshButtonState() {
		isTouched = Gdx.input.isTouched();
		isSpaceDown = Gdx.input.isKeyPressed(Input.Keys.SPACE);
	}
}
