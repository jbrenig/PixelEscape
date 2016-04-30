package net.brenig.pixelescape.game.entity.particle;

import com.badlogic.gdx.graphics.Color;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.World;
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


	public EntityCrashParticle(World world) {
		super(world);
	}

	public void setVelocity(float xVel, float yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, float x, float y, float delta) {
		if(isDead()) {
			return;
		}
		//Move
		progress += xVel * delta;
		yPos += yVel * delta;

		//Accelerate and collide
		switch (doesCollide()) {
			case TERRAIN_BOT_RIGHT:
			case TERRAIN_BOT_LEFT:
				yVel = 0;
				xVel = 0;
				break;
			case ENTITY:
				yVel = collisionSpeed * delta;
				xVel = 0;
				break;
			case TERRAIN_TOP_RIGHT:
				if(collideTop) {
					if (xVel > 0) {
						xVel = 0;
					}
					if (yVel > 0) {
						yVel = 0;
					} else {
						yVel += Reference.GRAVITY_ACCELERATION * delta;
					}
					break;
				}
			case TERRAIN_TOP_LEFT:
				if(collideTop) {
					if (xVel < 0) {
						xVel = 0;
					}
					if (yVel > 0) {
						yVel = 0;
					} else {
						yVel += Reference.GRAVITY_ACCELERATION * delta;
					}
					break;
				}
			case NONE:
			default:
				xVel -= xVel * 0.5F * delta;
				yVel += Reference.GRAVITY_ACCELERATION * delta;
				break;
		}

		xVel = Math.min(Reference.MAX_ENTITY_SPEED, xVel);
		yVel = Math.min(Reference.MAX_ENTITY_SPEED, yVel);

		float renderX = world.convertWorldCoordToScreenCoord(progress);

		game.getRenderManager().beginFilledShape();
		game.getShapeRenderer().setColor(color);
		game.getShapeRenderer().rect(x + renderX - radius, y + yPos - radius, size, size);
	}

	@Override
	public boolean isDead() {
		float renderX = world.convertWorldCoordToScreenCoord(progress);
		return yPos - radius >= world.getWorldHeight() || yPos + radius <= 0 || renderX - radius >= world.getWorldWidth() || renderX + radius <= 0;
	}

	private CollisionType doesCollide() {
		return world.doesAreaCollideWithWorld(progress - radius, yPos - radius, progress + radius, yPos + radius);
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
		progress = 0;
		yPos = 0;
		xVel = 0;
		yVel = 0;
		color = Color.BLACK;
		collideTop = true;
	}
}
