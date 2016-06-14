package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import net.brenig.pixelescape.render.SimpleAnimation;
import net.brenig.pixelescape.render.ui.general.TwoStateImageButton;
import net.brenig.pixelescape.render.ui.ingame.AbilityWidget;

import java.util.Random;

/**
 * Holds and loads all game assets
 */
public class GameAssets {

	private BitmapFont font;
	private Sound gameOverSound;

	private Music mainMenuMusic;
	private Music snpMusic;
	private Music sslMusic;


	private NinePatch buttonNinePatch;
	private TextureAtlas textureAtlas;

	private TextureRegion heart;

	private TextureRegion item_frame;
	private Drawable item_blink;
	private Drawable item_slow;
	private Drawable item_shield;
	private Drawable item_move;
	private Drawable item_life;
	private Drawable item_small_barricades;
	private Drawable item_score;

	private TextureRegion effect_item_shield;

	private Drawable missingTexture;

	private TextureRegion square;

	private Skin mainUiSkin;

	private SimpleAnimation itemAnimatedBackground;

	public void disposeAll() {
		font.dispose();
		gameOverSound.dispose();
		mainMenuMusic.dispose();
		snpMusic.dispose();
		sslMusic.dispose();

		textureAtlas.dispose();

		mainUiSkin.dispose();
	}

	public void initAll() {
		initFont();
		initTextureAtlas();
		initTextures();
		initSkin();
		initSounds();
		initMusic();
	}

	private void initSounds() {
		//load sounds
		gameOverSound = Gdx.audio.newSound(Gdx.files.internal("sound/explode.ogg"));
	}

	private void initMusic() {
		mainMenuMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SynthPower.ogg"));
		mainMenuMusic.setLooping(true);

		snpMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SynthNPiano.ogg"));
		snpMusic.setLooping(true);

		sslMusic = Gdx.audio.newMusic(Gdx.files.internal("music/SawSquareLoop.ogg"));
		sslMusic.setLooping(true);
	}

	private void initFont() {
		//Use custom font
		Texture texture = new Texture(Gdx.files.internal("font/p2p_0.png"), false);
		texture.setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		font = new BitmapFont(Gdx.files.internal("font/p2p.fnt"), new TextureRegion(texture));
		font.setColor(Color.BLACK);
	}

	private void initTextureAtlas() {
		//load ui textures
		textureAtlas = new TextureAtlas(Gdx.files.internal("drawable/main_textures.atlas"));
	}

	private void initTextures() {
		//Cache default button texture for other use cases
		buttonNinePatch = textureAtlas.createPatch("button");
		square = textureAtlas.findRegion("square");
		heart = textureAtlas.findRegion("heart");

		//ITEMS
		item_blink = new TextureRegionDrawable(textureAtlas.findRegion("item_blink"));
		item_slow = new TextureRegionDrawable(textureAtlas.findRegion("item_slow"));
		item_shield = new TextureRegionDrawable(textureAtlas.findRegion("item_shield"));
		item_move = new TextureRegionDrawable(textureAtlas.findRegion("item_move"));
		item_score = new TextureRegionDrawable(textureAtlas.findRegion("item_score"));
		item_small_barricades = new TextureRegionDrawable(textureAtlas.findRegion("item_small_obstacles"));
		item_life = new TextureRegionDrawable(heart);

		effect_item_shield = textureAtlas.findRegion("effect_item_shield");

		item_frame = textureAtlas.findRegion("item_frame");
		itemAnimatedBackground = new SimpleAnimation(3, 2, textureAtlas.findRegion("item_blob_filled"), 0.5F);

		missingTexture = new TextureRegionDrawable(textureAtlas.findRegion("fullscreen_hover"));

	}

