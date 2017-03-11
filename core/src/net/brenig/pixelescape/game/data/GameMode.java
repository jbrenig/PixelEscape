package net.brenig.pixelescape.game.data;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.constants.StyleNames;
import net.brenig.pixelescape.game.data.constants.Textures;
import net.brenig.pixelescape.game.player.abliity.Abilities;
import net.brenig.pixelescape.game.player.abliity.Ability;
import net.brenig.pixelescape.game.player.effects.EffectMove;
import net.brenig.pixelescape.game.player.item.ItemLife;
import net.brenig.pixelescape.game.player.item.ItemScoreDynamic;
import net.brenig.pixelescape.game.player.movement.DefaultMovementController;
import net.brenig.pixelescape.game.player.movement.DragMovementController;
import net.brenig.pixelescape.game.player.movement.FlashMovementController;
import net.brenig.pixelescape.game.player.movement.PlayerMovementController;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.game.worldgen.special.BarricadeGenerator;
import net.brenig.pixelescape.game.worldgen.special.ItemGenerator;
import net.brenig.pixelescape.lib.FilteredElementProvider;
import net.brenig.pixelescape.lib.Names;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack;

/**
 * GameMode information
 */
public enum GameMode {
	CLASSIC("Classic", Names.SCOREBOARD_CLASSIC, "gamemode_classic") {
		@Override
		public void createCustomTutorial(Skin skin, SwipeTabbedStack stack, int contentSizeX, int contentSizeY) {
			final int maxLabelWidth = contentSizeX - 60;
			Table table = new Table(skin);
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);
			{
				Label lbl = new Label("Good old classic!", skin);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}
			stack.add(table);
		}
	},
	ARCADE("Arcade", Names.SCOREBOARD_ARCADE, "gamemode_arcade", true, 2) {
		@Override
		public void registerWorldGenerators(WorldGenerator worldGenerator) {
			super.registerWorldGenerators(worldGenerator);
			worldGenerator.addSpecialGenerator(new ItemGenerator(600, 1000, 800, 1600, ItemGenerator.createDefaultItemList()));
			worldGenerator.addSpecialGenerator(new ItemGenerator(20000, 25000, 30000, 35000, new FilteredElementProvider.SingleElementProvider<>(ItemLife.ITEM)));
			worldGenerator.addSpecialGenerator(new ItemGenerator(25000, 30000, 50000, 60000, new FilteredElementProvider.SingleElementProvider<>(EffectMove.ITEM)));
			worldGenerator.addSpecialGenerator(new ItemGenerator(25000, 50000, 60000, 70000, new FilteredElementProvider.SingleElementProvider<>(ItemScoreDynamic.ITEM)));
		}

		@Override
		public boolean itemsEnabled() {
			return true;
		}

		@Override
		public void createCustomTutorial(Skin skin, SwipeTabbedStack stack, int contentSizeX, int contentSizeY) {
			final int maxLabelWidth = contentSizeX - 60;
			Table table = new Table(skin);
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);
			{
				Label lbl = new Label("Like Classic, but cooler!", skin, StyleNames.LABEL_WHITE);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.setColor(Color.PURPLE);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}
			stack.add(table);
		}
	},
	SPEED("Speed", Names.SCOREBOARD_SPEED, "gamemode_speed", Reference.MAX_ENTITY_SPEED * 1.2F, Reference.STARTING_SPEED * 2, Reference.SPEED_MODIFIER * 3) {
		@Override
		public void createCustomTutorial(Skin skin, SwipeTabbedStack stack, int contentSizeX, int contentSizeY) {
			final int maxLabelWidth = contentSizeX - 60;
			Table table = new Table(skin);
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);
			{
				Label lbl = new Label("Watch out! It will get fast!", skin, StyleNames.LABEL_WHITE);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.setColor(Color.ORANGE);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}
			stack.add(table);
		}
	},
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

		@Override
		public void createCustomTutorial(Skin skin, SwipeTabbedStack stack, int contentSizeX, int contentSizeY) {
			final int maxLabelWidth = contentSizeX - 60;
			Table table = new Table(skin);
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);
			{
				Label lbl = new Label("Ready for a challenge?", skin, StyleNames.LABEL_WHITE);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.setColor(Color.NAVY);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}
			table.row();
			{
				Label lbl = new Label("You'll need to react quickly!", skin, StyleNames.LABEL_WHITE);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.setColor(Color.NAVY);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}
			stack.add(table);
		}
	},
	BLINK("Blink", Names.SCOREBOARD_BLINK, "gamemode_blink", true, Abilities.BLINK, -1) {
		@Override
		public void registerWorldGenerators(WorldGenerator worldGenerator) {
			worldGenerator.registerDefaultTerrainGenerators();
			worldGenerator.addSpecialGenerator(new BarricadeGenerator(400));
			worldGenerator.obstacleSizeModifier = 1.5F;
		}

		@Override
		public void createCustomTutorial(Skin skin, SwipeTabbedStack stack, int contentSizeX, int contentSizeY) {
			final int maxLabelWidth = contentSizeX - 60;
			Table table = new Table(skin);
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);
			{
				Label lbl = new Label("Like teleporting? Here you go!", skin, StyleNames.LABEL_WHITE);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.setColor(Color.NAVY);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}
			stack.add(table);
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

		@Override
		public void createCustomTutorial(Skin skin, SwipeTabbedStack stack, int contentSizeX, int contentSizeY) {
			final int maxLabelWidth = contentSizeX - 60;
			Table table = new Table(skin);
			table.setBackground(Textures.BUTTON_UP);
			table.defaults().padBottom(20);
			{
				Label lbl = new Label("Just drag along!", skin, StyleNames.LABEL_WHITE);
				lbl.setWrap(true);
				lbl.setAlignment(Align.center);
				lbl.setColor(Color.FIREBRICK);
				lbl.pack();

				table.add(lbl).width(maxLabelWidth).center();
			}
			stack.add(table);
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
	         int extraLives, float maxEntitySpeed, float startingSpeed, float speedIncreaseFactor) {
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
		return new DefaultMovementController();
	}

	/**
	 * finds the texture region for the gamemode icon
	 *
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
	 *
	 * @param world the world to generate in
	 * @return whether barricades should be generated
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

	/**
	 * @return whether items are enabled (used for tutorial)
	 */
	public boolean itemsEnabled() {
		return false;
	}

	/**
	 * ability for gamemodes to add custom tutorial pages
	 *
	 * @param skin         skin used for ui
	 * @param stack        stack the pages should get added to
	 * @param contentSizeX target X size for the elements added
	 * @param contentSizeY target Y size for the elements added
	 */
	public void createCustomTutorial(Skin skin, SwipeTabbedStack stack, int contentSizeX, int contentSizeY) {
	}


}
