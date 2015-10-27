package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

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

	private static final Random random = new Random();

	private GlyphLayout fontLayout;
	private PixelEscape game;

	private enum Animations {
		WAIT(2, 8), BLEND(0.4F, 2F), MOVE_X(0.4F, 4F), SIZE(0.4F, 0.8F);

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


	public CurrentHighscoreLabel() {
		super();
		game = PixelEscape.getPixelEscape();
		fontLayout = new GlyphLayout(game.font, SCORE_TEXT + game.userData.highScore);
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//validate layout
		super.draw(batch, parentAlpha);

		if(animationTimer >= animationDuration) {
			updateAnimation();
		}



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
					alpha = Utils.easeOut(animationTimer, animationDuration, 2);
				}
			}
				break;
			case MOVE_X:
				offsetX = (float) (Math.sin(Utils.easeInAndOut(animationTimer, animationDuration) * 2 * Math.PI) * 10F);
				break;
			case WAIT:
				break;
			case SIZE: {
				float part = animationDuration / 2;
				if(animationTimer < part) {
					game.font.getData().setScale(1 - Utils.easeInAndOut(animationTimer, animationDuration));
				} else {
					game.font.getData().setScale(Utils.easeInAndOut(animationTimer, animationDuration));
				}
			}
				break;

		}
		//Score text
		game.font.setColor(0, 0, 0, alpha);
		game.font.draw(batch, fontLayout, getX() + padding_side + offsetX, getY() + fontLayout.height + padding_height + offsetY);

		float delta = Gdx.graphics.getDeltaTime();
		animationTimer += delta;
	}

	private void updateAnimation() {
		game.font.getData().setScale(1);
		if(random.nextInt(10) < 4) {
			state = Animations.values()[random.nextInt(Animations.values().length)];
		} else {
			state = Animations.WAIT;
		}
		animationTimer = 0;
		animationDuration = state.getDuration(random);
	}

	@Override
	public float getPrefWidth() {
		return fontLayout.width + padding_side * 2;
	}

	@Override
	public float getPrefHeight() {
		return fontLayout.height + padding_height * 2;
	}
}
