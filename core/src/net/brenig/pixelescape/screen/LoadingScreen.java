package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Screen;

/**
 * Currently unused
 */
public class LoadingScreen implements Screen {

	private Screen nextScreen;

	/**
	 * Loads assets and switches to given screen when finished
	 * @param screen nextScreen to show
	 */
	public LoadingScreen(Screen screen) {
		nextScreen = screen;
	}

	@Override
	public void show() {

	}

	@Override
	public void render(float delta) {

	}

	@Override
	public void resize(int width, int height) {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {

	}
}
