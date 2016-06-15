package net.brenig.pixelescape.game.entity.impl.particle;

import com.badlogic.gdx.graphics.Color;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.render.WorldRenderer;

public class EntityFadingParticle extends Entity {

	private static final int size = 4;
	private static final int radius = size / 2;

	private float xVel = 0;
	private float yVel = 0;

	private float fadeDuration = 0.5F;
	private float fadeTimePassed = 0;

	private float xAccelerationFactor = 1F;
	private float yAccelerationFactor = 1F;

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

	public void setFadeDuration(float fadeDuration) {
		this.fadeDuration = fadeDuration;
	}

	public void setAccelerationFactor(float xAcc, float yAcc) {
		this.xAccelerationFactor = xAcc;
		this.yAccelerationFactor = yAcc;
	}

	public void setVelocity(float xVel, float yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		if(isDead()) {
			return;
		}
		//Move
		xPos += xVel * delta;
		yPos += yVel * delta;

		xVel = Math.min(gameMode.getMaxEntitySpeed(), xVel * xAccelerationFactor);
		yVel = Math.min(gameMode.getMaxEntitySpeed(), yVel * yAccelerationFactor);

		fadeTimePassed += delta;

		float currentAlpha = 1 - Utils.easeInAndOut(fadeTimePassed, fadeDuration);

		game.getRenderManager().begin();
		game.getRenderManager().setColor(color_r, color_g, color_b, currentAlpha);
		renderer.renderRectWorld(xPos - radius, yPos - radius, size, size);
	}

	@Override
	public boolean isDead() {
		return fadeTimePassed >= fadeDuration;
	}


	@Override
	public void reset() {
		super.reset();
		color_r = 0;
		color_g = 0;
		color_b = 0;
		xVel = 0;
		yVel = 0;
		fadeDuration = 0.5F;
		fadeTimePassed = 0;
		xAccelerationFactor = 1F;
		yAccelerationFactor = 1F;
	}
}
