package net.brenig.pixelescape.game.player.movement;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.render.WorldRenderer;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Handles player input and applies it to the Player entity
 */
public interface PlayerMovementController {

	/**
	 * update player movement (x and y)
	 * @param game Game instance
	 * @param manager inputmanager instnace
	 * @param gameMode current GameMode
	 * @param world Current world
	 * @param player the player
	 * @param deltaTick time since last frame
	 * @param yVelocityFactor players y-velocity factor (y-movement might be amplified)
	 */
	void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor);

	/**
	 * reset the player input controller
	 * <p/>
	 * gets called when player respawns
	 * @see EntityPlayer#reset(GameMode)
	 *
	 * @param mode current gamemode
	 */
	void reset(GameMode mode);

	/**
	 * render effects in the background
	 * <p/>
	 * @see EntityPlayer#renderBackground(PixelEscape, WorldRenderer, GameMode, float)
	 * 
	 * @param game game instance
	 * @param renderer current renderer
	 * @param world current world
	 * @param delta time passed since last frame
	 */
	void renderBackground(PixelEscape game, WorldRenderer renderer, World world, float delta);

	void renderForeground(PixelEscape game, WorldRenderer renderer, World world, float delta);

	Table createTutorialWindow(Skin skin, GameScreen screen, int maxWidth, int maxHeight);
}
