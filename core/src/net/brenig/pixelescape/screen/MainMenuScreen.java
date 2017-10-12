package net.brenig.pixelescape.screen;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.data.constants.StyleNames;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.ui.CurrentHighscoreLabel;
import net.brenig.pixelescape.render.ui.general.StageManager;
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack;

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
	private final SwipeTabbedStack gmImageStack;

	public MainMenuScreen(final PixelEscape game) {
		super(game);
		//Setting up stage
		uiStage = new StageManager(game.getRenderManager());


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
		header.setFontScale(1.0F);

		centerTable.padTop(0);
		centerTable.add(header);
		centerTable.row();

		//GameMode Image
		gmImageStack = new SwipeTabbedStack(SwipeTabbedStack.DEFAULT_ANIMATION_X_OFFSET);
		//init gamemodes
		for (GameMode mode : game.getGameConfig().getAvailableGameModes()) {
			Image gameModeImage = new Image(mode.createIcon(game.getGameAssets()));
//			gameModeImage.setRotation(PixelEscape.rand.nextFloat() * 10 - 5F);
			gameModeImage.setScaling(Scaling.fit);
			gmImageStack.add(gameModeImage);
		}

		gmImageStack.setCurrentElement(game.getUserData().getLastGameMode());
		centerTable.add(gmImageStack).pad(20, 0, 10, 0).height(48).fillX();
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
		btnStart.getLabel().setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		centerButtons.add(btnStart).padBottom(8).fillX();

		//Quit Button
		if (game.getGameConfig().getCanQuitGame()) {
			TextButton btnQuit = new TextButton("Quit game", game.getSkin());
			btnQuit.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					Gdx.app.exit();
				}
			});
			btnQuit.getLabel().setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
			centerButtons.row();
			centerButtons.add(btnQuit).fillX();
		}
		centerTable.add(centerButtons);

		//Left Arrow
		Button arrow_left = new Button(game.getSkin(), StyleNames.BUTTON_ARROW_LEFT);
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
		Button arrow_right = new Button(game.getSkin(), StyleNames.BUTTON_ARROW_RIGHT);
		arrow_right.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				gmImageStack.next();
				highscoreLabel.setGameMode(getGameMode());
			}
		});
		mainUiLayout.add(arrow_right).size(96, 256);

		//Move Arrows to front
		arrow_left.toFront();
		arrow_right.toFront();

		//Add ui elements to stage
		uiStage.getRootTable().top().right().pad(4);
		uiStage.add(buttonPanelLayout);
		uiStage.addActorToStage(mainUiLayout);
	}

	private GameMode getGameMode() {
		if (gmImageStack == null) {
			throw new IllegalStateException("GameMode select UI not initialized!");
		}
		return game.getGameConfig().getAvailableGameModes().get(gmImageStack.getCurrentElement());
	}

	@Override
	public void show() {
		game.getRenderManager().resetFontSize();
		uiStage.updateViewportToScreen();
		mainUiLayout.invalidateHierarchy();
		buttonPanelLayout.invalidateHierarchy();
		game.getGameMusic().playOrFadeInto(game.getGameAssets().getMainMenuMusic());
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
		game.getUserData().setLastGameMode(gmImageStack.getCurrentElement());
		uiStage.dispose();
	}
}
