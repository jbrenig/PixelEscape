package net.brenig.pixelescape.game.entity;

import com.badlogic.gdx.utils.Pool;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Entity that can be spawned into the world
 */
public abstract class Entity implements Pool.Poolable {

	protected World world;
	protected float xPos;
	protected float yPos;

	public Entity() {
	}

	/**
	 * sets the position of this entity
	 */
	public void setPosition(float xPos, float yPos) {
		this.xPos = xPos;
		this.yPos = yPos;
	}

	/**
	 * @return the xCoordinate of the left corner of this entity
	 */
	public float getMinX() {
		return xPos;
	}


	/**
	 * @return the xCoordinate of the right corner of this entity
	 */
	public float getMaxX() {
		return xPos;
	}


	/**
	 * @return the yCoordinate of the upper corner of this entity
	 */
	public float getMinY() {
		return yPos;
	}


	/**
	 * @return the yCoordinate of the bottom corner of this entity
	 */
	public float getMaxY() {
		return yPos;
	}

	public float getXPos() {
		return xPos;
	}

	public float getYPos() {
		return yPos;
	}

	/**
	 * renders the entity in the background
	 * <p/>
	 * gets called before terrain is rendered
	 *
	 * @param game     game instance
	 * @param renderer renderer instance
	 * @param gameMode current gamemode
	 * @param delta    time passed since last frame
	 */
	public void renderBackground(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
	}

	/**
	 * renders the entity
	 *
	 * @param game     game instance
	 * @param renderer renderer instance
	 * @param gameMode current gamemode
	 * @param delta    time passed since last frame
	 */
	public void render(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
	}

	/**
	 * update the entity, gets called every frame
	 *
	 * @return true if game update should be cancelled (eg. gameover)
	 */
	@SuppressWarnings("EmptyMethod")
	public boolean update(float delta, InputManager inputManager, GameMode gameMode) {
		return false;
	}

	/**
	 * checks whether the entity is dead (--> should be removed from the world)
	 * <br/>
	 * default implementation checks if the entities right edge left the screen (to the left)
	 *
	 * @return true if the entity should not be used anymore, and is ready to be removed
	 */
	public boolean isDead() {
		return getMaxX() < world.getCurrentScreenStart();
	}

	@SuppressWarnings("EmptyMethod")
	public void removeEntityOnDeath() {

	}

	@Override
	public void reset() {
		xPos = 0;
		yPos = 0;
	}

	/**
	 * checks whether the given area collides with this entity
	 * <p>
	 * used to check for collision (with world or rigid objects)
	 *
	 * @param x1 left x coordinate
	 * @param y1 bottom y coordinate
	 * @param x2 right x coordinate
	 * @param y2 upper y coordinate
	 * @return whether the area collides with this entity
	 */
	public CollisionType doesAreaCollideWithEntity(float x1, float y1, float x2, float y2) {
		return CollisionType.NONE;
	}

	/**
	 * returns whether this entity intersects with the given area
	 *
	 * @param x1 left x coordinate
	 * @param y1 bottom y coordinate
	 * @param x2 right x coordinate
	 * @param y2 upper y coordinate
	 * @return whether the area intersects with this entity
	 */
	public boolean doesAreaIntersectWithEntity(float x1, float y1, float x2, float y2) {
		if (getMinX() < x2 && getMaxX() > x1) {
			if (getMinY() < y2 && getMaxY() > y1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * returns whether this entity intersects with the given entity
	 *
	 * @param other the entity to intersect with
	 * @return whether the area intersects with this entity
	 */
	public boolean doesEntityIntersectWithEntity(Entity other) {
		return other.doesAreaIntersectWithEntity(getMinX(), getMinY(), getMaxX(), getMaxY());
	}

	/**
	 * returns whether this entity intersects with the given entity
	 *
	 * @param other the entity to intersect with
	 * @return whether the area intersects with this entity
	 */
	public CollisionType doesEntityCollideWithEntity(Entity other) {
		return other.doesAreaCollideWithEntity(getMinX(), getMinY(), getMaxX(), getMaxY());
	}

	public World getWorld() {
		return world;
	}

	public void setWorld(World world) {
		this.world = world;
	}
}
