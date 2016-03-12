package net.brenig.pixelescape.game.entity.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.IMovingEntity;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.lib.CycleIntArray;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * the Player
 */
public class EntityPlayer extends Entity implements IMovingEntity {

	private double velocity;
	private float yVelocity;

	private int xPosScreen;

	public int extraLives;

	private float immortal = 0;

	private final PlayerPathEntity[] pathEntities = new PlayerPathEntity[4];

	private boolean lastTouched = false;

	private final CycleIntArray lastYPositions;
	private int lastXPosition = 0;

	private boolean isDead = false;

	private IAbility currentAbility;

	public EntityPlayer(World world, GameMode gameMode) {
		super(world);
		lastYPositions = new CycleIntArray(20, (int) yPos);
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i] = new PlayerPathEntity(yPos, xPosScreen);
		}
		reset(gameMode);
	}

	@Override
	public boolean update(float deltaTick, InputManager inputManager) {
		xPos += deltaTick * velocity;
		if(!GameDebugSettings.get("DEBUG_GOD_MODE")) {
			yPos += deltaTick * yVelocity;
			if(yPos < getPlayerSizeRadius()) {
				yPos = getPlayerSizeRadius();
				yVelocity = 0;
			} else if(yPos > world.getWorldHeight() - getPlayerSizeRadius()) {
				yPos = world.getWorldHeight() - getPlayerSizeRadius();
				yVelocity = 0;
			}
		}

		//trigger ability by key press
		if(hasAbility()) {
			if(currentAbility.cooldownRemaining() == 0)  {
				if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
					currentAbility.onActivate(world.getScreen(), world, this);
				}
			}
			currentAbility.update(world, this, deltaTick);
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

		//update trail
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
			if(collide()) {
				return true;
			}
		} else {
			immortal -= deltaTick;
		}
		return false;
	}

	/**
	 * resets this player entity to starting position
	 */
	public void reset(GameMode gameMode) {
		reviveAfterCrash();

		xPos = 0;
		velocity = gameMode.getStartingSpeed();
		isDead = false;
		extraLives = gameMode.getExtraLives();
		currentAbility = gameMode.getStartingAbility();
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
	 * @return player global x coordinate
	 */
	public float getXPos() {
		return xPos + getXPosScreen();
	}

	public int getScore() {
		return (int) xPos;
	}

	/**
	 * @return progress the player made (distance travelled)
	 */
	public float getProgress() {
		return xPos;
	}

	public double getVelocity() {
		return velocity;
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

	private boolean collide() {
		CollisionType col = world.doesAreaCollideWithWorld(xPosScreen - getPlayerSizeRadius(), yPos - getPlayerSizeRadius(), xPosScreen + getPlayerSizeRadius(), yPos + getPlayerSizeRadius());
		if(col != CollisionType.NONE) {
			return world.onPlayerCollide(col);
		}
		return false;
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
	public void render(PixelEscape game, WorldRenderer renderer, float xPos, float yPos, float delta) {
		if(this.isDead()) {
			return;
		}
		game.getRenderManager().beginFilledShape();

		// Draw Background color
		if(immortal % 1F < 0.5F) {
			game.getShapeRenderer().setColor(0, 0, 0, 1);
		} else {
			game.getShapeRenderer().setColor(Color.LIGHT_GRAY);
		}
		game.getShapeRenderer().rect(xPos + this.getXPosScreen() - this.getPlayerSize() / 2, this.getYPos() - this.getPlayerSize() / 2 + yPos, this.getPlayerSize(), this.getPlayerSize());

		for (PlayerPathEntity e : this.getPathEntities()) {
			game.getShapeRenderer().rect(xPos + e.getXPosScreen() - e.getSizeRadius(), yPos + e.getYPos() - e.getSizeRadius(), e.getSize(), e.getSize());
		}
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

	public void setCurrentAbility(net.brenig.pixelescape.game.entity.player.abliity.IAbility currentAbility) {
		this.currentAbility = currentAbility;
	}

	public net.brenig.pixelescape.game.entity.player.abliity.IAbility getCurrentAbility() {
		return currentAbility;
	}

	public boolean hasAbility() {
		return currentAbility != null;
	}

	public void increaseXPos(float x) {
		xPos += x;
	}
}
