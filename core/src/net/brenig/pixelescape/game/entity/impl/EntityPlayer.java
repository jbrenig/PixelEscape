package net.brenig.pixelescape.game.entity.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.CollisionType;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameDebugSettings;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.Entity;
import net.brenig.pixelescape.game.entity.IMovingEntity;
import net.brenig.pixelescape.game.entity.impl.particle.EntityCrashParticle;
import net.brenig.pixelescape.game.player.PlayerPathEntity;
import net.brenig.pixelescape.game.player.abliity.Ability;
import net.brenig.pixelescape.game.player.effects.StatusEffect;
import net.brenig.pixelescape.game.player.movement.PlayerMovementController;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;

import java.util.*;

/**
 * the Player
 */
public class EntityPlayer extends Entity implements IMovingEntity {

	private int SIZE = Reference.PLAYER_ENTITY_SIZE;
	private int RADIUS = SIZE / 2;

	private PlayerMovementController movementController;

	private float xVelocity;
	private float yVelocity;

	private float xVelocityModifier = 0;
	private float yVelocityFactor = 1;

	private int xPosScreen;

	private int extraLives;

	private float immortal = 0;

	private int bonusScore;

	private final PlayerPathEntity[] pathEntities = new PlayerPathEntity[4];

	private boolean isDead = false;

	private Ability currentAbility;
	private int remainingAbilityUses;

	private float cooldownRemaining = 0;

	private Set<StatusEffect> effects = new HashSet<StatusEffect>();

