package net.brenig.pixelescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import net.brenig.pixelescape.game.GameSettings;
import net.brenig.pixelescape.game.UserData;
import net.brenig.pixelescape.game.entity.EntityPlayer;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.MainMenuScreen;
import net.brenig.pixelescape.screen.ui.TwoStateImageButton;

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
	/**
	 * Main BitMap Font
	 */
	public BitmapFont font;


	/**
	 * Main Camera
	 * y-Up
	 */
	public OrthographicCamera cam;

	/**
	 * GameOverSound
	 */
	public Sound gameOverSound;

	/**
	 * Default button ninepatch
	 */
	public NinePatch buttonNinePatch;

	/**
	 * Gui Texture atlas
	 */
	public TextureAtlas guiAtlas;

	/**
	 * Main Gui Skin
	 */
	public Skin skin;

	public GameSettings gameSettings;
	public UserData userData;

	public int gameSizeX = Reference.TARGET_RESOLUTION_X;
	public int gameSizeY = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;
	private float scale = 1.0F;

	public EntityPlayer thePlayer;

	@Override
	public void create() {
		LogHelper.log("Main", "Starting up...");
		if(instance != null) {
			throw new IllegalStateException("Critical Error! Game already initialized!");
		}
		instance = this;

		//initialize drawing area
		batch = new SpriteBatch();

		//Use custom font
		font = new BitmapFont(Gdx.files.internal("font/p2p.fnt"), Gdx.files.internal("font/p2p_0.png"), false, true);
		font.setColor(Color.BLACK);

		//load ui textures
		guiAtlas = new TextureAtlas(Gdx.files.internal("drawable/gui/gui_textures.pack"));

		//initialize viewport
		cam = new OrthographicCamera();
		cam.setToOrtho(false);
		batch.setProjectionMatrix(cam.combined);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(cam.combined);

		//load sounds
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/GameOverSlide.ogg"));

		//Setting up skin
		skin = new Skin();

		//Cache default button texture for other use cases
		buttonNinePatch = guiAtlas.createPatch("button");

		//Button textures
		skin.add("up", buttonNinePatch);
		skin.add("down", guiAtlas.createPatch("button_clicked"));
		skin.add("hover", guiAtlas.createPatch("button_hover"));
		skin.add("disabled", guiAtlas.createPatch("button_disabled"));

		skin.add("button_settings", guiAtlas.createSprite("gear_settings"));
		skin.add("button_settings_white", guiAtlas.createSprite("gear_settings_white"));

		skin.add("button_music_enabled", guiAtlas.createSprite("music_enabled"));
		skin.add("button_music_disabled", guiAtlas.createSprite("music_disabled"));

		skin.add("button_sound_enabled", guiAtlas.createSprite("sound_enabled"));
		skin.add("button_sound_disabled", guiAtlas.createSprite("sound_disabled"));

		skin.add("button_pause", guiAtlas.createSprite("button_pause"));

		//Button style: Settings
		{
			ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
			imageButtonStyle.imageUp = skin.getDrawable("button_settings");
			imageButtonStyle.imageOver = skin.newDrawable("button_settings_white", Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = skin.newDrawable("button_settings_white", Color.GRAY);
			imageButtonStyle.imageDisabled = skin.newDrawable("button_settings_white", Color.DARK_GRAY);

			skin.add("settings", imageButtonStyle);
		}

		//Button style: Music
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = skin.getDrawable("button_music_enabled");
			imageButtonStyle.image2Up = skin.getDrawable("button_music_disabled");

			skin.add("music", imageButtonStyle);
		}

		//Button style: Sound
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = skin.getDrawable("button_sound_enabled");
			imageButtonStyle.image2Up = skin.getDrawable("button_sound_disabled");

			skin.add("sound", imageButtonStyle);
		}

		//Button style: Pause
		{
			ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
			imageButtonStyle.imageUp = skin.newDrawable("button_pause", Color.BLACK);

			skin.add("pause", imageButtonStyle);
		}

		//Button style: Text (default)
		{
			TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
			textButtonStyle.up = skin.getDrawable("up");
			textButtonStyle.down = skin.getDrawable("down");
			textButtonStyle.over = skin.getDrawable("hover");
			textButtonStyle.disabled = skin.getDrawable("disabled");
			textButtonStyle.font = font;
			textButtonStyle.fontColor = Color.BLACK;
			textButtonStyle.downFontColor = Color.WHITE;
			textButtonStyle.disabledFontColor = Color.GRAY;

			skin.add("default", textButtonStyle);
		}

		//Label style
		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);

		skin.add("default", labelStyle);

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
			font.setColor(Color.RED);
			resetFontSize();
			font.draw(batch, "FPS " + Gdx.graphics.getFramesPerSecond(), 10, gameSizeY - 10);
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
		font.dispose();
		shapeRenderer.dispose();
		gameOverSound.dispose();
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
		font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
	}

	public void resetFontSize() {
		font.getData().setScale(1.0F);
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
}
