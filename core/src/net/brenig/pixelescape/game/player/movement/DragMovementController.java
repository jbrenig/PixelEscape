package net.brenig.pixelescape.game.player.movement;

import com.badlogic.gdx.graphics.Color;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Movement Controller for Drag GameMode
 */
public class DragMovementController implements PlayerMovementController {

	private float acceleration;

	private boolean isTouched;
	private float touchX;
	private float touchY;

	@Override
	public void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
		player.modifiyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
		player.modifiyYVelocity(acceleration * deltaTick);
		if (isTouched) {
			if (!manager.isTouched()) {
				//Confirm
				if (touchX > 0) {
					acceleration = world.convertMouseYToScreenCoordinate(game.getScaledMouseY()) - touchY;
				}
				isTouched = false;
				touchX = Float.MIN_VALUE;
				touchY = Float.MIN_VALUE;
			}
		} else if (manager.isTouched()) {
			touchX = game.getScaledMouseX();
			touchY = world.convertMouseYToScreenCoordinate(game.getScaledMouseY());
			isTouched = true;
		}
	}

	@Override
	public void renderBackground(PixelEscape game, WorldRenderer renderer, World world, float delta) {
		renderer.getRenderManager().beginFilledShape();
		if (isTouched && touchX > 0) {
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
