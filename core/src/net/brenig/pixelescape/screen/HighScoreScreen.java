package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Reference;

/**
 * Created by Jonas Brenig on 20.10.2015.
 */
public class HighScoreScreen implements Screen {

	private final PixelEscape game;
	private final Stage uiStage;

	public HighScoreScreen(final PixelEscape game) {
		this.game = game;
		//Setting up stage
		uiStage = new Stage(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, game.cam));
		uiStage.setDebugAll(Reference.DEBUG_UI);
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
