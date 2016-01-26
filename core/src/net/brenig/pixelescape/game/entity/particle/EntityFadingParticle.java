package net.brenig.pixelescape.game.entity.particle;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.lib.Utils;

public class EntityFadingParticle extends Entity {

	private static final int size = 4;
	private static final int radius = size / 2;

	private float xPos, yPos;

	private float xVel = 0;
	private float yVel = 0;

	private float fadeDuration = 0.5F;
	private float fadeTimePassed = 0;

	private float xAccelerationFactor = 1F;
	private float yAccelerationFactor = 1F;

	private final float color_r;
	private final float color_g;
	private final float color_b;

	public EntityFadingParticle(World world, float xPos, float yPos, Color color, float fadeDuration) {
		this(world, xPos, yPos, color.r, color.g, color.b, fadeDuration);
	}

	public EntityFadingParticle(World world, float xPos, float yPos, float color_r, float color_g, float color_b, float fadeDuration) {
		super(world);
		this.xPos = xPos;
		this.yPos = yPos;
		this.color_r = color_r;
		this.color_g = color_g;
		this.color_b = color_b;
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
	public void render(PixelEscape game, float delta, float x, float y) {
		if(isDead()) {
			return;
		}
		//Move
		xPos += xVel * delta;
		yPos += yVel * delta;

		xVel = Math.min(Reference.MAX_ENTITY_SPEED, xVel * xAccelerationFactor);
		yVel = Math.min(Reference.MAX_ENTITY_SPEED, yVel * yAccelerationFactor);

		fadeTimePassed += delta;

		float currentAlpha = 1 - Utils.easeInAndOut(fadeTimePassed, fadeDuration);

		float renderX = worldObj.convertWorldCoordToScreenCoord(xPos);

		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		Gdx.gl.glEnable(GL20.GL_BLEND);
		game.shapeRenderer.setColor(color_r, color_g, color_b, currentAlpha);
		game.shapeRenderer.rect(x + renderX - radius, y + yPos - radius, size, size);
		game.shapeRenderer.end();
	}

	@Override
	public boolean isDead() {
		return fadeTimePassed >= fadeDuration;
	}

	public float getY() {
		return yPos;
	}

	public float getX() {
		return xPos;
	}
}
