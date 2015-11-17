package net.brenig.pixelescape.screen;


import com.badlogic.gdx.Gdx;
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
public class MainMenuScreen extends PixelScreen {

	private StageManager uiStage;

	/**
	 * layout used to group main ui elements
	 */
	private Table menuLayout;

	/**
	 * layout used to group setting buttons
	 */
	private Table headLayout;

	private TwoStateImageButton btnSound;
	private TwoStateImageButton btnMusic;

	public MainMenuScreen(final PixelEscape game) {
		super(game);
		//Setting up stage
		uiStage = new StageManager(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, game.cam));

		game.resetFontSize();

		headLayout = Utils.createUIHeadLayout(game);

		Utils.addSoundAndMusicControllerToLayout(game, headLayout);

		ImageButton btnSettings = new ImageButton(game.getSkin(), "settings");
		btnSettings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new SettingsScreen(game));
			}
		});
		btnSettings.getImageCell().fill().expand();
		headLayout.add(btnSettings);

		headLayout.invalidateHierarchy();

		Utils.addFullScreenButtonToTable(game, headLayout);



		menuLayout = new Table();
		menuLayout.setFillParent(true);
		menuLayout.setPosition(0, 0);
		menuLayout.center();

		Label header = new Label("PixelEscape", game.getSkin());
		header.setHeight(150);

		TextButton btnStart = new TextButton("Start game", game.getSkin());
		btnStart.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new GameScreen(game));
			}
		});



		menuLayout.padTop(30);
		menuLayout.add(header).padBottom(60);
		menuLayout.row();
		menuLayout.add(new CurrentHighscoreLabel()).padBottom(40);
		menuLayout.row();
		menuLayout.add(btnStart).padBottom(40);

		if(game.gameConfig.canQuitGame()) {
			TextButton btnQuit = new TextButton("Quit game", game.getSkin());
			btnQuit.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
				}
			});
			menuLayout.row();
			menuLayout.add(btnQuit);
		}

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
		game.gameMusic.playOrFadeInto(game.getGameAssets().getMainMenuMusic());
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
