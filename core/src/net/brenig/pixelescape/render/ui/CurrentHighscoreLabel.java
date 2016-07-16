package net.brenig.pixelescape.render.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Align;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Utils;

import java.util.Random;

/**
 * Label that displays current highscore (animated, no frame)
 */
public class CurrentHighscoreLabel extends Widget {

	private static final String SCORE_TEXT = "Highscore: ";
	private static final float padding_side = 4;
	private static final float padding_height = 4;

	private static final float font_size_x = 0.6F;
	private static final float font_size_y = 0.8F;

	private static final float font_scaling_strength = 0.5F;

	private final GlyphLayout fontLayout;
	private final PixelEscape game;

	private enum Animations {
		WAIT(2, 8), BLEND(0.4F, 2F), MOVE_X(0.4F, 4F), MOVE_Y(0.4F, 8), SIZE(0.5F, 0.5F), GM_BLEND_OUT(0.5F, 0.5F), GM_BLEND_IN(0.5F, 0.5F);

		private final float minDuration;
		private final float maxDuration;

		Animations(float minDuration, float maxDuration) {
			this.minDuration = minDuration;
			this.maxDuration = maxDuration;
		}

		private float getDuration(Random random) {
			return minDuration + random.nextFloat() * (maxDuration - minDuration);
		}

		public float getMinDuration() {
			return minDuration;
		}
	}

	private Animations state;

	private float animationTimer = 0;
	private float animationDuration = 0;
	private int animationData = 0;

	private String text;

	private GameMode gameMode;

	public CurrentHighscoreLabel(GameMode gameMode) {
		super();
		game = PixelEscape.getPixelEscape();
		state = Animations.WAIT;
		this.gameMode = gameMode;
		updateText();
		fontLayout = new GlyphLayout(game.getFont(), text);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//validate layout
		super.draw(batch, parentAlpha);

		if (animationTimer >= animationDuration) {
			updateAnimation();
		}

		final float oldFontSizeX = game.getFont().getScaleX();
		final float oldFontSizeY = game.getFont().getScaleY();
		float fontSizeX = font_size_x;
		float fontSizeY = font_size_y;
		float offsetX = 0;
		float offsetY = 0;
		float alpha = 1;
		switch (state) {
			case BLEND: {
				float part = animationDuration / 3;
				if (animationTimer < part) {
					alpha = 1 - Utils.easeOut(animationTimer, part, 2);
				} else if (animationTimer < part * 2) {
					alpha = 0;
				} else {
					alpha = Utils.easeInAndOut(animationTimer - part * 2, part);
				}
				Gdx.gl.glEnable(GL20.GL_BLEND);
			}
			break;
			case MOVE_X:
				offsetX = (float) (Math.sin((animationTimer / animationDuration) * animationData * Math.PI * 2) * 10F);
				break;
			case MOVE_Y:
				offsetY = (float) (Math.sin((animationTimer / animationDuration) * animationData * Math.PI * 2) * 10F);
				break;
			case WAIT:
				break;
			case SIZE: {
				float part = animationTimer / animationDuration;
				if (part < 0.5) {
					float ease = Utils.easeInAndOut(part, 0.5F) * font_scaling_strength;
					fontSizeX = font_size_x - ease * font_size_x;
					fontSizeY = font_size_y - ease * font_size_y;
				} else {
					float ease = Utils.easeInAndOut(part - 0.5F, 0.5F) * font_scaling_strength;
					fontSizeX = font_size_x - font_scaling_strength * font_size_x + ease * font_size_x;
					fontSizeY = font_size_y - font_scaling_strength * font_size_y + ease * font_size_y;
				}
			}
			break;
			case GM_BLEND_IN:
				if(animationTimer == 0) {
					updateText();
				}
				alpha = animationTimer / animationDuration;
				Gdx.gl.glEnable(GL20.GL_BLEND);
				break;
			case GM_BLEND_OUT:
				alpha = 1 - animationTimer / animationDuration;
				Gdx.gl.glEnable(GL20.GL_BLEND);
				break;

		}
		//Score text
		setColor(0, 0, 0, alpha);
		if (fontSizeX <= 0) {
			LogHelper.error("Invalid text scale in score widget animation");
			fontSizeX = font_size_x;
		}
		if (fontSizeY <= 0) {
			LogHelper.error("Invalid text scale in score widget animation");
			fontSizeY = font_size_y;
		}
		game.getFont().getData().setScale(fontSizeX, fontSizeY);
		fontLayout.setText(game.getFont(), text, getColor(), 0, Align.center, false);
		game.getFont().draw(batch, fontLayout, getX() + padding_side + offsetX + getWidth() / 2, getY() + fontLayout.height + padding_height + offsetY);

		float delta = Gdx.graphics.getDeltaTime();
		animationTimer += delta;
		//reset font size
		game.getFont().getData().setScale(oldFontSizeX, oldFontSizeY);

	}

	public void setGameMode(GameMode mode) {
		gameMode = mode;
		if (state == Animations.GM_BLEND_IN) {
			state = Animations.GM_BLEND_OUT;
		} else if (state != Animations.GM_BLEND_OUT) {
			state = Animations.GM_BLEND_OUT;
			animationTimer = 0;
			animationData = 0;
			animationDuration = state.getMinDuration();
		}
	}

	private void updateText() {
		text = SCORE_TEXT + game.userData.getHighScore(gameMode);
	}

	private void updateAnimation() {
		if (state == Animations.GM_BLEND_OUT) {
			state = Animations.GM_BLEND_IN;
		} else if (PixelEscape.rand.nextInt(10) < 4) {
			state = Animations.values()[PixelEscape.rand.nextInt(Animations.values().length)];
		} else {
			state = Animations.WAIT;
		}
		animationTimer = 0;
		animationData = 0;
		animationDuration = state.getDuration(PixelEscape.rand);

		switch (state) {
			case MOVE_X:
				animationData = 1 + PixelEscape.rand.nextInt(4);
				break;
			case MOVE_Y:
				animationData = 1 + PixelEscape.rand.nextInt(3);
				break;
			case BLEND:
//				Gdx.gl.glDisable(GL20.GL_BLEND);
				break;
		}
	}

	@Override
	public float getPrefWidth() {
		float oldFontSizeX = game.getFont().getScaleX();
		float oldFontSizeY = game.getFont().getScaleY();
		game.getFont().getData().setScale(font_size_x, font_size_y);
		float v = fontLayout.width + padding_side * 2;
		game.getFont().getData().setScale(oldFontSizeX, oldFontSizeY);
		return v;

	}

	@Override
	public float getPrefHeight() {
		float oldFontSizeX = game.getFont().getScaleX();
		float oldFontSizeY = game.getFont().getScaleY();
		game.getFont().getData().setScale(font_size_x, font_size_y);
		float v = fontLayout.height + padding_height * 2;
		game.getFont().getData().setScale(oldFontSizeX, oldFontSizeY);
		return v;
	}
}
