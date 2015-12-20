package net.brenig.pixelescape.game.entity.player;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.IMovingEntity;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.lib.CycleIntArray;
import net.brenig.pixelescape.lib.Reference;

/**
 * the Player
 */
public class EntityPlayer extends Entity implements IMovingEntity {

	private double velocity;
	private float yVelocity;

	private int xPosScreen;

	private float yPos;
	private float xPos;

	public int extraLives;

	private float immortal = 0;

	private final PlayerPathEntity[] pathEntities = new PlayerPathEntity[4];

	private boolean lastTouched = false;

	private final CycleIntArray lastYPositions;
	private int lastXPosition = 0;

	private boolean isDead = false;

	public EntityPlayer(World world, GameMode gameMode) {
		super(world);
		lastYPositions = new CycleIntArray(20, (int) yPos);
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i] = new PlayerPathEntity(yPos, xPosScreen);
		}
		reset(gameMode);
	}

	public void update(float deltaTick, InputManager inputManager) {
		xPos += deltaTick * velocity;
		if(!GameDebugSettings.get("DEBUG_GOD_MODE")) {
			yPos += deltaTick * yVelocity;
		}

		//speed update
		if (inputManager.isTouched() || inputManager.isSpaceDown()) {
			if(!lastTouched) {
				yVelocity += Reference.CLICK_ACCELERATION;
				lastTouched = true;
			} else {
				yVelocity += Reference.TOUCH_ACCELERATION * deltaTick;
				lastTouched = true;
			}
		} else {
			yVelocity += Reference.GRAVITY_ACCELERATION * deltaTick;
			lastTouched = false;
		}
		velocity += Reference.SPEED_MODIFIER * deltaTick;

		yVelocity = Math.min(Reference.MAX_ENTITY_SPEED, yVelocity);
		velocity = Math.min(Reference.MAX_ENTITY_SPEED, velocity);

		for (int i = 0; i < pathEntities.length; i++) {
			if (i == 0) {
				pathEntities[i].update(this, deltaTick, this, lastYPositions.getFromNewest(4));
			} else {
				pathEntities[i].update(pathEntities[i - 1], deltaTick, this, lastYPositions.getFromNewest(4 + i * 5));
			}
		}

		if(getXPos() - lastXPosition > Reference.PATH_ENTITY_OFFSET / 5) {
			lastYPositions.add((int) yPos);
			lastXPosition = (int) getXPos();
		}
		if(immortal <= 0 && !GameDebugSettings.get("DEBUG_GOD_MODE")) {
			collide();
		} else {
			immortal -= deltaTick;
		}
	}

	/**
	 * resets this player entity to starting position
	 */
	public void reset(GameMode gameMode) {
//		xPos = getXPosScreen();
		reviveAfterCrash();

		xPos = 0;
		velocity = gameMode.getStartingSpeed();
		isDead = false;
		extraLives = gameMode.getExtraLives();
	}

	/**
	 * resets player to start in the y-center of the screen<br></br>
	 * used for reviving player after crashing (when he still has a life left)
	 */
	public void reviveAfterCrash() {
		yPos = Reference.GAME_RESOLUTION_Y / 2;
		yVelocity = 0;
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i].reset(yPos, xPosScreen - Reference.PATH_ENTITY_OFFSET * (i + 1));
		}
		lastYPositions.fill((int) yPos);
	}

	/**
	 * player progress in pixels
	 */
	public float getXPos() {
		return xPos + getXPosScreen();
	}

	public int getScore() {
		return (int) xPos;
	}

	public double getVelocity() {
		return velocity;
	}

	@Override
	public float getYPos() {
		return yPos;
	}

	public int getXPosScreen() {
		return xPosScreen;
	}

	public void setXPosScreen(int xPosScreen) {
		this.xPosScreen = xPosScreen;
		for(int i = 0; i < pathEntities.length; i++) {
			pathEntities[i].setXPosScreen(xPosScreen - (Reference.PATH_ENTITY_OFFSET * (i + 1)));
		}
	}

	private void collide() {
		CollisionType col = worldObj.doesAreaCollideWithWorld(xPosScreen - getPlayerSizeRadius(), yPos - getPlayerSizeRadius(), xPosScreen + getPlayerSizeRadius(), yPos + getPlayerSizeRadius());
		if(col != CollisionType.NONE) {
			worldObj.onPlayerCollide(col);
		}
	}

	public PlayerPathEntity[] getPathEntities() {
		return pathEntities;
	}

	@SuppressWarnings("SameReturnValue")
	public int getPlayerSize() {
		return Reference.PLAYER_ENTITY_SIZE;
	}

	public int getPlayerSizeRadius() {
		return getPlayerSize() / 2;
	}

	public float getYVelocity() {
		return yVelocity;
	}

	public void setIsDead(boolean isDead) {
		this.isDead = isDead;
	}

	@Override
	public void render(PixelEscape game, float delta, float xPos, float yPos) {
		if(this.isDead()) {
			return;
		}
		game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

		// Draw Background color
		if(immortal % 1F < 0.5F) {
			game.shapeRenderer.setColor(0, 0, 0, 1);
		} else {
			game.shapeRenderer.setColor(Color.LIGHT_GRAY);
		}
		game.shapeRenderer.rect(xPos + this.getXPosScreen() - this.getPlayerSize() / 2, this.getYPos() - this.getPlayerSize() / 2 + yPos, this.getPlayerSize(), this.getPlayerSize());

		for (PlayerPathEntity e : this.getPathEntities()) {
			game.shapeRenderer.rect(xPos + e.getXPosScreen() - e.getSizeRadius(), yPos + e.getYPos() - e.getSizeRadius(), e.getSize(), e.getSize());
		}

		// End ShapeRenderer
		game.shapeRenderer.end();
	}

	@Override
	public boolean isDead() {
		return isDead;
	}

	/**
	 * makes player immortal
	 * @param time time in seconds to remain immortal
	 */
	public void setImmortal(float time) {
		immortal = time;
	}
}
