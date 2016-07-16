package net.brenig.pixelescape.game.entity.impl.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Entity that renders a given string for a given duration (default: 0.6F)
 */
public class EntityFadingText extends Entity {

	private float fadeDuration;
	private float timePassed;
	private String text;

	private float color_r = 0;
	private float color_g = 0;
	private float color_b = 0;


	public void setColor(float color_r, float color_g, float color_b) {
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
	}

	public void setColor(Color color) {
		setColor(color.r, color.g, color.b);
	}

	@Override
	public void renderBackground(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		renderer.getRenderManager().begin();


		Gdx.gl.glEnable(GL20.GL_BLEND);
		float currentAlpha = 1 - Utils.easeOut(timePassed, fadeDuration, 2);
		renderer.getRenderManager().getFont().setColor(color_r, color_g, color_b, currentAlpha);
		renderer.getRenderManager().setFontScale(0.5F);
		renderer.renderTextWorld(text, xPos, yPos);

		timePassed += delta;
	}

	/**
	 * sets the text to be displayed
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * sets lifetime of this entity
	 *
	 * @param fadeDuration time in seconds
	 */
	public void setFadeDuration(float fadeDuration) {
		this.fadeDuration = fadeDuration;
	}

	/**
	 * sets the text to be displayed and life time of this entity
	 *
	 * @param text         text to be displayed
	 * @param fadeDuration time in seconds
	 * @see #setFadeDuration(float)
	 */
	public void setText(String text, float fadeDuration) {
		this.text = text;
		this.fadeDuration = fadeDuration;
	}


	@Override
	public boolean isDead() {
		return timePassed > fadeDuration || getMaxX() < world.getCurrentScreenStart();
	}

	@Override
	public void reset() {
		text = null;
		timePassed = 0;
		fadeDuration = 0.6F;
		setColor(Color.DARK_GRAY);
	}
}
