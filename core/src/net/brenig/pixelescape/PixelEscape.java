package net.brenig.pixelescape;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import net.brenig.pixelescape.game.data.*;
import net.brenig.pixelescape.lib.LogHelperKt;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.GameRenderManager;
import net.brenig.pixelescape.screen.MainMenuScreen;
import net.brenig.pixelescape.screen.PixelScreen;

import java.util.Random;

/**
 * Main Game class
 */
public class PixelEscape extends Game {
	/**
	 * general Random instance
	 */
	public static final Random rand = new Random();

	/**
	 * singleton static instance
	 */
	private static PixelEscape instance;

	/**
	 * true if assets are loaded. Used for unloading when paused and in background
	 */
	private boolean assetsLoaded = false;

	private GameRenderManager renderManager;
	private GameAssets gameAssets;
	private GameMusic gameMusic;
	private GameSettings gameSettings;
	private GameDebugSettings gameDebugSettings;
	private UserData userData;

	/**
	 * Platform dependent GameConfiguration
	 */
	private final GameConfiguration gameConfig;

	private int gameSizeX = Reference.TARGET_RESOLUTION_X;
	private int gameSizeY = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;

	public PixelEscape() {
		//set default config
		this(new GameConfiguration());
	}

	public PixelEscape(GameConfiguration config) {
		super();
		gameConfig = config;
	}

	@Override
	public void create() {
		if (gameConfig.getLoggingEnabled()) {
			LogHelperKt.setGDXLogLevel(Application.LOG_DEBUG);
		} else {
			LogHelperKt.setGDXLogLevel(Application.LOG_NONE);
		}
		LogHelperKt.info("Main", "Starting up...");
		if (instance != null) {
			if (instance.assetsLoaded) {
				instance.dispose(); //needed?
			}
			LogHelperKt.warn("Critical Error! Game already initialized!");
		}
		instance = this;

		//initialize renderer
		renderManager = new GameRenderManager();

		//load everything needed
		initializeRendering();

		gameMusic = new GameMusic(this);

		//load settings
		gameSettings = new GameSettings();
		gameDebugSettings = new GameDebugSettings();

		//load userdata
		//currently only highscore
		userData = new UserData();
		//convert legacy savedata
		getUserData().updateSaveGames();

		//open main menu
		showMainMenu();

		LogHelperKt.log("Main", "Finished loading!");
	}

	public static PixelEscape getPixelEscape() {
		return instance;
	}

	public GameAssets getGameAssets() {
		return gameAssets;
	}

	@Override
	public void setScreen(Screen screen) {
		//reset font
		getRenderManager().resetFontSize();
		super.setScreen(screen);
	}

	@Override
	public void render() {
		// update game music
		getGameMusic().update(Gdx.graphics.getDeltaTime());

		// render frame
		renderManager.prepareRender();
		super.render();
		if (GameDebugSettings.get("SHOW_FPS")) {
			renderManager.begin();
			getFont().setColor(Color.RED);
			getRenderManager().resetFontSize();
			getFont().draw(getBatch(), "FPS " + Gdx.graphics.getFramesPerSecond(), 10, getGameSizeY() - 10);
		}
		renderManager.end();
	}

	@Override
	public void dispose() {
		saveUserData();
		unloadAssets();
		super.dispose();
	}

	public void saveUserData() {
		getGameSettings().saveToDisk();
		getUserData().saveToDisk();
	}

	public void unloadAssets() {
		renderManager.dispose();
		gameAssets.disposeAll();
		assetsLoaded = false;
	}

	@Override
	public void resume() {
		if (!assetsLoaded) {
			initializeRendering();
		}
		super.resume();
	}

	/**
	 * loads game assets and initializes rendering
	 */
	private void initializeRendering() {

		renderManager.initializeRendering();

		if (gameAssets == null) {
			gameAssets = new GameAssets();
		}

		gameAssets.initAll();

		renderManager.setGameAssets(gameAssets);

		assetsLoaded = true;
	}

	@Override
	public void resize(int width, int height) {
		LogHelperKt.log("Main", "Resizing...");

		final float targetHeight = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
		final float targetWidth = Reference.TARGET_RESOLUTION_X;
		final float targetRatio = targetHeight / targetWidth;
		final float sourceRatio = (float) height / (float) width;
		final float scale = sourceRatio > targetRatio ? targetWidth / width : targetHeight / height;
		gameSizeX = (int) Math.ceil(width * scale);
		gameSizeY = (int) Math.ceil(height * scale);

		renderManager.onResize(getGameSizeX(), getGameSizeY());

		LogHelperKt.log("Main", "new width: " + getGameSizeX() + ", new height: " + getGameSizeY());

		super.resize(width, height);
	}

	public void showMainMenu() {
		setScreen(new MainMenuScreen(this));
	}

	public float getScaledMouseX() {
		final float scale = (float) getGameSizeX() / Gdx.graphics.getWidth();
		return Gdx.input.getX() * scale;
	}

	public float getScaledMouseY() {
		final float scale = (float) getGameSizeY() / Gdx.graphics.getHeight();
		return Gdx.input.getY() * scale;
	}

	/**
	 * stops or starts music if settings have changed
	 */
	public void updateMusicPlaying() {
		if (!getGameSettings().isMusicEnabled()) {
			getGameMusic().fadeOutToStop(0.5F);
		}
		if (screen instanceof PixelScreen) {
			((PixelScreen) screen).updateMusic(getGameSettings().isMusicEnabled());
		}
	}

	/**
	 * goes or leaves fullscreen
	 */
	public void updateFullscreen() {
		if (getGameConfig().getCanGoFullScreen()) {
			if (getGameSettings().fullscreen) {
				final Graphics.DisplayMode oldMode = Gdx.graphics.getDisplayMode();
				Gdx.graphics.setFullscreenMode(oldMode);
			} else {
				Gdx.graphics.setWindowedMode(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y);
			}
		}
	}

	/**
	 * Main BitMap Font
	 */
	public BitmapFont getFont() {
		return getGameAssets().getDefaultFont();
	}

	/**
	 * Default button ninepatch
	 */
	public NinePatch getButtonNinePatch() {
		return getGameAssets().getButtonNinePatch();
	}

	/**
	 * Main Gui Skin
	 */
	public Skin getSkin() {
		return getGameAssets().getMainUiSkin();
	}

	/**
	 * Main Sprite Batch
	 */
	public SpriteBatch getBatch() {
		return renderManager.getBatch();
	}

	/**
	 * @return Game Renderer
	 */
	public GameRenderManager getRenderManager() {
		return renderManager;
	}

	public GameSettings getGameSettings() {
		return gameSettings;
	}

	public GameDebugSettings getGameDebugSettings() {
		return gameDebugSettings;
	}

	public UserData getUserData() {
		return userData;
	}

	public GameConfiguration getGameConfig() {
		return gameConfig;
	}

	public GameMusic getGameMusic() {
		return gameMusic;
	}

	public int getGameSizeX() {
		return gameSizeX;
	}

	public int getGameSizeY() {
		return gameSizeY;
	}
}
