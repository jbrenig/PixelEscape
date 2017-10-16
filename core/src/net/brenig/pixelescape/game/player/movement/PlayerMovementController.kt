package net.brenig.pixelescape.game.player.movement

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.render.WorldRenderer
import net.brenig.pixelescape.screen.GameScreen

/**
 * Handles player input and applies it to the Player entity
 */
interface PlayerMovementController {

    /**
     * update player movement (x and y)
     *
     * @param game            Game instance
     * @param manager         inputmanager instance
     * @param gameMode        current GameMode
     * @param world           Current world
     * @param player          the player
     * @param deltaTick       time since last frame
     * @param yVelocityFactor players y-velocity factor (y-movement might be amplified)
     */
    fun updatePlayerMovement(game: PixelEscape, manager: InputManager, gameMode: GameMode, world: World, player: EntityPlayer, deltaTick: Float, yVelocityFactor: Float)

    /**
     * reset the player input controller
     *
     *
     * gets called when player respawns
     *
     * @param mode current gamemode
     * @see EntityPlayer.reset
     */
    fun reset(mode: GameMode)

    /**
     * render effects in the background
     *
     *
     *
     * @param game     game instance
     * @param renderer current renderer
     * @param world    current world
     * @param delta    time passed since last frame
     * @see EntityPlayer.renderBackground
     */
    fun renderBackground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float)

    fun renderForeground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float)

    fun createTutorialWindow(skin: Skin, screen: GameScreen, maxWidth: Int, maxHeight: Int): Table
}
