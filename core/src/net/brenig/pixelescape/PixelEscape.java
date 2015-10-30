package net.brenig.pixelescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import net.brenig.pixelescape.game.GameAssets;
import net.brenig.pixelescape.game.GameConfiguration;
import net.brenig.pixelescape.game.GameSettings;
import net.brenig.pixelescape.game.UserData;
import net.brenig.pixelescape.game.entity.EntityPlayer;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.MainMenuScreen;

public class PixelEscape extends Game {

	private static PixelEscape instance;

	/**
	 * Main ShapeRenderer
	 */
	public ShapeRenderer shapeRenderer;
	/**
	 * Main Sprite Batch
	 */
	public SpriteBatch batch;
//	private BitmapFont font;


	/**
	 * Main Camera
	 * y-Up
	 */
	public OrthographicCamera cam;

//	private Sound gameOverSound;

//	private NinePatch buttonNinePatch;
//
//	private TextureAtlas guiAtlas;
//
//	private Skin skin;

	private GameAssets gameAssets;
	public GameSettings gameSettings;
	public UserData userData;
	public GameConfiguration gameConfig;

	public int gameSizeX = Reference.TARGET_RESOLUTION_X;
	public int gameSizeY = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
	private float scale = 1.0F;

	public EntityPlayer thePlayer;

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
			throw new IllegalStateException("Critical Error! Game already initialized!");
		}
		instance = this;

		//initialize drawing area
		batch = new SpriteBatch();


		//initialize viewport
		cam = new OrthographicCamera();
		cam.setToOrtho(false);
		batch.setProjectionMatrix(cam.combined);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);


		updateAssetManager();
		gameAssets.initAll();

		//load settings
		gameSettings = new GameSettings();
		gameSettings.loadFromDisk();

		//load userdata
		//currently only highscore
		userData = new UserData();
		userData.loadFromDisk();

		//open main menu
		showMainMenu();

		//Test
//		CycleArrayTest.runTest();

		LogHelper.log("Main", "Finished loading!");
	}

	public static PixelEscape getPixelEscape() {
		return instance;
	}

	public void updateAssetManager() {
		if(gameAssets == null) {
			gameAssets = new GameAssets();
		}
	}

	public GameAssets getGameAssets() {
		return gameAssets;
	}

	@Override
	public void setScreen(Screen screen) {
		//reset font
		resetFontSize();
		super.setScreen(screen);
	}

	@Override
	public void render() {
		prepareRender();
		super.render();
		if(Reference.SHOW_FPS) {
			batch.begin();
			getFont().setColor(Color.RED);
			resetFontSize();
			getFont().draw(batch, "FPS " + Gdx.graphics.getFramesPerSecond(), 10, gameSizeY - 10);
			batch.end();
		}
	}

	public void prepareRender() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		cam.update();
	}

	@Override
	public void dispose() {
		gameSettings.saveToDisk();
		userData.saveToDisk();
		batch.dispose();
		getFont().dispose();
		shapeRenderer.dispose();
		getGameOverSound().dispose();
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log("PixelEscape | Main", "resizing...");

		final float targetHeight = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
		final float targetWidth = Reference.TARGET_RESOLUTION_X;
		final float targetRatio = targetHeight / targetWidth;
		float sourceRatio = (float) height / (float) width;
		this.scale = sourceRatio > targetRatio ? targetWidth / width : targetHeight / height;
		gameSizeX = (int) Math.ceil(width * scale);
		gameSizeY = (int) Math.ceil(height * scale);
		cam.setToOrtho(false, gameSizeX, gameSizeY);
		batch.setProjectionMatrix(cam.combined);
		shapeRenderer.setProjectionMatrix(cam.combined);
		cam.update();

		super.resize(width, height);
	}

	public void setFontSizeToDefaultGuiSize() {
		getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
	}

	public void resetFontSize() {
		getFont().getData().setScale(1.0F);
//		font.getData().setScale(scale);
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
	 * GameOverSound
	 */
	public Sound getGameOverSound() {
		return getGameAssets().getGameOverSound();
	}

	/**
	 * Default button ninepatch
	 */
	public NinePatch getButtonNinePatch() {
		return getGameAssets().getButtonNinePatch();
	}

	/**
	 * Gui Texture atlas
	 */
	public TextureAtlas getGuiAtlas() {
		return getGameAssets().getGuiAtlas();
	}

	/**
	 * Main Gui Skin
	 */
	public Skin getSkin() {
		return getGameAssets().getMainUiSkin();
	}
}