	public EntityPlayer(World world, GameMode gameMode) {
		movementController = gameMode.createPlayerMovementController();
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i] = new PlayerPathEntity(yPos, xPosScreen);
		}
		reset(gameMode);
		setWorld(world);
	}

	@Override
	public boolean update(float deltaTick, InputManager inputManager, GameMode gameMode) {
		xPos += deltaTick * (xVelocity + xVelocityModifier);
		if (!GameDebugSettings.Companion.get("DEBUG_GOD_MODE")) {
			yPos += deltaTick * yVelocity;
			//make sure player doesn't leave the screen
			if (yPos < getPlayerSizeRadius()) {
				yPos = getPlayerSizeRadius();
				yVelocity = 0;
			} else if (yPos > world.getWorldHeight() - getPlayerSizeRadius()) {
				yPos = world.getWorldHeight() - getPlayerSizeRadius();
				yVelocity = 0;
			}
		}

		//trigger ability by key press
		if (hasAbility()) {
			if (cooldownRemaining == 0) {
				if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
					useAbility();
				}
			} else {
				cooldownRemaining = Math.max(0, cooldownRemaining - deltaTick);
			}
		}

		//speed update
		movementController.updatePlayerMovement(world.getScreen().getGame(), inputManager, gameMode, world, this, deltaTick, yVelocityFactor);

		xVelocity = Math.min(gameMode.getMaxEntitySpeed(), xVelocity);
		yVelocity = Math.min(gameMode.getMaxEntitySpeed(), yVelocity);

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
			if (!effect.effectActive()) {
				effect.onEffectRemove(this);
				iterEffect.remove();
			}
		}
		if (immortal <= 0) {
			if (!GameDebugSettings.Companion.get("DEBUG_GOD_MODE") && collide()) {
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
		if (currentAbility.onActivate(world.getScreen(), world, this)) {
			remainingAbilityUses--;
			if (remainingAbilityUses == 0) {
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

		movementController.reset(gameMode);

		xPos = 0;
		bonusScore = 0;
		xVelocity = gameMode.getStartingSpeed();
		xVelocityModifier = 0;

		isDead = false;
		extraLives = gameMode.getExtraLives();

		currentAbility = gameMode.getStartingAbility();
		remainingAbilityUses = gameMode.getStartingAbilityUses();
		cooldownRemaining = 0;

		for (StatusEffect effect : effects) {
			effect.onEffectRemove(this);
		}
		effects.clear();
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
		return (int) xPos + bonusScore;
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
		for (int i = 0; i < pathEntities.length; i++) {
			pathEntities[i].setXPosScreen(xPosScreen - (Reference.PATH_ENTITY_OFFSET * (i + 1)));
		}
	}

	/**
	 * adds the given value to the xVelocity ignoring maximum speed limit
	 * <p>
	 * a negative value will decrease player speed
	 * </p>
	 */
	public void addXVelocityModifier(float xVelocityModifier) {
		this.xVelocityModifier += xVelocityModifier;
	}

	private boolean collide() {
		CollisionType col = world.doesAreaCollideWithWorld(getXPos() - getPlayerSizeRadius(), yPos - getPlayerSizeRadius(), getXPos() + getPlayerSizeRadius(), yPos + getPlayerSizeRadius());
		if (col != CollisionType.NONE) {
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

	public net.brenig.pixelescape.game.player.PlayerPathEntity[] getPathEntities() {
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
	public void renderBackground(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		if (this.isDead()) {
			return;
		}
		movementController.renderBackground(game, renderer, world, delta);
	}

	@Override
	public void render(PixelEscape game, WorldRenderer renderer, GameMode gameMode, float delta) {
		if (this.isDead()) {
			return;
		}

		game.getRenderManager().begin();

		// Draw Background color
		if (immortal % 1F < 0.5F) {
			game.getRenderManager().setColor(0, 0, 0, 1);
		} else {
			game.getRenderManager().setColor(Color.LIGHT_GRAY);
		}
		renderer.renderRect(this.getXPosScreen() - this.getPlayerSize() / 2, this.getYPos() - this.getPlayerSize() / 2, this.getPlayerSize(), this.getPlayerSize());

		for (PlayerPathEntity e : this.getPathEntities()) {
			renderer.renderRect(e.getXPosScreen() - e.getSizeRadius(), e.getYPos() - e.getSizeRadius(), e.getSize(), e.getSize());
		}

		for (StatusEffect effect : effects) {
			effect.render(game, renderer, this, delta);
		}

		movementController.renderForeground(game, renderer, world, delta);
	}

	@Override
	public boolean isDead() {
		return isDead;
	}

	/**
	 * makes player immortal
	 *
	 * @param time time in seconds to remain immortal
	 */
	public void setImmortal(float time) {
		immortal = time;
	}

	/**
	 * sets the currently available ability, amount of uses will increase, should the player already have this ability
	 *
	 * @param ability new ability
	 * @param uses    amount of times the player can use this ability (-1 for unlimited uses)
	 */
	public void addAbility(Ability ability, int uses) {
		if (this.currentAbility == ability) {
			if (uses > 0) {
				this.remainingAbilityUses += uses;
			} else {
				this.remainingAbilityUses = uses;
			}
		} else {
			this.cooldownRemaining = 0;
			this.currentAbility = ability;
			this.remainingAbilityUses = uses;
		}
	}

	public Ability getCurrentAbility() {
		return currentAbility;
	}

	public boolean hasAbility() {
		return currentAbility != null && remainingAbilityUses != 0;
	}

	public void increaseXPos(float x) {
		xPos += x;
	}

	public float getCooldownRemaining() {
		return cooldownRemaining;
	}

	public float getCooldownRemainingScaled() {
		if (currentAbility != null) {
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
	 * used to spawn explosion and other effects as well as reducing lives/showing gameover screen
	 *
	 * @param col   type of collision
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
			e.setColor(world.getScreen().getGame().getGameDebugSettings().getBoolean("PLAYER_EXPLOSION_RED") ? Color.RED : Color.BLACK);
			final float xVel = (x * 2 + (random.nextFloat() - 0.5F)) * 70 + getXVelocity() * 0.4F;
			final float yVel = (y * 2 + (random.nextFloat() - 0.5F)) * 70;
			e.setVelocity(xVel, yVel);
			world.spawnEntity(e);
		}
		//apply screenshake
		//increase effect with higher score
		final float scoreModifier = 1 - 1 / (getXVelocity() * 0.4F);
		final float forceX = 0.5F + random.nextFloat() * 0.5F * scoreModifier;
		final float forceY = 0.5F + random.nextFloat() * 0.5F * scoreModifier;
		//when colliding with Barricades, shake horizontally
		world.getScreen().getWorldRenderer().applyForceToScreen(col.doesCollideHorizontally() ? forceX : 0, col.doesCollideVertically() ? forceY : 0);

		//play sound
		if (world.getScreen().getGame().getGameSettings().isSoundEnabled()) {
			world.getScreen().getGame().getGameAssets().getPlayerCrashedSound().play(world.getScreen().getGame().getGameSettings().getSoundVolume());
		}

		//explode life icon
		if (world.getScreen().getGameMode().getExtraLives() > 0) {
			//We have a live system (and therefor have a lives icon)
			final float lifeX = world.convertScreenToWorldCoordinate(world.getScreen().getGame().getGameSizeX() - 36 * getExtraLives() - 16);
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
		if (getExtraLives() > 0) {
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
	 * DO NOT CALL THIS WITHIN A {@link StatusEffect}!!!
	 * </p>
	 *
	 * @throws java.util.ConcurrentModificationException when access while player is updating status effects
	 */
	public void addEffect(StatusEffect effect) {
		effects.add(effect);
		effect.onEffectAdded(this);
	}

	/**
	 * adds a statuseffect to this player
	 * <p>
	 * also removes existing instances of this effect (uses {@code instanceof} to find these)
	 * </p>
	 * <p>
	 * DO NOT CALL THIS WITHIN A {@link StatusEffect}!!!
	 * </p>
	 *
	 * @throws java.util.ConcurrentModificationException when access while player is updating status effects
	 */
	public void addOrUpdateEffect(StatusEffect effect) {
		Iterator<StatusEffect> effectIterator = effects.iterator();
		Class<? extends StatusEffect> clazz = effect.getClass();
		while (effectIterator.hasNext()) {
			StatusEffect old = effectIterator.next();
			if (clazz.equals(old.getClass())) {
				old.onEffectRemove(this);
				effectIterator.remove();
			}
		}
		addEffect(effect);
	}

	/**
	 * gets a statuseffect if possible
	 * <p>
	 * DO NOT CALL THIS WITHIN A {@link StatusEffect}!!!
	 * </p>
	 *
	 * @return found statuseffect, null if none was found
	 * @throws java.util.ConcurrentModificationException when access while player is updating status effects
	 */
	public StatusEffect tryGetStatusEffect(Class<? extends StatusEffect> clazz) {
		for (StatusEffect effect : effects) {
			if (clazz.equals(effect.getClass())) {
				return effect;
			}
		}
		//noinspection ConstantConditions
		return null;
	}

	/**
	 * additive modifies yVelocityFactor
	 */
	public void addYVelocityFactor(float change) {
		this.yVelocityFactor += change;
	}

	/**
	 * sets yVelocityFactor
	 */
	public void setYVelocityFactor(float yVelocityFactor) {
		this.yVelocityFactor = yVelocityFactor;
	}

	public Collection<StatusEffect> getStatusEffects() {
		return effects;
	}

	/**
	 * adds given value to the players x-velocity
	 */
	public void modifyXVelocity(float xVelocity) {
		this.xVelocity += xVelocity;
	}

	/**
	 * adds given value to the players y-velocity
	 */
	public void modifyYVelocity(float yVelocity) {
		this.yVelocity += yVelocity;
	}

	/**
	 * sets new y- position
	 */
	public void setYPosition(float pos) {
		this.yPos = pos;
	}

	/**
	 * sets new y velocity
	 */
	public void setYVelocity(float velocity) {
		this.yVelocity = velocity;
	}

	/**
	 * sets new x velocity
	 */
	public void setXVelocity(float xVelocity) {
		this.xVelocity = xVelocity;
	}

	public void addBonusScore(int score) {
		bonusScore += score;
	}

	public PlayerMovementController getMovementController() {
		return movementController;
	}

	public int getBonusScore() {
		return bonusScore;
	}
}
