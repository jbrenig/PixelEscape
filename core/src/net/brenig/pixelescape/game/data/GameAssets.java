package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.screen.ui.TwoStateImageButton;

/**
 * Holds and loads all game assets
 */
public class GameAssets {

//	private AssetManager assetManager;

	private BitmapFont font;
	private Sound gameOverSound;

	private Music mainMenuMusic;
	private Music mipIntro;
	private Music mipMain;
	private Music snpMusic;


	private NinePatch buttonNinePatch;
	private TextureAtlas guiAtlas;

	private Skin mainUiSkin;

	public void disposeAll() {
		font.dispose();
		gameOverSound.dispose();
		mainMenuMusic.dispose();
		mipIntro.dispose();
		mipMain.dispose();
		snpMusic.dispose();
		guiAtlas.dispose();
		mainUiSkin.dispose();
	}

	public void initAll() {
		initFont();
		initGuiAtlas();
		initTextures();
		initSkin();
		initSounds();
		initMusic();
	}

//	public void initAssetManager() {
//		assetManager = new AssetManager();
//	}

	public void initSounds() {
		//load sounds
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/explode.ogg"));
	}

	public void initMusic() {
		mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SynthPower.ogg"));
		mainMenuMusic.setLooping(true);
		mipIntro = Gdx.audio.newMusic(Gdx.files.internal("music/MIPSynthIntro.ogg"));
		mipIntro.setLooping(false);
		mipMain = Gdx.audio.newMusic(Gdx.files.internal("music/MIPSynth.ogg"));
		mipMain.setLooping(true);
		mipIntro.setOnCompletionListener(new Music.OnCompletionListener() {
			@Override
			public void onCompletion(Music music) {
				PixelEscape.getPixelEscape().gameMusic.setCurrentMusic(mipMain);
				PixelEscape.getPixelEscape().gameMusic.play();
			}
		});
		snpMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SynthNPiano.ogg"));
		snpMusic.setLooping(true);
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
		mainUiSkin.add("button_music_enabled_hover", guiAtlas.createSprite("music_enabled_hover"));
		mainUiSkin.add("button_music_disabled_hover", guiAtlas.createSprite("music_disabled_hover"));

		mainUiSkin.add("button_sound_enabled", guiAtlas.createSprite("sound_enabled"));
		mainUiSkin.add("button_sound_disabled", guiAtlas.createSprite("sound_disabled"));
		mainUiSkin.add("button_sound_enabled_hover", guiAtlas.createSprite("sound_enabled_hover"));
		mainUiSkin.add("button_sound_disabled_hover", guiAtlas.createSprite("sound_disabled_hover"));

		mainUiSkin.add("button_pause", guiAtlas.createSprite("button_pause"));

		mainUiSkin.add("button_fullscreen", guiAtlas.createSprite("fullscreen"));
		mainUiSkin.add("button_fullscreen_hover", guiAtlas.createSprite("fullscreen_hover"));
		mainUiSkin.add("button_restore_window", guiAtlas.createSprite("fullscreen_restore"));
		mainUiSkin.add("button_restore_window_hover", guiAtlas.createSprite("fullscreen_restore_hover"));

		mainUiSkin.add("slider_background", guiAtlas.createSprite("slider_background"));
		mainUiSkin.add("slider_knob", guiAtlas.createSprite("slider_knop"));

		mainUiSkin.add("chbx_unchecked", guiAtlas.createSprite("checkbox"));
		mainUiSkin.add("chbx_checked", guiAtlas.createSprite("checkbox_checked"));
		mainUiSkin.add("chbx_hover", guiAtlas.createSprite("checkbox_hover"));

		mainUiSkin.add("scroll_background", guiAtlas.createPatch("scroll_background"));
		mainUiSkin.add("scrollbar", guiAtlas.createSprite("scrollbar"));

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
			imageButtonStyle.imageOver = mainUiSkin.newDrawable("button_music_enabled_hover", Color.LIGHT_GRAY);
			imageButtonStyle.image2Over = mainUiSkin.newDrawable("button_music_disabled_hover", Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable("button_music_enabled_hover", Color.GRAY);
			imageButtonStyle.image2Down = mainUiSkin.newDrawable("button_music_disabled_hover", Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable("button_music_enabled_hover", Color.DARK_GRAY);
			imageButtonStyle.image2Disabled = mainUiSkin.newDrawable("button_music_disabled_hover", Color.DARK_GRAY);

			mainUiSkin.add("music", imageButtonStyle);
		}

		//Button style: Sound
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable("button_sound_enabled");
			imageButtonStyle.image2Up = mainUiSkin.getDrawable("button_sound_disabled");
			imageButtonStyle.imageOver = mainUiSkin.newDrawable("button_sound_enabled_hover", Color.LIGHT_GRAY);
			imageButtonStyle.image2Over = mainUiSkin.newDrawable("button_sound_disabled_hover", Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable("button_sound_enabled_hover", Color.GRAY);
			imageButtonStyle.image2Down = mainUiSkin.newDrawable("button_sound_disabled_hover", Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable("button_sound_enabled_hover", Color.DARK_GRAY);
			imageButtonStyle.image2Disabled = mainUiSkin.newDrawable("button_sound_disabled_hover", Color.DARK_GRAY);

			mainUiSkin.add("sound", imageButtonStyle);
		}

		//Button style: Fullscreen
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable("button_fullscreen");
			imageButtonStyle.image2Up = mainUiSkin.getDrawable("button_restore_window");
			imageButtonStyle.imageOver = mainUiSkin.newDrawable("button_fullscreen_hover", Color.LIGHT_GRAY);
			imageButtonStyle.image2Over = mainUiSkin.newDrawable("button_restore_window_hover", Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable("button_fullscreen_hover", Color.GRAY);
			imageButtonStyle.image2Down = mainUiSkin.newDrawable("button_restore_window_hover", Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable("button_fullscreen_hover", Color.DARK_GRAY);
			imageButtonStyle.image2Disabled = mainUiSkin.newDrawable("button_restore_window_hover", Color.DARK_GRAY);

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

		//Label style: default
		{
			Label.LabelStyle labelStyle = new Label.LabelStyle(getFont(), Color.BLACK);
			labelStyle.font = font;
			labelStyle.fontColor = Color.BLACK;
			mainUiSkin.add("default", labelStyle);
		}

		//Slide style: default
		{
			Slider.SliderStyle style = new Slider.SliderStyle(mainUiSkin.getDrawable("slider_background"), mainUiSkin.getDrawable("slider_knob"));
			mainUiSkin.add("default", style);
		}

		//Dialog style: default
		{
			Window.WindowStyle style = new Window.WindowStyle();
			style.titleFont = font;
			style.titleFontColor = Color.BLACK;
			style.background = new NinePatchDrawable(buttonNinePatch);
			mainUiSkin.add("default", style);
		}

		//CheckBox style: default
		{
			CheckBox.CheckBoxStyle style = new CheckBox.CheckBoxStyle();
			style.checkboxOff = mainUiSkin.getDrawable("chbx_unchecked");
			style.checkboxOn = mainUiSkin.getDrawable("chbx_checked");
			style.checkboxOver = mainUiSkin.getDrawable("chbx_hover");
			style.font = font;
			style.fontColor = Color.BLACK;
			mainUiSkin.add("default", style);
		}

		//Scroller style: default
		{
			ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
			style.background = new NinePatchDrawable(mainUiSkin.getPatch("scroll_background"));
			style.vScrollKnob = mainUiSkin.getDrawable("scrollbar");
			mainUiSkin.add("default", style);
		}

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

	public Music getMainMenuMusic() {
		return mainMenuMusic;
	}

	public Music getSnpMusic() {
		return snpMusic;
	}

	public TextureAtlas getGuiAtlas() {
		return guiAtlas;
	}

	public Music getMipComplete() {
		return mipIntro;
	}
}
