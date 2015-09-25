package net.brenig.pixelescape.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Reference;

/**
 * Created by Jonas Brenig on 02.08.2015.
 */
public class MainMenuScreen implements Screen {

	private final PixelEscape game;

	private Stage uiStage;
	private Table menuLayout;
	private HorizontalGroup headLayout;

	private TextButton btnStart;
	private TextButton btnQuit;

	private ImageButton btnSettings;

	public MainMenuScreen(final PixelEscape game) {
		this.game = game;
		//Setting up stage
		uiStage = new Stage(new ScreenViewport());
		uiStage.setDebugAll(Reference.DEBUG_UI);

		game.resetFontSize();

		headLayout = new HorizontalGroup();
		headLayout.setFillParent(true);
		headLayout.setPosition(0, 0);
		headLayout.align(Align.right | Align.top);
		headLayout.reverse();
		headLayout.pad(10);

		btnSettings = new ImageButton(game.skin);

		headLayout.addActor(btnSettings);


		menuLayout = new Table();
		menuLayout.setFillParent(true);
		menuLayout.setPosition(0, 0);
		menuLayout.center();

		Label header = new Label("PixelEscape", game.skin);
		header.setHeight(150);

		btnStart = new TextButton("Start game", game.skin);
		btnStart.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new GameScreen(game));
			}
		});

		btnQuit = new TextButton("Quit game", game.skin);
		btnQuit.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Gdx.app.exit();
			}
		});

		menuLayout.padTop(30);
		menuLayout.add(header).padBottom(60);
		menuLayout.row();
		menuLayout.add(btnStart).padBottom(40);
		menuLayout.row();
		menuLayout.add(btnQuit);

		uiStage.addActor(headLayout);
		uiStage.addActor(menuLayout);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(uiStage);
		game.resetFontSize();
		uiStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
		menuLayout.invalidateHierarchy();
		headLayout.invalidateHierarchy();
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
		menuLayout.invalidateHierarchy();
		headLayout.invalidateHierarchy();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		uiStage.dispose();
	}

	@Override
	public void dispose() {
		uiStage.dispose();
	}
}