	private void initSkin() {
		//Setting up skin
		mainUiSkin = new Skin();

		//Button textures
		mainUiSkin.add("up", getButtonNinePatch());
		mainUiSkin.add("down", textureAtlas.createPatch("button_clicked"));
		mainUiSkin.add("hover", textureAtlas.createPatch("button_hover"));
		mainUiSkin.add("disabled", textureAtlas.createPatch("button_disabled"));

		mainUiSkin.add("button_settings", textureAtlas.createSprite("gear_settings"));
		mainUiSkin.add("button_settings_white", textureAtlas.createSprite("gear_settings_white"));

		mainUiSkin.add("button_music_enabled", textureAtlas.createSprite("music_enabled"));
		mainUiSkin.add("button_music_disabled", textureAtlas.createSprite("music_disabled"));
		mainUiSkin.add("button_music_enabled_hover", textureAtlas.createSprite("music_enabled_hover"));
		mainUiSkin.add("button_music_disabled_hover", textureAtlas.createSprite("music_disabled_hover"));

		mainUiSkin.add("button_sound_enabled", textureAtlas.createSprite("sound_enabled"));
		mainUiSkin.add("button_sound_disabled", textureAtlas.createSprite("sound_disabled"));
		mainUiSkin.add("button_sound_enabled_hover", textureAtlas.createSprite("sound_enabled_hover"));
		mainUiSkin.add("button_sound_disabled_hover", textureAtlas.createSprite("sound_disabled_hover"));

		mainUiSkin.add("button_pause", textureAtlas.createSprite("button_pause"));

		mainUiSkin.add("button_fullscreen", textureAtlas.createSprite("fullscreen"));
		mainUiSkin.add("button_fullscreen_hover", textureAtlas.createSprite("fullscreen_hover"));
		mainUiSkin.add("button_restore_window", textureAtlas.createSprite("fullscreen_restore"));
		mainUiSkin.add("button_restore_window_hover", textureAtlas.createSprite("fullscreen_restore_hover"));

		mainUiSkin.add("button_right", textureAtlas.createSprite("arrow_right"));
		mainUiSkin.add("button_left", textureAtlas.createSprite("arrow_left"));

		mainUiSkin.add("button_pause", textureAtlas.createSprite("pause"));
		mainUiSkin.add("button_resume", textureAtlas.createSprite("resume"));

		mainUiSkin.add("item_frame", textureAtlas.createSprite("item_frame"));

		mainUiSkin.add("slider_background", textureAtlas.createSprite("slider_background"));
		mainUiSkin.add("slider_knob", textureAtlas.createSprite("slider_knop"));

		mainUiSkin.add("chbx_unchecked", textureAtlas.createSprite("checkbox"));
		mainUiSkin.add("chbx_checked", textureAtlas.createSprite("checkbox_checked"));
		mainUiSkin.add("chbx_hover", textureAtlas.createSprite("checkbox_hover"));

		mainUiSkin.add("scroll_background", textureAtlas.createPatch("scroll_background"));
		mainUiSkin.add("scrollbar", textureAtlas.createSprite("scrollbar"));

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

		//Button style: Arrow Right
		{
			Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
			buttonStyle.up = mainUiSkin.getDrawable("button_right");
			buttonStyle.down = mainUiSkin.newDrawable("button_right", Color.BLACK);
			buttonStyle.over = mainUiSkin.newDrawable("button_right", Color.LIGHT_GRAY);
			buttonStyle.disabled = mainUiSkin.newDrawable("button_right", Color.DARK_GRAY);

			mainUiSkin.add("arrow_right", buttonStyle);
		}

		//Button style: Arrow Left
		{
			Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
			buttonStyle.up = mainUiSkin.getDrawable("button_left");
			buttonStyle.down = mainUiSkin.newDrawable("button_left", Color.BLACK);
			buttonStyle.over = mainUiSkin.newDrawable("button_left", Color.LIGHT_GRAY);
			buttonStyle.disabled = mainUiSkin.newDrawable("button_left", Color.DARK_GRAY);

			mainUiSkin.add("arrow_left", buttonStyle);
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

		//Button style: Pause
		{
			ImageTextButton.ImageTextButtonStyle imageButtonStyle = new ImageTextButton.ImageTextButtonStyle(mainUiSkin.get(TextButton.TextButtonStyle.class));
			imageButtonStyle.imageUp = mainUiSkin.newDrawable("button_pause");

			mainUiSkin.add("pause", imageButtonStyle);
		}

		//Button style: Resume
		{
			ImageTextButton.ImageTextButtonStyle imageButtonStyle = new ImageTextButton.ImageTextButtonStyle(mainUiSkin.get(TextButton.TextButtonStyle.class));
			imageButtonStyle.imageUp = mainUiSkin.newDrawable("button_resume");

			mainUiSkin.add("resume", imageButtonStyle);
		}

		//Button style: Ability
		{
			AbilityWidget.AbilityButtonStyle abilityButtonStyle = new AbilityWidget.AbilityButtonStyle();
			abilityButtonStyle.up = mainUiSkin.getDrawable("item_frame");
			abilityButtonStyle.down = mainUiSkin.getDrawable("item_frame");
			abilityButtonStyle.over = mainUiSkin.getDrawable("item_frame");
			abilityButtonStyle.disabled = mainUiSkin.getDrawable("item_frame");

			mainUiSkin.add("default", abilityButtonStyle);
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

	public Sound getPlayerChrashedSound() {
		return gameOverSound;
	}

	public Music getMainMenuMusic() {
		return mainMenuMusic;
	}

	public Music getSnpMusic() {
		return snpMusic;
	}

	public Music getSslMusic() {
		return sslMusic;
	}

	public Music getRandomGameMusic(Random random) {
		switch (random.nextInt(2)) {
			case 0:
				return getSnpMusic();
			case 1:
				return getSslMusic();
		}
		return null;
	}

	public TextureAtlas getTextureAtlas() {
		return textureAtlas;
	}

	public TextureRegion getHeart() {
		return heart;
	}

	public TextureRegion getItemFrame() {
		return item_frame;
	}

	public Drawable getItemBlink() {
		return item_blink;
	}

	public Drawable getMissingTexture() {
		return missingTexture;
	}

	public SimpleAnimation getItemAnimatedBackground() {
		return itemAnimatedBackground;
	}

	public Drawable getItemShield() {
		return item_shield;
	}

	public Drawable getItemSlow() {
		return item_slow;
	}

	public TextureRegion getEffectItemShield() {
		return effect_item_shield;
	}

	public Drawable getItemSmallBarricades() {
		return item_small_barricades;
	}

	public Drawable getItemScore() {
		return item_score;
	}

	public Drawable getItemMove() {
		return item_move;
	}

	public Drawable getHeartDrawable() {
		return item_life;
	}

	public TextureRegion getSquare() {
		return square;
	}
}
