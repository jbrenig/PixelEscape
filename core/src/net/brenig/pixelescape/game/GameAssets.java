package net.brenig.pixelescape.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import net.brenig.pixelescape.screen.ui.TwoStateImageButton;

/**
 * Holds and loads all game assets
 */
public class GameAssets {

//	private AssetManager assetManager;

	private BitmapFont font;
	private Sound gameOverSound;

	private NinePatch buttonNinePatch;
	private TextureAtlas guiAtlas;

	private Skin mainUiSkin;

	public void initAll() {
		initFont();
		initGuiAtlas();
		initTextures();
		initSkin();
		initSounds();
	}

//	public void initAssetManager() {
//		assetManager = new AssetManager();
//	}

	public void initSounds() {
		//load sounds
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/GameOverSlide.ogg"));
	}

	public void initFont() {
		//Use custom font
		font = new BitmapFont(Gdx.files.internal("font/p2p.fnt"), Gdx.files.internal("font/p2p_0.png"), false, true);
		font.setColor(Color.BLACK);
	}

	public void initGuiAtlas() {
		//load ui textures
		guiAtlas = new TextureAtlas(Gdx.files.internal("drawable/gui/gui_textures.pack"));
	}

	public void initTextures() {
		//Cache default button texture for other use cases
		buttonNinePatch = guiAtlas.createPatch("button");
	}

	public void initSkin() {
		//Setting up skin
		mainUiSkin = new Skin();

		//Button textures
		mainUiSkin.add("up", getButtonNinePatch());
		mainUiSkin.add("down", guiAtlas.createPatch("button_clicked"));
		mainUiSkin.add("hover", guiAtlas.createPatch("button_hover"));
		mainUiSkin.add("disabled", guiAtlas.createPatch("button_disabled"));

		mainUiSkin.add("button_settings", guiAtlas.createSprite("gear_settings"));
		mainUiSkin.add("button_settings_white", guiAtlas.createSprite("gear_settings_white"));

		mainUiSkin.add("button_music_enabled", guiAtlas.createSprite("music_enabled"));
		mainUiSkin.add("button_music_disabled", guiAtlas.createSprite("music_disabled"));

		mainUiSkin.add("button_sound_enabled", guiAtlas.createSprite("sound_enabled"));
		mainUiSkin.add("button_sound_disabled", guiAtlas.createSprite("sound_disabled"));

		mainUiSkin.add("button_pause", guiAtlas.createSprite("button_pause"));

		mainUiSkin.add("button_fullscreen", guiAtlas.createSprite("fullscreen"));
		mainUiSkin.add("button_fullscreen_hover", guiAtlas.createSprite("fullscreen_hover"));
		mainUiSkin.add("button_restore_window", guiAtlas.createSprite("fullscreen"));
		mainUiSkin.add("button_restore_window_hover", guiAtlas.createSprite("fullscreen_hover"));

		//Button style: Settings
		{
			ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable("button_settings");
			imageButtonStyle.imageOver = mainUiSkin.newDrawable("button_settings_white", Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable("button_settings_white", Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable("button_settings_white", Color.DARK_GRAY);

			mainUiSkin.add("settings", imageButtonStyle);
		}

		//Button style: Music
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable("button_music_enabled");
			imageButtonStyle.image2Up = mainUiSkin.getDrawable("button_music_disabled");

			mainUiSkin.add("music", imageButtonStyle);
		}

		//Button style: Sound
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable("button_sound_enabled");
			imageButtonStyle.image2Up = mainUiSkin.getDrawable("button_sound_disabled");

			mainUiSkin.add("sound", imageButtonStyle);
		}

		//Button style: Fullscreen
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable("button_fullscreen");
			imageButtonStyle.image2Up = mainUiSkin.getDrawable("button_restore_window"); //TODO: image that represents inversion to fullscreen (go to windowed)
			imageButtonStyle.imageOver = mainUiSkin.newDrawable("button_fullscreen_hover", Color.LIGHT_GRAY);
			imageButtonStyle.image2Over = mainUiSkin.newDrawable("button_restore_window_hover", Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable("button_fullscreen_hover", Color.GRAY);
			imageButtonStyle.image2Down = mainUiSkin.newDrawable("button_restore_window_hover", Color.GRAY);

			mainUiSkin.add("fullscreen", imageButtonStyle);
		}

		//Button style: Pause
		{
			ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.newDrawable("button_pause", Color.BLACK);

			mainUiSkin.add("pause", imageButtonStyle);
		}

		//Button style: Text (default)
		{
			TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
			textButtonStyle.up = mainUiSkin.getDrawable("up");
			textButtonStyle.down = mainUiSkin.getDrawable("down");
			textButtonStyle.over = mainUiSkin.getDrawable("hover");
			textButtonStyle.disabled = mainUiSkin.getDrawable("disabled");
			textButtonStyle.font = getFont();
			textButtonStyle.fontColor = Color.BLACK;
			textButtonStyle.downFontColor = Color.WHITE;
			textButtonStyle.disabledFontColor = Color.GRAY;

			mainUiSkin.add("default", textButtonStyle);
		}

		//Label style
		Label.LabelStyle labelStyle = new Label.LabelStyle(getFont(), Color.BLACK);

		mainUiSkin.add("default", labelStyle);
	}

	public Skin getMainUiSkin() {
		return mainUiSkin;
	}

	public BitmapFont getFont() {
		return font;
	}

	public NinePatch getButtonNinePatch() {
		return buttonNinePatch;
	}

	public Sound getGameOverSound() {
		return gameOverSound;
	}

	public TextureAtlas getGuiAtlas() {
		return guiAtlas;
	}
}
