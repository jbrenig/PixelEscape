package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.Align;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Utils;

import java.util.Random;

/**
 * Created by Jonas Brenig on 27.10.2015.
 */
public class CurrentHighscoreLabel extends Widget {
	
	private static final String SCORE_TEXT = "Current Highscore: ";
	private static final float padding_side = 4;
	private static final float padding_height = 4;

	private static final float font_size_x = 0.6F;
	private static final float font_size_y = 0.8F;

	private static final float font_scaling_strength = 0.5F;

	private static final Random random = new Random();

	private GlyphLayout fontLayout;
	private PixelEscape game;

	private enum Animations {
		WAIT(2, 8), BLEND(0.4F, 2F), MOVE_X(0.4F, 4F), MOVE_Y(0.4F, 8), SIZE(0.5F, 0.5F);

		private float minDuration;
		private float maxDuration;

		Animations(float minDuration, float maxDuration) {
			this.minDuration = minDuration;
			this.maxDuration = maxDuration;
		}

		public float getDuration(Random random) {
			return minDuration + random.nextFloat() * (maxDuration - minDuration);
		}
	}

	private Animations state;

	private float animationTimer = 0;
	private float animationDuration = 0;
	private int animationData = 0;

	private final String text;

	public CurrentHighscoreLabel() {
		super();
		game = PixelEscape.getPixelEscape();
		text = SCORE_TEXT + game.userData.highScore;
		fontLayout = new GlyphLayout(game.font, text);
		state = Animations.WAIT;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//validate layout
		super.draw(batch, parentAlpha);

		if(animationTimer >= animationDuration) {
			updateAnimation();
		}

		float oldFontSizeX = game.font.getScaleX();
		float oldFontSizeY = game.font.getScaleY();
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
				offsetX = (float) (Math.sin((animationTimer / animationDuration) * animationData * Math.PI) * 10F);
				break;
			case MOVE_Y:
				offsetY = (float) (Math.sin((animationTimer / animationDuration) * animationData * Math.PI) * 10F);
				break;
			case WAIT:
				break;
			case SIZE: {
				float part = animationTimer / animationDuration;
				if(part < 0.5) {
					float ease = Utils.easeInAndOut(part, 0.5F) * font_scaling_strength;
					fontSizeX = font_size_x - ease * font_size_x;
					fontSizeY = font_size_y - ease * font_size_y;
				} else {
					float ease = Utils.easeInAndOut(part - 0.5F, 0.5F) * font_scaling_strength;
					fontSizeX = ease * font_size_x;
					fontSizeY = ease * font_size_y;
				}
			}
				break;

		}
		//Score text
		setColor(0, 0, 0, alpha);
		game.font.getData().setScale(fontSizeX, fontSizeY);
		fontLayout.setText(game.font, text, getColor(), 0, Align.center, false);
		game.font.draw(batch, fontLayout, getX() + padding_side + offsetX + getWidth() / 2, getY() + fontLayout.height + padding_height + offsetY);

		float delta = Gdx.graphics.getDeltaTime();
		animationTimer += delta;
		//reset fontsize
		game.font.getData().setScale(oldFontSizeX, oldFontSizeY);

	}


	private void updateAnimation() {
		if(random.nextInt(10) < 4) {
			state = Animations.values()[random.nextInt(Animations.values().length)];
		} else {
			state = Animations.WAIT;
		}
		animationTimer = 0;
		animationData = 0;
		animationDuration = state.getDuration(random);

		switch (state) {
			case MOVE_X:
				animationData = 1 + random.nextInt(4);
				break;
			case MOVE_Y:
				animationData = 1 + random.nextInt(3);
				break;
			case BLEND:
				Gdx.gl.glDisable(GL20.GL_BLEND);
				break;
		}
	}

	@Override
	public float getPrefWidth() {
		float oldFontSizeX = game.font.getScaleX();
		float oldFontSizeY = game.font.getScaleY();
		game.font.getData().setScale(font_size_x, font_size_y);
		float v = fontLayout.width + padding_side * 2;
		game.font.getData().setScale(oldFontSizeX, oldFontSizeY);
		return v;

	}

	@Override
	public float getPrefHeight() {
		float oldFontSizeX = game.font.getScaleX();
		float oldFontSizeY = game.font.getScaleY();
		game.font.getData().setScale(font_size_x, font_size_y);
		float v = fontLayout.height + padding_height * 2;
		game.font.getData().setScale(oldFontSizeX, oldFontSizeY);
		return v;
	}
}
