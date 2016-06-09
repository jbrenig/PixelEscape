package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.player.Item;
import net.brenig.pixelescape.game.player.PlayerMovementController;
import net.brenig.pixelescape.game.player.abliity.Ability;
import net.brenig.pixelescape.game.player.item.ItemLife;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.game.worldgen.special.BarricadeGenerator;
import net.brenig.pixelescape.game.worldgen.special.ItemGenerator;
import net.brenig.pixelescape.lib.FilteredElementProvider;
import net.brenig.pixelescape.lib.Names;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * GameMode information
 */
public enum GameMode {
	CLASSIC("Classic", Names.SCOREBOARD_CLASSIC, "gamemode_classic"),
	ARCADE("Arcade", Names.SCOREBOARD_ARCADE, "gamemode_arcade", true, 2) {
		@Override
		public void registerWorldGenerators(WorldGenerator worldGenerator) {
			super.registerWorldGenerators(worldGenerator);
			worldGenerator.addSpecialGenerator(new ItemGenerator(600, 1000, 800, 1600, ItemGenerator.createDefaultItemList()));
			worldGenerator.addSpecialGenerator(new ItemGenerator(20000, 25000, 30000, 35000, new FilteredElementProvider.SingleElementProvider<Item>(ItemLife.ITEM)));
		}
	},
	SPEED("Speed", Names.SCOREBOARD_SPEED, "gamemode_speed", Reference.MAX_ENTITY_SPEED * 1.2F, Reference.STARTING_SPEED * 2, Reference.SPEED_MODIFIER * 3),
	FLASH("Flash", Names.SCOREBOARD_FLASH, "gamemode_flash", Reference.MAX_ENTITY_SPEED * 1.4F, Reference.STARTING_SPEED * 4, Reference.SPEED_MODIFIER * 4) {
		@Override
		public void registerWorldGenerators(WorldGenerator worldGenerator) {
			worldGenerator.registerDefaultTerrainGenerators();
			worldGenerator.addSpecialGenerator(new BarricadeGenerator((int) (Reference.TARGET_RESOLUTION_X * 0.7F)));
		}

		@Override
		public PlayerMovementController createPlayerMovementController() {
			return new FlashMovementController();
		}
	},
	BLINK("Blink", Names.SCOREBOARD_BLINK, "gamemode_blink", true, Ability.BLINK, -1) {
		@Override
		public void registerWorldGenerators(WorldGenerator worldGenerator) {
			worldGenerator.registerDefaultTerrainGenerators();
			worldGenerator.addSpecialGenerator(new BarricadeGenerator(400));
			worldGenerator.obstacleSizeModifier = 1.5F;
		}
	},
	DRAG("Drag", Names.SCOREBOARD_DRAG, "gamemode_drag") {
		@Override
		public PlayerMovementController createPlayerMovementController() {
			return new DragMovementController();
		}

		@Override
		public void registerWorldGenerators(WorldGenerator worldGenerator) {
			super.registerWorldGenerators(worldGenerator);
			worldGenerator.obstacleSizeModifier = 0.7F;
		}
	};

	private String name;
	private String scoreBoardName;
	private String iconTexture;

	private boolean abilitiesEnabled = false;
	private Ability startingAbility = null;
	private int startingAbilityUses = -1;

	private int extraLives = 0;

	private float maxEntitySpeed = Reference.MAX_ENTITY_SPEED;
	private float startingSpeed = Reference.STARTING_SPEED;
	private float speedIncreaseFactor = Reference.SPEED_MODIFIER;

	GameMode(String name, String scoreBoardName, String iconTexture) {
		this.name = name;
		this.scoreBoardName = scoreBoardName;
		this.iconTexture = iconTexture;
	}

	GameMode(String name, String scoreBoardName, String iconTexture, boolean abilitiesEnabled, Ability startingAbility, int startingAbilityUses,
	         int extraLives, float maxEntitySpeed, float startingSpeed, float  speedIncreaseFactor) {
		this.name = name;
		this.scoreBoardName = scoreBoardName;
		this.iconTexture = iconTexture;
		this.abilitiesEnabled = abilitiesEnabled;
		this.startingAbility = startingAbility;
		this.startingAbilityUses = startingAbilityUses;
		this.extraLives = extraLives;
		this.maxEntitySpeed = maxEntitySpeed;
		this.startingSpeed = startingSpeed;
		this.speedIncreaseFactor = speedIncreaseFactor;
	}

	GameMode(String name, String scoreBoardName, String iconTexture, boolean abilitiesEnabled, int extraLives) {
		this.name = name;
		this.scoreBoardName = scoreBoardName;
		this.iconTexture = iconTexture;
		this.abilitiesEnabled = abilitiesEnabled;
		this.extraLives = extraLives;
	}

	GameMode(String name, String scoreBoardName, String iconTexture, boolean abilitiesEnabled, Ability startingAbility, int startingAbilityUses) {
		this.name = name;
		this.scoreBoardName = scoreBoardName;
		this.iconTexture = iconTexture;
		this.abilitiesEnabled = abilitiesEnabled;
		this.startingAbility = startingAbility;
		this.startingAbilityUses = startingAbilityUses;
	}


