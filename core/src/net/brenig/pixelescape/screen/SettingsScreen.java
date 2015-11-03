package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.screen.ui.StageManager;

/**
 * Created by Jonas Brenig on 26.09.2015.
 */
public class SettingsScreen extends PixelScreen {

	private StageManager uiStage;
	private Table uiLayout;
	private Table headLayout;


	public SettingsScreen(final PixelEscape game) {
		super(game);
		//Setting up stage
		uiStage = new StageManager(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, game.cam));

		game.resetFontSize();

		//configure main layout
		uiLayout = new Table();
		uiLayout.setFillParent(true);
		uiLayout.setPosition(0, 0);
		uiLayout.center();
		uiLayout.padTop(30).padBottom(20);

		Label header = new Label("Settings", game.getSkin());
		header.setHeight(150);
		header.setScale(2);

		uiLayout.add(header).padBottom(60);
		uiLayout.row();

		//Sound
		Table soundControl = new Table();
		soundControl.pad(4);

		Label txtSound = new Label("Sound:", game.getSkin());
		txtSound.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		soundControl.add(txtSound).padRight(8.0F);

		Slider sliderSound = new Slider(0, 1F, 0.01F, false, game.getSkin(), "default");
		sliderSound.setValue(game.gameSettings.soundVolume);
		sliderSound.setAnimateDuration(0.2F);
		sliderSound.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.gameSettings.soundVolume = ((Slider) actor).getValue();
				if(!((Slider) actor).isDragging()) {
					game.getGameOverSound().play(game.gameSettings.soundVolume);
				}
			}
		});
		soundControl.add(sliderSound).fillX().padBottom(4.0F);

		uiLayout.add(soundControl).fillX().padBottom(20);
		uiLayout.row();

		//Music
		Table musicControl = new Table();
		musicControl.pad(4);

		Label txtMusic = new Label("Music:", game.getSkin());
		txtMusic.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		musicControl.add(txtMusic).padRight(8.0F);
		Slider sliderMusic = new Slider(0, 1F, 0.01F, false, game.getSkin(), "default");
		sliderMusic.setValue(game.gameSettings.musicVolume);
		sliderMusic.setAnimateDuration(0.2F);
		sliderMusic.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.gameSettings.musicVolume = ((Slider) actor).getValue();
				game.gameMusic.updateMusicVolume();
			}
		});
		musicControl.add(sliderMusic).fillX().padBottom(4.0F);

		uiLayout.add(musicControl).fillX().padBottom(20);
		uiLayout.row();

		TextButton btnBack = new TextButton("Go Back", game.getSkin());
		btnBack.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.showMainMenu();
			}
		});

		uiLayout.add(btnBack);

		//Add ui elements to stage
		uiStage.getRootTable().top().right().pad(4);
		uiStage.add(headLayout = Utils.createDefaultUIHeadControls());
		uiStage.addActorToStage(uiLayout);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(uiStage.getInputProcessor());
		game.resetFontSize();
		uiStage.updateViewportToScreen();
		uiLayout.invalidateHierarchy();
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
