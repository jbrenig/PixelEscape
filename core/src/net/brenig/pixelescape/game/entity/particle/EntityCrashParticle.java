package net.brenig.pixelescape.game.entity.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.lib.Reference;


/**
 * Particle entity that spawns when the player dies
 */
public class EntityCrashParticle extends Entity {

	private static final int size = 6;
	private static final int radius = size / 2;
	private static final float collisionSpeed = -0.4F;

	private float xPos, yPos;

	private float xVel = 0;
	private float yVel = 0;

	private final Color color;

	private boolean collideTop = true;


	public EntityCrashParticle(World world, float xPos, float yPos, Color color) {
		super(world);
		this.xPos = xPos;
		this.yPos = yPos;
		this.color = color;
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
		//Accelerate
		xPos += xVel * delta;
		yPos += yVel * delta;

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

		float renderX = worldObj.convertWorldCoordToScreenCoord(xPos);

		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		game.shapeRenderer.setColor(color);
		game.shapeRenderer.rect(x + renderX - radius, y + yPos - radius, size, size);
		game.shapeRenderer.end();

	}

	@Override
	public boolean isDead() {
		float renderX = worldObj.convertWorldCoordToScreenCoord(xPos);
		return yPos - radius >= worldObj.getWorldHeight() || yPos + radius <= 0 || renderX - radius >= worldObj.getWorldWidth() || renderX + radius <= 0;
	}

	private CollisionType doesCollide() {
		float renderX = worldObj.convertWorldCoordToScreenCoord(xPos);
		return worldObj.doesAreaCollideWithWorld(renderX - radius, yPos - radius, renderX + radius, yPos + radius);
	}

	/**
	 * set whether the particle should collide with the top terrain<br></br>
	 * true is default
	 */
	public void setCollideTop(boolean collideTop) {
		this.collideTop = collideTop;
	}
}
