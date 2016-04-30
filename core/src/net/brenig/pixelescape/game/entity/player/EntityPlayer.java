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
import net.brenig.pixelescape.game.entity.particle.EntityCrashParticle;
import net.brenig.pixelescape.game.entity.player.abliity.Ability;
import net.brenig.pixelescape.game.entity.player.effects.StatusEffect;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

/**
 * the Player
 */
public class EntityPlayer extends Entity implements IMovingEntity {

	private int SIZE = Reference.PLAYER_ENTITY_SIZE;
	private int RADIUS = SIZE / 2;

	private float xVelocity;
	private float yVelocity;

	private float xVelocityModifier = 0;

	private int xPosScreen;

	private int extraLives;

	private float immortal = 0;

	private final PlayerPathEntity[] pathEntities = new PlayerPathEntity[4];

	/**
	 * used to allow for better acceleration of clicks
	 */
	private boolean lastTouched = false;

	private boolean isDead = false;

	private Ability currentAbility;
	private int remaingAbilityUses;

	private float cooldownRemaining = 0;

	private Set<StatusEffect> effects = new HashSet<StatusEffect>();

	public EntityPlayer(World world, GameMode gameMode) {
		super(world);
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i] = new PlayerPathEntity(yPos, xPosScreen);
		}
		reset(gameMode);
	}

	@Override
	public boolean update(float deltaTick, InputManager inputManager) {
		xPos += deltaTick *(xVelocity + xVelocityModifier);
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
			if(cooldownRemaining == 0)  {
				if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
					useAbility();
				}
			} else {
				cooldownRemaining = Math.max(0, cooldownRemaining - deltaTick);
			}
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
		xVelocity += Reference.SPEED_MODIFIER * deltaTick;

		yVelocity = Math.min(Reference.MAX_ENTITY_SPEED, yVelocity);
		xVelocity = Math.min(Reference.MAX_ENTITY_SPEED, xVelocity);

		//update trail
		for (int i = 0; i < pathEntities.length; i++) {
			if (i == 0) {
				pathEntities[i].update(this, deltaTick, this);
			} else {
				pathEntities[i].update(pathEntities[i - 1], deltaTick, this);
			}
		}
		//update effects
		final Iterator<StatusEffect> iterEffect = effects.iterator();
		while (iterEffect.hasNext()) {
			final StatusEffect effect = iterEffect.next();
			effect.update(deltaTick);
			if(!effect.effectActive()) {
				effect.onEffectRemove(this);
				iterEffect.remove();
			}
		}
		if(immortal <= 0) {
			if(!GameDebugSettings.get("DEBUG_GOD_MODE") && collide()) {
				return true;
			}
		} else {
			immortal -= deltaTick;
		}
		return false;
	}

	/**
	 * use the current ability of the player
	 */
	public void useAbility() {
		if(currentAbility.onActivate(world.getScreen(), world, this)) {
			remaingAbilityUses--;
			if(remaingAbilityUses == 0) {
				currentAbility = null;
				cooldownRemaining = 0;
			} else {
				cooldownRemaining = currentAbility.getCooldown();
			}
		}
	}

	/**
	 * resets this player entity to starting position
	 */
	public void reset(GameMode gameMode) {
		reviveAfterCrash();

		xPos = 0;
		xVelocity = gameMode.getStartingSpeed();
		isDead = false;
		extraLives = gameMode.getExtraLives();
		currentAbility = gameMode.getStartingAbility();
		remaingAbilityUses = gameMode.getStartingAbilityUses();
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
		for(StatusEffect effect : effects) {
			effect.onEffectRemove(this);
		}
		effects.clear();
		xVelocityModifier = 0;
		cooldownRemaining = 0;
	}

	/**
	 * @return player global x coordinate
	 */
	@Override
	public float getXPos() {
		return xPos + getXPosScreen();
	}

	@Override
	public float getMinX() {
		return xPos + getXPosScreen() - RADIUS;
	}

	@Override
	public float getMaxX() {
		return xPos + getXPosScreen() + RADIUS;
	}

	@Override
	public float getMinY() {
		return yPos - RADIUS;
	}

	@Override
	public float getMaxY() {
		return yPos + RADIUS;
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

	public float getXVelocity() {
		return xVelocity;
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

	/**
	 * adds the given value to the xVelocity ignoring maximum speed limit
	 * <p>
	 *     a negative value will decrease player speed
	 * </p>
	 */
	public void addXVelocityModifier(float xVelocityModifier) {
		this.xVelocityModifier += xVelocityModifier;
	}

	private boolean collide() {
		CollisionType col = world.doesAreaCollideWithWorld(xPosScreen - getPlayerSizeRadius(), yPos - getPlayerSizeRadius(), xPosScreen + getPlayerSizeRadius(), yPos + getPlayerSizeRadius());
		if(col != CollisionType.NONE) {
			boolean collide = true;
			for (StatusEffect effect : effects) {
				if (!effect.onPlayerCollide()) {
					collide = false;
				}
			}
			return collide && onPlayerCollide(col, world);
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
		renderer.renderRect(this.getXPosScreen() - this.getPlayerSize() / 2, this.getYPos() - this.getPlayerSize() / 2, this.getPlayerSize(), this.getPlayerSize());

		for (PlayerPathEntity e : this.getPathEntities()) {
			renderer.renderRect(e.getXPosScreen() - e.getSizeRadius(), e.getYPos() - e.getSizeRadius(), e.getSize(), e.getSize());
		}

		for (StatusEffect effect : effects) {
			effect.render(game, renderer, this, xPos, yPos, delta);
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

	/**
	 * sets the currently available ability
	 * @param ability new ability
	 * @param uses amount of times the player can use this ability (-1 for unlimited uses)
	 */
	public void setCurrentAbility(Ability ability, int uses) {
		if (this.currentAbility == ability) {
			if(uses > 0) {
				this.remaingAbilityUses += uses;
			} else {
				this.remaingAbilityUses = uses;
			}
		} else {
			this.cooldownRemaining = 0;
			this.currentAbility = ability;
			this.remaingAbilityUses = uses;
		}
	}

	public Ability getCurrentAbility() {
		return currentAbility;
	}

	public boolean hasAbility() {
		return currentAbility != null && remaingAbilityUses != 0;
	}

	public void increaseXPos(float x) {
		xPos += x;
	}

	public float getCooldownRemaining() {
		return cooldownRemaining;
	}

	public float getCooldownRemainingScaled() {
		if(currentAbility != null) {
			return cooldownRemaining / currentAbility.getCooldown();
		}
		return 0;
	}

	public int getExtraLives() {
		return extraLives;
	}

	public void setExtraLives(int extraLives) {
		this.extraLives = extraLives;
	}

	/**
	 * Gets called when player collides<br></br>
	 * used to spawn explosion and other effects as well as reducing lives/schowing gameover screen
	 * @param col type of collision
	 * @param world the world instance
	 */
	public boolean onPlayerCollide(CollisionType col, World world) {
		//Spawn particles
		final Random random = world.getRandom();
		for (int i = 0; i < 60; i++) {
			final float x = (float) Math.sin(i) + (random.nextFloat() - 0.5F);
			final float y = (float) Math.cos(i) + (random.nextFloat() - 0.5F);
			EntityCrashParticle e = world.createEntity(EntityCrashParticle.class);
			e.setPosition(getXPos() - getXVelocity() * Gdx.graphics.getDeltaTime() + x, getYPos() - getYVelocity() * Gdx.graphics.getDeltaTime() + y);
			e.setColor(world.getScreen().game.gameDebugSettings.getBoolean("PLAYER_EXPLOSION_RED") ? Color.RED : Color.BLACK);
			final float xVel = (x * 2 + (random.nextFloat() - 0.5F)) * 70;
			final float yVel = (y * 2 + (random.nextFloat() - 0.5F)) * 70;
			e.setVelocity(xVel, yVel);
			world.spawnEntity(e);
		}
		//apply screenshake
		//increase effect with higher score
		final float scoreModifier = 1 - 1 / (getScore() * 0.001F);
		final float force = 0.5F + random.nextFloat() * 0.5F * scoreModifier;
		//when colliding with Barricades, shake horizontally
		final boolean horizontal = col == CollisionType.ENTITY;
		world.getScreen().worldRenderer.applyForceToScreen(horizontal ? force : 0, horizontal ? 0 : force);

		//play sound
		if (world.getScreen().game.gameSettings.isSoundEnabled()) {
			world.getScreen().game.getGameAssets().getPlayerChrashedSound().play(world.getScreen().game.gameSettings.getSoundVolume());
		}

		//explode live icon
		if(world.getScreen().getGameMode().getExtraLives() > 0) {
			//We have a live system (and therefor have a lives icon)
			final float lifeX = world.convertScreenToWorldCoordinate(world.getScreen().game.gameSizeX - 36 * getExtraLives() + 16);
			final float lifeY = world.getWorldHeight() - 28 + 16;
			//Spawn crash particles
			for (int i = 0; i < 60; i++) {
				final float x = (float) Math.sin(i) + (random.nextFloat() - 0.5F);
				final float y = (float) Math.cos(i) + (random.nextFloat() - 0.5F);
				EntityCrashParticle e = world.createEntity(EntityCrashParticle.class);
				e.setPosition(lifeX + x, lifeY + y);
				e.setColor(Color.RED);
				e.setCollideTop(false);
				final float xVel = (x * 2 + (random.nextFloat() - 0.5F)) * 70;
				final float yVel = (y * 2 + (random.nextFloat() - 0.5F)) * 70;
				e.setVelocity(xVel, yVel);
				world.spawnEntity(e);
			}
		}
		//use lives
		if(getExtraLives() > 0) {
			extraLives--;
			setImmortal(3);
			reviveAfterCrash();
			return false;
		} else {
			setIsDead(true);
			world.getScreen().onGameOver();
			return true;
		}
	}

	/**
	 * adds a statuseffect to this player
	 * <p>
	 *     DO NOT CALL THIS WITHIN A {@link StatusEffect}!!!
	 * </p>
	 *
	 * @throws java.util.ConcurrentModificationException when access while player is updating status effects
	 */
	public void addEffect(StatusEffect effect) {
		effects.add(effect);
	}

	/**
	 * adds a statuseffect to this player
	 * <p>
	 *     also removes existing inscances of this effect (uses {@code instanceof} to find these)
	 * </p>
	 * <p>
	 *     DO NOT CALL THIS WITHIN A {@link StatusEffect}!!!
	 * </p>
	 *
	 * @throws java.util.ConcurrentModificationException when access while player is updating status effects
	 */
	public void addOrUpdateEffect(StatusEffect effect) {
		Iterator<StatusEffect> effectIterator = effects.iterator();
		Class<? extends StatusEffect> clazz = effect.getClass();
		while (effectIterator.hasNext()) {
			StatusEffect old = effectIterator.next();
			if(clazz.isInstance(old)) {
				old.onEffectRemove(this);
				effectIterator.remove();
			}
		}
		addEffect(effect);
	}
}
