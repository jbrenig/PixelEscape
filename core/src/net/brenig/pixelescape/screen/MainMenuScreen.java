package net.brenig.pixelescape.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.screen.ui.CurrentHighscoreLabel;
import net.brenig.pixelescape.screen.ui.StageManager;
import net.brenig.pixelescape.screen.ui.TwoStateImageButton;

/**
 * Created by Jonas Brenig on 02.08.2015.
 */
public class MainMenuScreen implements Screen {

	private final PixelEscape game;
	private StageManager uiStage;

	/**
	 * layout used to group main ui elements
	 */
	private Table menuLayout;

	/**
	 * layout used to group setting buttons
	 */
	private Table headLayout;

	private TextButton btnStart;
	private TextButton btnQuit;

	private ImageButton btnSettings;
	private TwoStateImageButton btnSound;
	private TwoStateImageButton btnMusic;

	public MainMenuScreen(final PixelEscape game) {
		this.game = game;
		//Setting up stage
		uiStage = new StageManager(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, game.cam));

		game.resetFontSize();

		headLayout = Utils.createUIHeadLayout(game);

		Utils.addSoundAndMusicControllerToLayout(game, headLayout);

		btnSettings = new ImageButton(game.skin, "settings");
		btnSettings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new SettingsScreen(game));
			}
		});
		headLayout.add(btnSettings);



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
		menuLayout.add(new CurrentHighscoreLabel()).padBottom(40);
		menuLayout.row();
		menuLayout.add(btnStart).padBottom(40);
		menuLayout.row();
		menuLayout.add(btnQuit);

		uiStage.getRootTable().top().right().pad(4);
		uiStage.add(headLayout);
		uiStage.addActorToStage(menuLayout);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(uiStage.getInputProcessor());
		game.resetFontSize();
		uiStage.updateViewportToScreen();
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
		uiStage.updateViewport(width, height, true);
		menuLayout.invalidateHierarchy();
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
	}

	@Override
	public void dispose() {
		uiStage.dispose();
	}
}
