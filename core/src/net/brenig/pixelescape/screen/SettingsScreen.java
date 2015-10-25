package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Reference;

/**
 * Created by Jonas Brenig on 26.09.2015.
 */
public class SettingsScreen implements Screen {

	private PixelEscape game;

	private Stage uiStage;
	private Table uiLayout;

	private TextButton btnBack;



	public SettingsScreen(final PixelEscape game) {
		this.game = game;
		//Setting up stage
		uiStage = new Stage(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, game.cam));
		uiStage.setDebugAll(Reference.DEBUG_UI);

		game.resetFontSize();


		uiLayout = new Table();
		uiLayout.setFillParent(true);
		uiLayout.setPosition(0, 0);
		uiLayout.center();

		Label header = new Label("[WIP]", game.skin);
		header.setHeight(150);


		btnBack = new TextButton("Go Back", game.skin);
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.showMainMenu();
			}
		});

		uiLayout.padTop(30);
		uiLayout.add(header).padBottom(60);
		uiLayout.row();
		uiLayout.add(btnBack);

		uiStage.addActor(uiLayout);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(uiStage);
		game.resetFontSize();
		uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		uiLayout.invalidateHierarchy();
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		uiStage.act(delta);
		uiStage.draw();
	}

	@Override
	public void resize(int width, int height) {
		uiStage.getViewport().update(width, height, true);
		uiLayout.invalidateHierarchy();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
		game.gameSettings.saveToDisk();
	}

	@Override
	public void dispose() {
		uiStage.dispose();
	}
}
