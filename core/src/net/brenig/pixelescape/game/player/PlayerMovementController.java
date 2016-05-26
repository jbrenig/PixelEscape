package net.brenig.pixelescape.game.player;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.lib.Reference;

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

	void reset();

	class DefaultMovementController implements PlayerMovementController {
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
		public void reset() {
			lastTouched = false;
		}
	}
}
