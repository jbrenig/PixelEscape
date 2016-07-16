package net.brenig.pixelescape.game.player.movement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.InputManager;
import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.game.data.constants.StyleNames;
import net.brenig.pixelescape.game.data.constants.Textures;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.WorldRenderer;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Movement Controller for Drag GameMode
 */
public class DragMovementController implements PlayerMovementController {

	private float acceleration;

	private boolean isTouched;
	private float touchX;
	private float touchY;

	private static final int DEAD_ZONE = 4;

	@Override
	public void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
		player.modifyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
		player.modifyYVelocity(acceleration * deltaTick);
		if (isTouched) {
			if (!manager.isTouched()) {
				//Confirm
				if (touchX > 0) {
					acceleration = world.convertMouseYToScreenCoordinate(game.getScaledMouseY()) - touchY;
					if (acceleration > 0) {
						acceleration = Math.max(0, acceleration - DEAD_ZONE);
					} else {
						acceleration = Math.min(0, acceleration + DEAD_ZONE);
					}
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
		renderer.getRenderManager().begin();
		final float ySize = Reference.PLAYER_ENTITY_SIZE * acceleration / 40;
		renderer.getRenderManager().setColor(Color.GRAY);
		renderer.renderRect(world.player.getXPosScreen() - Reference.PATH_ENTITY_SIZE / 2, world.player.getYPos() + (ySize > 0 ? Reference.PLAYER_ENTITY_SIZE / 2 : -Reference.PLAYER_ENTITY_SIZE / 2), Reference.PATH_ENTITY_SIZE, ySize);

	}

	@Override
	public void renderForeground(PixelEscape game, WorldRenderer renderer, World world, float delta) {
		renderer.getRenderManager().begin();
		if (isTouched && touchX > 0) {
			Color color = world.convertMouseYToScreenCoordinate(game.getScaledMouseY()) < touchY ? Color.RED : Color.BLACK;
			renderer.getRenderManager().line(touchX, touchY, game.getScaledMouseX(), world.convertMouseYToScreenCoordinate(game.getScaledMouseY()), 2, color);
		}
	}

	@Override
	public void reset(GameMode mode) {
		acceleration = 0;
		isTouched = false;
		touchX = Float.MIN_VALUE;
		touchY = Float.MIN_VALUE;
	}

	@Override
	public Table createTutorialWindow(Skin skin, GameScreen screen, int maxWidth, int maxHeight) {
		final int maxLabelWidth = maxWidth - 60;
		Table table = new Table(skin);
		table.setBackground(Textures.BUTTON_UP);
		table.defaults().padBottom(20);

		Label lbl = new Label("Drag across the screen to change your height.", skin, StyleNames.LABEL_WHITE);
		lbl.setWrap(true);
		lbl.setColor(Color.GREEN);
		lbl.pack();

		Label lbl2_1 = new Label("Be careful, you need to react fast!", skin, StyleNames.LABEL_WHITE);
		lbl2_1.setWrap(true);
		lbl2_1.setColor(Color.GREEN);
		lbl2_1.pack();

		table.add(lbl).center().width(maxLabelWidth);
		table.row();
		table.add(lbl2_1).center().width(maxLabelWidth);
		return table;
	}
}
