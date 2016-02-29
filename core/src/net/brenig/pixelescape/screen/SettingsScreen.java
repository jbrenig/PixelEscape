package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
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
import net.brenig.pixelescape.screen.ui.general.PixelDialog;
import net.brenig.pixelescape.screen.ui.general.StageManager;

/**
 * Screen that provides user settings<br></br>
 * Currently used for Music/Sound volume
 */
public class SettingsScreen extends PixelScreen {

	private final StageManager uiStage;
	private final Table uiLayout;
	private final Table headLayout;


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
		{
			Table soundControl = new Table();
			soundControl.pad(4);

			Label txtSound = new Label("Sound:", game.getSkin());
			txtSound.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
			soundControl.add(txtSound).padRight(8.0F);

			Slider sliderSound = new Slider(0, 1F, 0.01F, false, game.getSkin(), "default");
			sliderSound.setValue(game.gameSettings.getSoundVolume());
			sliderSound.setAnimateDuration(0.2F);
			sliderSound.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					game.gameSettings.setSoundVolume(((Slider) actor).getValue());
					if (!((Slider) actor).isDragging()) {
						game.getGameAssets().getPlayerChrashedSound().play(game.gameSettings.getSoundVolume());
					}
				}
			});
			soundControl.add(sliderSound).fillX().padBottom(4.0F);

			uiLayout.add(soundControl).fillX().padBottom(20);
			uiLayout.row();
		}

		//Music
		{
			Table musicControl = new Table();
			musicControl.pad(4);

			Label txtMusic = new Label("Music:", game.getSkin());
			txtMusic.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
			musicControl.add(txtMusic).padRight(8.0F);
			Slider sliderMusic = new Slider(0, 1F, 0.01F, false, game.getSkin(), "default");
			sliderMusic.setValue(game.gameSettings.getMusicVolume());
			sliderMusic.setAnimateDuration(0.2F);
			sliderMusic.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					game.gameSettings.setMusicVolume(((Slider) actor).getValue());
					game.gameMusic.updateMusicVolume();
				}
			});
			musicControl.add(sliderMusic).fillX().padBottom(4.0F);

			uiLayout.add(musicControl).fillX().padBottom(20);
			uiLayout.row();
		}
		//Back Button
		{
			TextButton btnBack = new TextButton("Go Back", game.getSkin());
			btnBack.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					game.showMainMenu();
				}
			});

			uiLayout.add(btnBack);
		}
		//Add ui elements to stage

		//Head controls
		uiStage.getRootTable().top().right().pad(4);
		headLayout = Utils.createUIHeadLayout(game);

		Utils.addSoundAndMusicControllerToLayout(game, headLayout);

		if (game.gameConfig.debugSettingsAvailable()) {
			final ImageButton btnSettings = new ImageButton(game.getSkin(), "settings");
			btnSettings.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					final PixelDialog d = new PixelDialog("Debug Settings", game.getSkin());
					d.setPrefWidth(uiStage.getStageViewport().getWorldWidth() * 0.8F);
					d.setWidth(uiStage.getStageViewport().getWorldWidth() * 0.8F);
					d.setMovable(false);
					d.label("Do you want to open DEBUG Settings?");
					d.label("(This is only useful to beta testers)");
					{
						TextButton btnYes = new TextButton("Yes", game.getSkin());
						btnYes.addListener(new ClickListener() {
							@Override
							public void clicked(InputEvent event, float x, float y) {
								game.setScreen(new DebugSettingsScreen(game));
								d.hide();
							}
						});
						d.button(btnYes);
					}
					{
						TextButton btnNo = new TextButton("No", game.getSkin());
						btnNo.addListener(new ClickListener() {
							@Override
							public void clicked(InputEvent event, float x, float y) {
								//show debug screen
								d.hide();
							}
						});
						d.button(btnNo);
					}
					d.init();
					d.show(uiStage.getUiStage());
				}
			});
			btnSettings.getImageCell().fill().expand();
			headLayout.add(btnSettings);

		}

		Utils.addFullScreenButtonToTable(game, headLayout);
		uiStage.add(headLayout);
		//Main Layout
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
