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

import net.brenig.pixelescape.game.data.constants.Textures;
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

		item_frame = textureAtlas.findRegion(Textures.ITEM_FRAME);
		itemAnimatedBackground = new SimpleAnimation(3, 2, textureAtlas.findRegion("item_blob_filled"), 0.5F);

		missingTexture = new TextureRegionDrawable(textureAtlas.findRegion("fullscreen_hover"));

	}

	private void initSkin() {
		//Setting up skin
		mainUiSkin = new Skin();

		//Button textures
		mainUiSkin.add(Textures.BUTTON_UP, getButtonNinePatch());
		mainUiSkin.add(Textures.BUTTON_DOWN, textureAtlas.createPatch("button_clicked"));
		mainUiSkin.add(Textures.BUTTON_HOVER, textureAtlas.createPatch("button_hover"));
		mainUiSkin.add(Textures.BUTTON_DISABLED, textureAtlas.createPatch("button_disabled"));

		mainUiSkin.add(Textures.BUTTON_SETTINGS, textureAtlas.createSprite("gear_settings"));
		mainUiSkin.add(Textures.BUTTON_SETTINGS_WHITE, textureAtlas.createSprite("gear_settings_white"));

		mainUiSkin.add(Textures.BUTTON_MUSIC_ENABLED, textureAtlas.createSprite("music_enabled"));
		mainUiSkin.add(Textures.BUTTON_MUSIC_DISABLED, textureAtlas.createSprite("music_disabled"));
		mainUiSkin.add(Textures.BUTTON_MUSIC_ENABLED_HOVER, textureAtlas.createSprite("music_enabled_hover"));
		mainUiSkin.add(Textures.BUTTON_MUSIC_DISABLED_HOVER, textureAtlas.createSprite("music_disabled_hover"));

		mainUiSkin.add(Textures.BUTTON_SOUND_ENABLED, textureAtlas.createSprite("sound_enabled"));
		mainUiSkin.add(Textures.BUTTON_SOUND_DISABLED, textureAtlas.createSprite("sound_disabled"));
		mainUiSkin.add(Textures.BUTTON_SOUND_ENABLED_HOVER, textureAtlas.createSprite("sound_enabled_hover"));
		mainUiSkin.add(Textures.BUTTON_SOUND_DISABLED_HOVER, textureAtlas.createSprite("sound_disabled_hover"));

		mainUiSkin.add(Textures.BUTTON_FULLSCREEN, textureAtlas.createSprite("fullscreen"));
		mainUiSkin.add(Textures.BUTTON_FULLSCREEN_HOVER, textureAtlas.createSprite("fullscreen_hover"));
		mainUiSkin.add(Textures.BUTTON_RESTORE_WINDOW, textureAtlas.createSprite("fullscreen_restore"));
		mainUiSkin.add(Textures.BUTTON_RESTORE_WINDOW_HOVER, textureAtlas.createSprite("fullscreen_restore_hover"));

		mainUiSkin.add(Textures.BUTTON_RIGHT, textureAtlas.createSprite("arrow_right"));
		mainUiSkin.add(Textures.BUTTON_LEFT, textureAtlas.createSprite("arrow_left"));

		mainUiSkin.add(Textures.BUTTON_PAUSE, textureAtlas.createSprite("pause"));
		mainUiSkin.add(Textures.BUTTON_RESUME, textureAtlas.createSprite("resume"));

		mainUiSkin.add(Textures.ITEM_FRAME, textureAtlas.createSprite("item_frame"));

		mainUiSkin.add(Textures.SLIDER_BACKGROUND, textureAtlas.createSprite("slider_background"));
		mainUiSkin.add(Textures.SLIDER_KNOB, textureAtlas.createSprite("slider_knop"));

		mainUiSkin.add(Textures.CHBX_UNCHECKED, textureAtlas.createSprite("checkbox"));
		mainUiSkin.add(Textures.CHBX_CHECKED, textureAtlas.createSprite("checkbox_checked"));
		mainUiSkin.add(Textures.CHBX_HOVER, textureAtlas.createSprite("checkbox_hover"));

		mainUiSkin.add(Textures.SCROLL_BACKGROUND, textureAtlas.createPatch("scroll_background"));
		mainUiSkin.add(Textures.SCROLLBAR, textureAtlas.createSprite("scrollbar"));

		//Button style: Settings
		{
			ImageButton.ImageButtonStyle imageButtonStyle = new ImageButton.ImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable(Textures.BUTTON_SETTINGS);
			imageButtonStyle.imageOver = mainUiSkin.newDrawable(Textures.BUTTON_SETTINGS_WHITE, Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable(Textures.BUTTON_SETTINGS_WHITE, Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable(Textures.BUTTON_SETTINGS_WHITE, Color.DARK_GRAY);

			mainUiSkin.add("settings", imageButtonStyle);
		}

		//Button style: Music
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable(Textures.BUTTON_MUSIC_ENABLED);
			imageButtonStyle.image2Up = mainUiSkin.getDrawable(Textures.BUTTON_MUSIC_DISABLED);
			imageButtonStyle.imageOver = mainUiSkin.newDrawable(Textures.BUTTON_MUSIC_ENABLED_HOVER, Color.LIGHT_GRAY);
			imageButtonStyle.image2Over = mainUiSkin.newDrawable(Textures.BUTTON_MUSIC_DISABLED_HOVER, Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable(Textures.BUTTON_MUSIC_ENABLED_HOVER, Color.GRAY);
			imageButtonStyle.image2Down = mainUiSkin.newDrawable(Textures.BUTTON_MUSIC_DISABLED_HOVER, Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable(Textures.BUTTON_MUSIC_ENABLED_HOVER, Color.DARK_GRAY);
			imageButtonStyle.image2Disabled = mainUiSkin.newDrawable(Textures.BUTTON_MUSIC_DISABLED_HOVER, Color.DARK_GRAY);

			mainUiSkin.add("music", imageButtonStyle);
		}

		//Button style: Sound
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable(Textures.BUTTON_SOUND_ENABLED);
			imageButtonStyle.image2Up = mainUiSkin.getDrawable(Textures.BUTTON_SOUND_DISABLED);
			imageButtonStyle.imageOver = mainUiSkin.newDrawable(Textures.BUTTON_SOUND_ENABLED_HOVER, Color.LIGHT_GRAY);
			imageButtonStyle.image2Over = mainUiSkin.newDrawable(Textures.BUTTON_SOUND_DISABLED_HOVER, Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable(Textures.BUTTON_SOUND_ENABLED_HOVER, Color.GRAY);
			imageButtonStyle.image2Down = mainUiSkin.newDrawable(Textures.BUTTON_SOUND_DISABLED_HOVER, Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable(Textures.BUTTON_SOUND_ENABLED_HOVER, Color.DARK_GRAY);
			imageButtonStyle.image2Disabled = mainUiSkin.newDrawable(Textures.BUTTON_SOUND_DISABLED_HOVER, Color.DARK_GRAY);

			mainUiSkin.add("sound", imageButtonStyle);
		}

		//Button style: Fullscreen
		{
			TwoStateImageButton.TwoStateImageButtonStyle imageButtonStyle = new TwoStateImageButton.TwoStateImageButtonStyle();
			imageButtonStyle.imageUp = mainUiSkin.getDrawable(Textures.BUTTON_FULLSCREEN);
			imageButtonStyle.image2Up = mainUiSkin.getDrawable(Textures.BUTTON_RESTORE_WINDOW);
			imageButtonStyle.imageOver = mainUiSkin.newDrawable(Textures.BUTTON_FULLSCREEN_HOVER, Color.LIGHT_GRAY);
			imageButtonStyle.image2Over = mainUiSkin.newDrawable(Textures.BUTTON_RESTORE_WINDOW_HOVER, Color.LIGHT_GRAY);
			imageButtonStyle.imageDown = mainUiSkin.newDrawable(Textures.BUTTON_FULLSCREEN_HOVER, Color.GRAY);
			imageButtonStyle.image2Down = mainUiSkin.newDrawable(Textures.BUTTON_RESTORE_WINDOW_HOVER, Color.GRAY);
			imageButtonStyle.imageDisabled = mainUiSkin.newDrawable(Textures.BUTTON_FULLSCREEN_HOVER, Color.DARK_GRAY);
			imageButtonStyle.image2Disabled = mainUiSkin.newDrawable(Textures.BUTTON_RESTORE_WINDOW_HOVER, Color.DARK_GRAY);

			mainUiSkin.add("fullscreen", imageButtonStyle);
		}

		//Button style: Arrow Right
		{
			Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
			buttonStyle.up = mainUiSkin.getDrawable(Textures.BUTTON_RIGHT);
			buttonStyle.down = mainUiSkin.newDrawable(Textures.BUTTON_RIGHT, Color.BLACK);
			buttonStyle.over = mainUiSkin.newDrawable(Textures.BUTTON_RIGHT, Color.LIGHT_GRAY);
			buttonStyle.disabled = mainUiSkin.newDrawable(Textures.BUTTON_RIGHT, Color.DARK_GRAY);

			mainUiSkin.add("arrow_right", buttonStyle);
		}

		//Button style: Arrow Left
		{
			Button.ButtonStyle buttonStyle = new Button.ButtonStyle();
			buttonStyle.up = mainUiSkin.getDrawable(Textures.BUTTON_LEFT);
			buttonStyle.down = mainUiSkin.newDrawable(Textures.BUTTON_LEFT, Color.BLACK);
			buttonStyle.over = mainUiSkin.newDrawable(Textures.BUTTON_LEFT, Color.LIGHT_GRAY);
			buttonStyle.disabled = mainUiSkin.newDrawable(Textures.BUTTON_LEFT, Color.DARK_GRAY);

			mainUiSkin.add("arrow_left", buttonStyle);
		}

		//Button style: Text (default)
		{
			TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
			textButtonStyle.up = mainUiSkin.getDrawable(Textures.BUTTON_UP);
			textButtonStyle.down = mainUiSkin.getDrawable(Textures.BUTTON_DOWN);
			textButtonStyle.over = mainUiSkin.getDrawable(Textures.BUTTON_HOVER);
			textButtonStyle.disabled = mainUiSkin.getDrawable(Textures.BUTTON_DISABLED);
			textButtonStyle.font = getFont();
			textButtonStyle.fontColor = Color.BLACK;
			textButtonStyle.downFontColor = Color.WHITE;
			textButtonStyle.disabledFontColor = Color.GRAY;

			mainUiSkin.add("default", textButtonStyle);
		}

		//Button style: Pause
		{
			ImageTextButton.ImageTextButtonStyle imageButtonStyle = new ImageTextButton.ImageTextButtonStyle(mainUiSkin.get(TextButton.TextButtonStyle.class));
			imageButtonStyle.imageUp = mainUiSkin.newDrawable(Textures.BUTTON_PAUSE);

			mainUiSkin.add("pause", imageButtonStyle);
		}

		//Button style: Resume
		{
			ImageTextButton.ImageTextButtonStyle imageButtonStyle = new ImageTextButton.ImageTextButtonStyle(mainUiSkin.get(TextButton.TextButtonStyle.class));
			imageButtonStyle.imageUp = mainUiSkin.newDrawable(Textures.BUTTON_RESUME);

			mainUiSkin.add("resume", imageButtonStyle);
		}

		//Button style: Ability
		{
			AbilityWidget.AbilityButtonStyle abilityButtonStyle = new AbilityWidget.AbilityButtonStyle();
			abilityButtonStyle.up = mainUiSkin.getDrawable(Textures.ITEM_FRAME);
			abilityButtonStyle.down = mainUiSkin.getDrawable(Textures.ITEM_FRAME);
			abilityButtonStyle.over = mainUiSkin.getDrawable(Textures.ITEM_FRAME);
			abilityButtonStyle.disabled = mainUiSkin.getDrawable(Textures.ITEM_FRAME);

			mainUiSkin.add("default", abilityButtonStyle);
		}

		//Label style: default
		{
			Label.LabelStyle labelStyle = new Label.LabelStyle(getFont(), Color.BLACK);
			mainUiSkin.add("default", labelStyle);
		}

		//Label style: white
		{
			Label.LabelStyle labelStyle = new Label.LabelStyle(getFont(), Color.WHITE);
			mainUiSkin.add("white", labelStyle);
		}

		//Slide style: default
		{
			Slider.SliderStyle style = new Slider.SliderStyle(mainUiSkin.getDrawable(Textures.SLIDER_BACKGROUND), mainUiSkin.getDrawable(Textures.SLIDER_KNOB));
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
			style.checkboxOff = mainUiSkin.getDrawable(Textures.CHBX_UNCHECKED);
			style.checkboxOn = mainUiSkin.getDrawable(Textures.CHBX_CHECKED);
			style.checkboxOver = mainUiSkin.getDrawable(Textures.CHBX_HOVER);
			style.font = font;
			style.fontColor = Color.BLACK;
			mainUiSkin.add("default", style);
		}

		//Scroller style: default
		{
			ScrollPane.ScrollPaneStyle style = new ScrollPane.ScrollPaneStyle();
			style.background = new NinePatchDrawable(mainUiSkin.getPatch(Textures.SCROLL_BACKGROUND));
			style.vScrollKnob = mainUiSkin.getDrawable(Textures.SCROLLBAR);
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
