package net.brenig.pixelescape.game.data

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.data.constants.ScoreboardNames
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.game.data.constants.Textures
import net.brenig.pixelescape.game.player.Item
import net.brenig.pixelescape.game.player.abliity.Abilities
import net.brenig.pixelescape.game.player.abliity.Ability
import net.brenig.pixelescape.game.player.effects.EffectMove
import net.brenig.pixelescape.game.player.item.ItemLife
import net.brenig.pixelescape.game.player.item.ItemScoreDynamic
import net.brenig.pixelescape.game.player.movement.DefaultMovementController
import net.brenig.pixelescape.game.player.movement.DragMovementController
import net.brenig.pixelescape.game.player.movement.FlashMovementController
import net.brenig.pixelescape.game.player.movement.PlayerMovementController
import net.brenig.pixelescape.game.worldgen.WorldGenerator
import net.brenig.pixelescape.game.worldgen.special.BarricadeGenerator
import net.brenig.pixelescape.game.worldgen.special.ItemGenerator
import net.brenig.pixelescape.lib.FilteredElementProvider
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack

/**
 * GameMode information
 */
enum class GameMode(name: String, scoreBoardName: String, private val iconTexture: String, private val abilitiesEnabled: Boolean = false,
        /**
         * @return the ability that is available when the game begins (returns null if abilities are disabled)
         */
                    val startingAbility: Ability? = null,
        /**
         * @return the uses the starting ability has left (default -1)
         */
                    val startingAbilityUses: Int = -1,
        /**
         * @return the amount of extra lives the player has when the game starts
         */
                    val extraLives: Int = 0,
        /**
         * @return maximum speed of entities
         */
                    val maxEntitySpeed: Float = Reference.MAX_ENTITY_SPEED,
        /**
         * @return the speed the player has when the game starts
         */
                    val startingSpeed: Float = Reference.STARTING_SPEED,
        /**
         * @return the speed increase of the player
         */
                    val speedIncreaseFactor: Float = Reference.SPEED_MODIFIER) {
    CLASSIC("Classic", ScoreboardNames.SCOREBOARD_CLASSIC, "gamemode_classic") {
        override fun createCustomTutorial(skin: Skin, stack: SwipeTabbedStack, contentSizeX: Int, contentSizeY: Int) {
            val maxLabelWidth = contentSizeX - 60
            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)
            run {
                val lbl = Label("Good old classic!", skin)
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }
            stack.add(table)
        }
    },
    ARCADE("Arcade", ScoreboardNames.SCOREBOARD_ARCADE, "gamemode_arcade", true, extraLives = 2) {
        override fun registerWorldGenerators(worldGenerator: WorldGenerator) {
            super.registerWorldGenerators(worldGenerator)
            worldGenerator.addSpecialGenerator(ItemGenerator(600, 1000, 800, 1600, ItemGenerator.createDefaultItemList()))
            worldGenerator.addSpecialGenerator(ItemGenerator(20000, 25000, 30000, 35000, FilteredElementProvider.SingleElementProvider<Item>(ItemLife.ITEM)))
            worldGenerator.addSpecialGenerator(ItemGenerator(25000, 30000, 50000, 60000, FilteredElementProvider.SingleElementProvider(EffectMove.ITEM)))
            worldGenerator.addSpecialGenerator(ItemGenerator(25000, 50000, 60000, 70000, FilteredElementProvider.SingleElementProvider<Item>(ItemScoreDynamic.ITEM)))
        }

        override fun itemsEnabled(): Boolean {
            return true
        }

        override fun createCustomTutorial(skin: Skin, stack: SwipeTabbedStack, contentSizeX: Int, contentSizeY: Int) {
            val maxLabelWidth = contentSizeX - 60
            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)
            run {
                val lbl = Label("Like Classic, but cooler!", skin, StyleNames.LABEL_WHITE)
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.color = Color.PURPLE
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }
            stack.add(table)
        }
    },
    SPEED("Speed", ScoreboardNames.SCOREBOARD_SPEED, "gamemode_speed",
            maxEntitySpeed = Reference.MAX_ENTITY_SPEED * 1.2f,
            startingSpeed = Reference.STARTING_SPEED * 2,
            speedIncreaseFactor = Reference.SPEED_MODIFIER * 3) {
        override fun createCustomTutorial(skin: Skin, stack: SwipeTabbedStack, contentSizeX: Int, contentSizeY: Int) {
            val maxLabelWidth = contentSizeX - 60
            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)
            run {
                val lbl = Label("Watch out! It will get fast!", skin, StyleNames.LABEL_WHITE)
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.color = Color.ORANGE
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }
            stack.add(table)
        }
    },
    FLASH("Flash", ScoreboardNames.SCOREBOARD_FLASH, "gamemode_flash",
            maxEntitySpeed = Reference.MAX_ENTITY_SPEED * 1.4f,
            startingSpeed = Reference.STARTING_SPEED * 4,
            speedIncreaseFactor = Reference.SPEED_MODIFIER * 4) {
        override fun registerWorldGenerators(worldGenerator: WorldGenerator) {
            worldGenerator.registerDefaultTerrainGenerators()
            worldGenerator.addSpecialGenerator(BarricadeGenerator((Reference.TARGET_RESOLUTION_X * 0.7f).toInt()))
        }

        override fun createPlayerMovementController(): PlayerMovementController {
            return FlashMovementController()
        }

        override fun createCustomTutorial(skin: Skin, stack: SwipeTabbedStack, contentSizeX: Int, contentSizeY: Int) {
            val maxLabelWidth = contentSizeX - 60
            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)
            run {
                val lbl = Label("Ready for a challenge?", skin, StyleNames.LABEL_WHITE)
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.color = Color.NAVY
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }
            table.row()
            run {
                val lbl = Label("You'll need to react quickly!", skin, StyleNames.LABEL_WHITE)
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.color = Color.NAVY
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }
            stack.add(table)
        }
    },
    BLINK("Blink", ScoreboardNames.SCOREBOARD_BLINK, "gamemode_blink", true, Abilities.BLINK_LONG_COOLDOWN, -1) {
        override fun registerWorldGenerators(worldGenerator: WorldGenerator) {
            worldGenerator.registerDefaultTerrainGenerators()
            worldGenerator.addSpecialGenerator(BarricadeGenerator(400))
            worldGenerator.obstacleSizeModifier = 1.5f
        }

        override fun createCustomTutorial(skin: Skin, stack: SwipeTabbedStack, contentSizeX: Int, contentSizeY: Int) {
            val maxLabelWidth = contentSizeX - 60
            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)
            run {
                val lbl = Label("Like teleporting? Here you go!", skin, StyleNames.LABEL_WHITE)
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.color = Color.NAVY
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }
            stack.add(table)
        }
    },
    DRAG("Drag", ScoreboardNames.SCOREBOARD_DRAG, "gamemode_drag") {
        override fun createPlayerMovementController(): PlayerMovementController {
            return DragMovementController()
        }

        override fun registerWorldGenerators(worldGenerator: WorldGenerator) {
            super.registerWorldGenerators(worldGenerator)
            worldGenerator.obstacleSizeModifier = 0.7f
        }

        override fun createCustomTutorial(skin: Skin, stack: SwipeTabbedStack, contentSizeX: Int, contentSizeY: Int) {
            val maxLabelWidth = contentSizeX - 60
            val table = Table(skin)
            table.setBackground(Textures.BUTTON_UP)
            table.defaults().padBottom(20f)
            run {
                val lbl = Label("Just drag along!", skin, StyleNames.LABEL_WHITE)
                lbl.setWrap(true)
                lbl.setAlignment(Align.center)
                lbl.color = Color.FIREBRICK
                lbl.pack()

                table.add(lbl).width(maxLabelWidth.toFloat()).center()
            }
            stack.add(table)
        }
    };

    /**
     * @return the name of this gamemode (unlocalized)
     */
    val gameModeName: String = name

    /**
     * @return the name of the scoreboard that should be used
     */
    val scoreboardName: String = scoreBoardName


    /**
     * @return the movement controller used to do the players movement
     */
    open fun createPlayerMovementController(): PlayerMovementController {
        return DefaultMovementController()
    }

    /**
     * finds the texture region for the gamemode icon
     *
     * @return the icon of this gamemode
     */
    fun createIcon(assets: GameAssets): TextureRegion {
        return assets.textureAtlas.findRegion(iconTexture)
    }

    /**
     * add WorldFeatures to the given worldGenerator
     */
    open fun registerWorldGenerators(worldGenerator: WorldGenerator) {
        worldGenerator.registerDefaultWorldGenerators()
    }

    /**
     * gets called every tick
     *
     * @param world the world to generate in
     * @return whether barricades should be generated
     */
    fun shouldGenerateBarricades(world: World): Boolean {
        return true
    }

    /**
     * whether abilities are enabled (note: this value must not change!)
     */
    fun abilitiesEnabled(): Boolean {
        return abilitiesEnabled
    }

    /**
     * @return whether items are enabled (used for tutorial)
     */
    open fun itemsEnabled(): Boolean {
        return false
    }

    /**
     * ability for gamemodes to add custom tutorial pages
     *
     * @param skin         skin used for ui
     * @param stack        stack the pages should get added to
     * @param contentSizeX target X size for the elements added
     * @param contentSizeY target Y size for the elements added
     */
    open fun createCustomTutorial(skin: Skin, stack: SwipeTabbedStack, contentSizeX: Int, contentSizeY: Int) {}


}
