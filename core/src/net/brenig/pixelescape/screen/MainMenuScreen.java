package net.brenig.pixelescape.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.ui.CurrentHighscoreLabel;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.StageManager;
import net.brenig.pixelescape.render.ui.general.TabbedStack;

/**
 * PixelEscape MainMenu
 */
public class MainMenuScreen extends PixelScreen {

	private final StageManager uiStage;

	/**
	 * layout used to group main ui elements
	 */
	private final Table mainUiLayout;

	/**
	 * layout used to group setting buttons
	 */
	private final Table buttonPanelLayout;
	private final CurrentHighscoreLabel highscoreLabel;
	private final TabbedStack gmImageStack;

	public MainMenuScreen(final PixelEscape game) {
		super(game);
		//Setting up stage
		uiStage = new StageManager(game.getRenderManager());

		game.getRenderManager().resetFontSize();

		//Settings Button Panel
		buttonPanelLayout = Utils.createUIHeadLayout(game);
		//music and sound
		Utils.addSoundAndMusicControllerToLayout(game, buttonPanelLayout);
		//settings
		ImageButton btnSettings = new ImageButton(game.getSkin(), "settings");
		btnSettings.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new SettingsScreen(game));
			}
		});
		btnSettings.getImageCell().fill().expand();
		buttonPanelLayout.add(btnSettings);
		buttonPanelLayout.invalidateHierarchy();
		//fullscreen
		Utils.addFullScreenButtonToTable(game, buttonPanelLayout);

		//Main UI Table
		mainUiLayout = new Table();
		mainUiLayout.setFillParent(true);
		mainUiLayout.setPosition(0, 0);
		mainUiLayout.center();

		//Center UI Table
		Table centerTable = new Table();

		//PixelEscape Heading
		Label header = new Label("PixelEscape", game.getSkin());
		header.setHeight(150);

		centerTable.padTop(0);
		centerTable.add(header);
		centerTable.row();

		//GameMode Image
		gmImageStack = new TabbedStack();

		//init gamemodes
		for(GameMode mode : PixelEscape.gameModes) {
			Image gameModeImageArcade = new Image(mode.getIcon(game.getGameAssets()));
			gameModeImageArcade.setRotation(5);
			gmImageStack.add(gameModeImageArcade);
		}

		gmImageStack.setCurrentElement(game.userData.getLastGameMode());
		centerTable.add(gmImageStack).pad(20, 0, 10, 0).size(144, 48);
		centerTable.row();

		//Highscore Label
		highscoreLabel = new CurrentHighscoreLabel(getGameMode());
		centerTable.add(highscoreLabel).padBottom(40);
		centerTable.row();

		//Buttons
		Table centerButtons = new Table();

		//Start Button
		TextButton btnStart = new TextButton("Start game", game.getSkin());
		btnStart.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				game.setScreen(new GameScreen(game, getGameMode()));
			}
		});
		centerButtons.add(btnStart).padBottom(40).fillX();

		//Quit Button
		if(game.gameConfig.canQuitGame()) {
			TextButton btnQuit = new TextButton("Quit game", game.getSkin());
			btnQuit.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
				}
			});
			centerButtons.row();
			centerButtons.add(btnQuit).fillX();
		}
		centerTable.add(centerButtons);

		//Left spacer
		mainUiLayout.add(new HorizontalSpacer());

		//Left Arrow
		Button arrow_left = new Button(game.getSkin(), "arrow_left");
		arrow_left.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gmImageStack.last();
				highscoreLabel.setGameMode(getGameMode());
			}
		});
		mainUiLayout.add(arrow_left).size(96, 256);

		//Main UI
		mainUiLayout.add(centerTable);

		//Right Arrow
		Button arrow_right = new Button(game.getSkin(), "arrow_right");
		arrow_right.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gmImageStack.next();
				highscoreLabel.setGameMode(getGameMode());
			}
		});
		mainUiLayout.add(arrow_right).size(96, 256);

		//Right spacer
		mainUiLayout.add(new HorizontalSpacer());

		//Move Arrows to front
		arrow_left.toFront();
		arrow_right.toFront();

		//Add ui elements to stage
		uiStage.getRootTable().top().right().pad(4);
		uiStage.add(buttonPanelLayout);
		uiStage.addActorToStage(mainUiLayout);
	}

	private GameMode getGameMode() {
		if(gmImageStack == null) {
			return null;
		}
		return PixelEscape.gameModes[gmImageStack.getCurrentElement()];
	}

	@Override
	public void show() {
		game.getRenderManager().resetFontSize();
		uiStage.updateViewportToScreen();
		mainUiLayout.invalidateHierarchy();
		buttonPanelLayout.invalidateHierarchy();
		game.gameMusic.playOrFadeInto(game.getGameAssets().getMainMenuMusic());
		Gdx.input.setInputProcessor(uiStage.getInputProcessor());
	}

	@Override
	public void render(float delta) {
		uiStage.act(delta);
		uiStage.draw(game.getRenderManager());
	}

	@Override
	public void resize(int width, int height) {
		uiStage.updateViewport(width, height, true);
		mainUiLayout.invalidateHierarchy();
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
		game.userData.setLastGameMode(gmImageStack.getCurrentElement());
		uiStage.dispose();
	}
}
