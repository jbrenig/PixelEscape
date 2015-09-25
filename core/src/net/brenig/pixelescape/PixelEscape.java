package net.brenig.pixelescape;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.MainMenuScreen;

public class PixelEscape extends Game {

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
	 */
	public OrthographicCamera cam;

	/**
	 * GameOverSOund
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

	public int gameSizeX = Reference.TARGET_RESOLUTION_X;
	public int gameSizeY = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE;

	@Override
	public void create() {
		Gdx.app.log("PixelEscape | Main", "Starting up...");
		//initialize drawing area
		batch = new SpriteBatch();
		//Use LibGDX's default Arial font.
		font = new BitmapFont(Gdx.files.internal("font/p2p.fnt"), Gdx.files.internal("font/p2p_0.png"), false, true);
		font.setColor(Color.BLACK);

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
		//skin data
		Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();
		//Cache default button texture for other use cases
		buttonNinePatch = guiAtlas.createPatch("button");

		skin.add("up", buttonNinePatch);
		skin.add("down", guiAtlas.createPatch("button_clicked"));
		skin.add("hover", guiAtlas.createPatch("button_hover"));
		skin.add("disabled", guiAtlas.createPatch("button_disabled"));

		skin.add("button_settings", guiAtlas.createSprite("gear_settings"));
		skin.add("button_settings_white", guiAtlas.createSprite("gear_settings_white"));

		ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
		imageButtonStyle.imageUp = skin.getDrawable("button_settings");
		imageButtonStyle.imageOver = skin.newDrawable("button_settings_white", Color.LIGHT_GRAY);
		imageButtonStyle.imageDown = skin.newDrawable("button_settings_white", Color.GRAY);
		imageButtonStyle.imageDisabled = skin.newDrawable("button_settings_white", Color.DARK_GRAY);

		skin.add("default", imageButtonStyle);

		TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
		textButtonStyle.up = skin.getDrawable("up");
		textButtonStyle.down = skin.getDrawable("down");
//		textButtonStyle.checked = skin.newDrawable("up", Color.BLUE);
		textButtonStyle.over = skin.getDrawable("hover");
		textButtonStyle.disabled = skin.getDrawable("disabled");
		textButtonStyle.font = font;
		textButtonStyle.fontColor = Color.BLACK;
		textButtonStyle.downFontColor = Color.WHITE;
		textButtonStyle.disabledFontColor = Color.GRAY;

		skin.add("default", textButtonStyle);

		Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);

		skin.add("default", labelStyle);

		//open main menu
		this.setScreen(new MainMenuScreen(this));
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
		float scale = sourceRatio > targetRatio ? targetWidth / width : targetHeight / height;
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
	}
}
