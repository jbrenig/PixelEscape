package net.brenig.pixelescape.game.entity.impl.particle;

import com.badlogic.gdx.graphics.Color;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;


/**
 * Particle entity that spawns when the player dies
 */
public class EntityCrashParticle extends Entity {

	private static final int size = 6;
	private static final int radius = size / 2;
	private static final float collisionSpeed = -0.4F;

	private float xVel = 0;
	private float yVel = 0;

	private Color color = Color.BLACK;

	private boolean collideTop = true;

	private boolean dead;

	private final static float BOUNCE_X = 0.4F;
	private final static float BOUNCE_Y = 0.1F;

	private final static float ANTI_BOUNCE_X= 10;
	private final static float ANTI_BOUNCE_Y = 20;


	public void setVelocity(float xVel, float yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		if(isDead()) {
			return;
		}
		//Move
		xPos += xVel * delta;
		yPos += yVel * delta;

		//Accelerate and collide
		switch (doesCollide()) {
			case TERRAIN_BOT_RIGHT:
				if(xVel > 0) bounceX();
				else slowDownX(delta);

				if(yVel <= 0) bounceY();
				else gravityY(delta);

				break;
			case TERRAIN_BOT_LEFT:
				if(xVel < 0) bounceX();
				else slowDownX(delta);

				if(yVel <= 0) bounceY();
				else gravityY(delta);

				break;
			case TERRAIN_BOTTOM:

				if(yVel <= 0) {
					bounceY();
					if(yVel != 0) {
						slowDownX(delta * 10);
					} else {
						if(xVel > ANTI_BOUNCE_X) {
							slowDownX(delta * 10);
						} else {
							xVel = 0;
						}
					}
				} else {
					slowDownX(delta);
					gravityY(delta);
				}

				break;
			case TERRAIN_RIGHT:
				if(xVel > 0) bounceX();
				else  slowDownX(delta);

				gravityY(delta);

				break;
			case TERRAIN_LEFT:
				if(xVel < 0) bounceX();
				else  slowDownX(delta);

				gravityY(delta);

				break;
			case TERRAIN_TOP:
				if(collideTop) {
					slowDownX(delta);

					if (yVel > 0) bounceY();
					else gravityY(delta);

					break;
				}
			case ENTITY:
				if(xVel > 0) bounceX();
				else slowDownX(delta);
//				gravityY(delta);
//				yVel = collisionSpeed * delta;
//				xVel = 0;
				break;
			case TERRAIN_TOP_RIGHT:
				if(collideTop) {
					if (xVel > 0) bounceX();
					else slowDownX(delta);

					if (yVel > 0) bounceY();
					else gravityY(delta);

					break;
				}
			case TERRAIN_TOP_LEFT:
				if(collideTop) {
					if (xVel < 0) bounceX();
					else slowDownX(delta);

					if (yVel > 0) bounceY();
					else gravityY(delta);
					break;
				}
			case NONE:
			default:
				slowDownX(delta);
				gravityY(delta);
				break;
		}

		xVel = Math.min(gameMode.getMaxEntitySpeed(), xVel);
		yVel = Math.min(gameMode.getMaxEntitySpeed(), yVel);

		game.getRenderManager().begin();
		game.getRenderManager().setColor(color);
		renderer.renderRectWorld(xPos - radius, yPos - radius, size, size);
	}

	private void slowDownX(float delta) {
		xVel -= xVel * 0.5F * delta;
	}

	private void gravityY(float delta) {
		yVel += Reference.GRAVITY_ACCELERATION * delta;
	}

	private void bounceX() {
		if(Math.abs(xVel) > ANTI_BOUNCE_X) {
			this.xVel = -xVel * BOUNCE_X;
		} else {
			xVel = 0;
		}
	}

	private void bounceY() {
		if(Math.abs(yVel) > ANTI_BOUNCE_Y) {
			this.yVel = -yVel * BOUNCE_Y;
		} else {
			yVel = 0;
		}
	}

	@Override
	public boolean isDead() {
		if(dead) return true;
		float renderX = world.convertWorldCoordToScreenCoord(xPos);
		return dead = (yPos - radius >= world.getWorldHeight() || yPos + radius <= 0 || renderX - radius >= world.getWorldWidth() || renderX + radius <= 0);
	}

	private CollisionType doesCollide() {
		return world.doesAreaCollideWithWorld(xPos - radius, yPos - radius, xPos + radius, yPos + radius);
	}

	/**
	 * set whether the particle should collide with the top terrain<br></br>
	 * true is default
	 */
	public void setCollideTop(boolean collideTop) {
		this.collideTop = collideTop;
	}

	@Override
	public void reset() {
		super.reset();
		xPos = 0;
		yPos = 0;
		xVel = 0;
		yVel = 0;
		color = Color.BLACK;
		collideTop = true;
		dead = false;
	}
}
