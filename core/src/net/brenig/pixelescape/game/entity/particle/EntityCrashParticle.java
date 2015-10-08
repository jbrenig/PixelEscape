package net.brenig.pixelescape.game.entity.particle;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.lib.Reference;


/**
 * Created by Jonas Brenig on 07.10.2015.
 */
public class EntityCrashParticle extends Entity {

	private static final int size = 6;
	private static final int radius = size / 2;
	private static final float collisionSpeed = -0.4F;

	private float xPos, yPos;

	private float xVel = 0;
	private float yVel = 0;


	public EntityCrashParticle(World world, float xPos, float yPos) {
		super(world);
		this.xPos = xPos;
		this.yPos = yPos;
	}

	public void setVelocity(float xVel, float yVel) {
		this.xVel = xVel;
		this.yVel = yVel;
	}

	@Override
	public void render(PixelEscape game, float delta) {
		//Accelerate
		xPos += xVel * delta;
		yPos += yVel * delta;

		switch (doesCollide()) {
			case TERRAIN_BOT:
				yVel = 0;
				xVel = 0;
				break;
			case OBSTACLE:
				yVel = collisionSpeed * delta;
				xVel = 0;
				break;
			case TERRAIN_TOP:
			case NONE:
			default:
				yVel += Reference.GRAVITIY_ACCELERATION * delta;
				break;
		}

		xVel = Math.min(Reference.MAX_ENTITY_SPEED, xVel);
		yVel = Math.min(Reference.MAX_ENTITY_SPEED, yVel);

		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		game.shapeRenderer.setColor(Color.RED);
		game.shapeRenderer.rect(xPos - radius, yPos - radius, size, size);
		game.shapeRenderer.end();

	}

	@Override
	public boolean isDead() {
		return yPos - size >= worldObj.getWorldHeight() || yPos + size <= 0 || xPos - size >= worldObj.getWorldWidth() || xPos + size <= 0;
	}

	private CollisionType doesCollide() {
		return worldObj.doesAreaCollideWithWorld(xPos - radius, yPos - radius, xPos + radius, yPos + radius);
	}
}
