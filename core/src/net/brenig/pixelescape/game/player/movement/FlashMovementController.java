package net.brenig.pixelescape.game.player.movement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Movement Controller for Flash GameMode
 */
public class FlashMovementController implements PlayerMovementController {

	@Override
	public void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
		if (manager.isTouched()) {
			player.setYPosition(world.convertMouseYToWorldCoordinate(game.getScaledMouseY()));
		}
		player.modifiyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
	}

	@Override
	public void reset(GameMode mode) {

	}

	@Override
	public void renderBackground(PixelEscape game, WorldRenderer renderer, World world, float delta) {
		if (world.getScreen().getInput().isTouched()) {
			renderer.getRenderManager().begin();
			renderer.getRenderManager().setColor(Color.CYAN);
			renderer.renderRect(world.player.getXPosScreen(), world.player.getYPos() - Reference.PATH_ENTITY_SIZE / 2, game.getScaledMouseX() - world.player.getXPosScreen(), Reference.PATH_ENTITY_SIZE);
			renderer.getRenderManager().setColor(Color.GRAY);
			renderer.renderRect(game.getScaledMouseX() - Reference.PLAYER_ENTITY_SIZE / 2, world.player.getYPos() - Reference.PLAYER_ENTITY_SIZE / 2, Reference.PLAYER_ENTITY_SIZE, Reference.PLAYER_ENTITY_SIZE);
		}
	}

	@Override
	public void renderForeground(PixelEscape game, WorldRenderer renderer, World world, float delta) {

	}

	@Override
	public Table createTutorialWindow(Skin skin) {
		Table table = new Table();
		return table;
	}
}