	GameMode(String name, String scoreBoardName, String iconTexture, boolean abilitiesEnabled, Ability startingAbility, int startingAbilityUses, int extraLives) {
		this.name = name;
		this.scoreBoardName = scoreBoardName;
		this.iconTexture = iconTexture;
		this.abilitiesEnabled = abilitiesEnabled;
		this.startingAbility = startingAbility;
		this.startingAbilityUses = startingAbilityUses;
		this.extraLives = extraLives;

	}

	GameMode(String name, String scoreBoardName, String iconTexture, float maxEntitySpeed, float startingSpeed, float speedIncreaseFactor) {
		this.name = name;
		this.scoreBoardName = scoreBoardName;
		this.iconTexture = iconTexture;
		this.maxEntitySpeed = maxEntitySpeed;
		this.startingSpeed = startingSpeed;
		this.speedIncreaseFactor = speedIncreaseFactor;
	}


	/**
	 * @return the movement controller used to do the players movement
	 */
	public PlayerMovementController createPlayerMovementController() {
		return new PlayerMovementController.DefaultMovementController();
	}

	/**
	 * finds the texture region for the gamemode icon
	 * @return the icon of this gamemode
	 */
	public TextureRegion createIcon(GameAssets assets) {
		return assets.getTextureAtlas().findRegion(iconTexture);
	}

	/**
	 * @return the name of the scoreboard that should be used
	 */
	public String getScoreboardName() {
		return scoreBoardName;
	}

	/**
	 * @return the name of this gamemode (unlocalized)
	 */
	public String getGameModeName() {
		return name;
	}

	/**
	 * add WorldFeatures to the given worldGenerator
	 */
	public void registerWorldGenerators(WorldGenerator worldGenerator) {
		worldGenerator.registerDefaultWorldGenerators();
	}

	/**
	 * gets called every tick
	 * @return whether barricades should be generated
	 * @param world the world to generate in
	 */
	public boolean shouldGenerateBarricades(World world) {
		return true;
	}

	/**
	 * @return the speed the player has when the game starts
	 */
	public float getStartingSpeed() {
		return startingSpeed;
	}

	/**
	 * @return the speed increase of the player
	 */
	public float getSpeedIncreaseFactor() {
		return speedIncreaseFactor;
	}

	/**
	 * @return maximum speed of entities
	 */
	public float getMaxEntitySpeed() {
		return maxEntitySpeed;
	}

	/**
	 * @return the amount of extra lives the player has when the game starts
	 */
	public int getExtraLives() {
		return extraLives;
	}

	/**
	 * whether abilities are enabled (note: this value must not change!)
	 */
	public boolean abilitiesEnabled() {
		return abilitiesEnabled;
	}

	/**
	 * @return the ability that is available when the game begins (returns null if abilities are disabled)
	 */
	public Ability getStartingAbility() {
		return startingAbility;
	}

	/**
	 * @return the uses the starting ability has left (default -1)
	 */
	public int getStartingAbilityUses() {
		return startingAbilityUses;
	}


	private class FlashMovementController implements PlayerMovementController {

		@Override
		public void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
			if(manager.isTouched()) {
				player.setYPosition(world.convertMouseYToWorldCoordinate(game.getScaledMouseY()));
			}
			player.modifiyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
		}

		@Override
		public void reset(GameMode mode) {

		}

		@Override
		public void render(PixelEscape game, WorldRenderer renderer, World world, float delta) {

		}
	}

	private class DragMovementController implements PlayerMovementController {

		private float acceleration;

		private boolean isTouched;
		private float touchX;
		private float touchY;

		@Override
		public void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
			player.modifiyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
			player.modifiyYVelocity(acceleration * deltaTick);
			if(isTouched) {
				if(!manager.isTouched()) {
					//Confirm
					if(touchX > 0) {
						acceleration = world.convertMouseYToScreenCoordinate(game.getScaledMouseY()) - touchY;
					}
					isTouched = false;
					touchX = Float.MIN_VALUE;
					touchY = Float.MIN_VALUE;
				}
			} else if(manager.isTouched()) {
				touchX = game.getScaledMouseX();
				touchY = world.convertMouseYToScreenCoordinate(game.getScaledMouseY());
				isTouched = true;
			}
		}

		@Override
		public void render(PixelEscape game, WorldRenderer renderer, World world, float delta) {
			renderer.getRenderManager().beginFilledShape();
			if(isTouched && touchX > 0) {
				Color color = world.convertMouseYToScreenCoordinate(game.getScaledMouseY()) < touchY ? Color.RED : Color.BLACK;
				renderer.getRenderManager().getShapeRenderer().line(touchX, touchY, game.getScaledMouseX(), world.convertMouseYToScreenCoordinate(game.getScaledMouseY()), color, color);
			}
			final float ySize = Reference.PLAYER_ENTITY_SIZE * acceleration / 40;
			renderer.getRenderManager().getShapeRenderer().setColor(Color.GRAY);
			renderer.renderRect(world.player.getXPosScreen() - Reference.PATH_ENTITY_SIZE / 2, world.player.getYPos(), Reference.PATH_ENTITY_SIZE, ySize);
		}

		@Override
		public void reset(GameMode mode) {
			acceleration = 0;
			isTouched = false;
			touchX = Float.MIN_VALUE;
			touchY = Float.MIN_VALUE;
		}
	}

}
