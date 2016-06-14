package net.brenig.pixelescape.game.player.movement;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * default implementation of a {@link PlayerMovementController}, standart behaviour
 *
 * @see net.brenig.pixelescape.game.gamemode.GameModeClassic
 */
public class DefaultMovementController implements PlayerMovementController {
	private boolean lastTouched = false;

	@Override
	public void updatePlayerMovement(PixelEscape game, InputManager inputManager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
		if (inputManager.isTouched() || inputManager.isSpaceDown()) {
			if(!lastTouched) {
				player.modifiyYVelocity(Reference.CLICK_ACCELERATION * yVelocityFactor);
				lastTouched = true;
			} else {
				player.modifiyYVelocity(Reference.TOUCH_ACCELERATION * deltaTick * yVelocityFactor);
				lastTouched = true;
			}
		} else {
			player.modifiyYVelocity(Reference.GRAVITY_ACCELERATION * deltaTick * yVelocityFactor);
			lastTouched = false;
		}
		player.modifiyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
	}

	@Override
	public void reset(GameMode mode) {
		lastTouched = false;
	}

	@Override
	public void renderBackground(PixelEscape game, WorldRenderer renderer, World world, float delta) {

	}

	@Override
	public void renderForeground(PixelEscape game, WorldRenderer renderer, World world, float delta) {

	}
}
