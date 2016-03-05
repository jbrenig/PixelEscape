package net.brenig.pixelescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.data.GameConfiguration;
import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.game.data.GameMusic;
import net.brenig.pixelescape.game.data.GameSettings;
import net.brenig.pixelescape.game.data.UserData;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.game.gamemode.GameModeArcade;
import net.brenig.pixelescape.game.gamemode.GameModeClassic;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.GameRenderManager;
import net.brenig.pixelescape.screen.MainMenuScreen;
import net.brenig.pixelescape.screen.PixelScreen;

import java.util.Random;

public class PixelEscape extends Game {

	private static PixelEscape instance;

	public static final Random rand = new Random();

	private boolean assetsLoaded = false;

	private GameRenderManager renderManager;


	private GameAssets gameAssets;
	public GameSettings gameSettings;
	public GameDebugSettings gameDebugSettings;
	public UserData userData;
	public final GameConfiguration gameConfig;
	public GameMusic gameMusic;

	public int gameSizeX = Reference.TARGET_RESOLUTION_X;
	public int gameSizeY = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
	private float scale = 1.0F;


	public static final GameMode[] gameModes = {new GameModeClassic(), new GameModeArcade()};

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
		LogHelper.log("Main", "Starting up...");
		if(instance != null) {
			if(instance.assetsLoaded) {
				instance.dispose(); //needed?
			}
			LogHelper.warn("Critical Error! Game already initialized!");
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
		userData.updateSaveGames();

		//open main menu
		showMainMenu();

		LogHelper.log("Main", "Finished loading!");
	}

	public static PixelEscape getPixelEscape() {
		return instance;
	}

	@Deprecated
	public void renderTextureRegion(TextureRegion region, float x, float y) {
		getBatch().draw(region, x, y);
	}

	@Deprecated
	public void renderTextureRegion(TextureRegion region, float x, float y, float width, float height) {
		getBatch().draw(region, x, y, width, height);
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
		gameMusic.update(Gdx.graphics.getDeltaTime());
		renderManager.prepareRender();
		super.render();
		if(GameDebugSettings.get("SHOW_FPS")) {
			renderManager.begin();
			getFont().setColor(Color.RED);
			getRenderManager().resetFontSize();
			getFont().draw(getBatch(), "FPS " + Gdx.graphics.getFramesPerSecond(), 10, gameSizeY - 10);
		}
		renderManager.end();
	}

//	private void prepareRender() {
//		Gdx.gl.glClearColor(1, 1, 1, 1);
//		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//		// tell the camera to update its matrices.
//		cam.update();
//	}

	@Override
	public void dispose() {
		gameSettings.saveToDisk();
		userData.saveToDisk();
//		batch.dispose();
//		shapeRenderer.dispose();
		renderManager.dispose();
		gameAssets.disposeAll();
		assetsLoaded = false;
		super.dispose();
	}

	@Override
	public void resume() {
		if(!assetsLoaded) {
			initializeRendering();
		}
		super.resume();
	}

	private void initializeRendering() {

		renderManager.initializeRendering();

		if(gameAssets == null) {
			gameAssets = new GameAssets();
		}

		gameAssets.initAll();

		renderManager.setGameAssets(gameAssets);

		assetsLoaded = true;
	}

	@Override
	public void resize(int width, int height) {
		LogHelper.log("Main", "Resizing...");

		final float targetHeight = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
		final float targetWidth = Reference.TARGET_RESOLUTION_X;
		final float targetRatio = targetHeight / targetWidth;
		final float sourceRatio = (float) height / (float) width;
		this.scale = sourceRatio > targetRatio ? targetWidth / width : targetHeight / height;
		gameSizeX = (int) Math.ceil(width * scale);
		gameSizeY = (int) Math.ceil(height * scale);

		renderManager.onResize(gameSizeX, gameSizeY);

		LogHelper.log("Main", "new width: " + gameSizeX + ", new height: " + gameSizeY);

		super.resize(width, height);
	}

	public void showMainMenu() {
		setScreen(new MainMenuScreen(this));
	}

	public float getScale() {
		return scale;
	}

	public float getScaledMouseX() {
		return Gdx.input.getX() * scale;
	}

	public float getScaledMouseY() {
		return Gdx.input.getY() * scale;
	}

	public float convertToScaled(float f) {
		return f * getScale();
	}

	public float convertToUnscaled(float f) {
		return f / getScale();
	}

	/**
	 * stops or starts music if settings have changed
	 */
	public void updateMusicPlaying() {
		if(!gameSettings.isMusicEnabled()) {
			gameMusic.fadeOutToStop(0.5F);
		}
		if(screen instanceof PixelScreen) {
			((PixelScreen) screen).updateMusic(gameSettings.isMusicEnabled());
		}
	}

	/**
	 * goes or leaves fullscreen
	 */
	public void updateFullscreen() {
		if(gameConfig.canGoFullScreen()) {
			if(gameSettings.fullscreen) {
				final Graphics.DisplayMode oldMode = Gdx.graphics.getDesktopDisplayMode();
				Gdx.graphics.setDisplayMode(oldMode.width, oldMode.height, true);
			} else {
				Gdx.graphics.setDisplayMode(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, false);
			}
		}
	}

	/**
	 * Main BitMap Font
	 */
	public BitmapFont getFont() {
		return getGameAssets().getFont();
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
	 * Main ShapeRenderer
	 */
	public ShapeRenderer getShapeRenderer() {
		return renderManager.getShapeRenderer();
	}

	/**
	 * Main Sprite Batch
	 */
	public SpriteBatch getBatch() {
		return renderManager.getBatch();
	}

	public GameRenderManager getRenderManager() {
		return renderManager;
	}
}
