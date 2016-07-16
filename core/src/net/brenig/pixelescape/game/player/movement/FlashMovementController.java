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
 * Movement Controller for Flash GameMode
 */
public class FlashMovementController implements PlayerMovementController {

	@Override
	public void updatePlayerMovement(PixelEscape game, InputManager manager, GameMode gameMode, World world, EntityPlayer player, float deltaTick, float yVelocityFactor) {
		if (manager.isTouched()) {
			player.setYPosition(world.convertMouseYToWorldCoordinate(game.getScaledMouseY()));
		}
		player.modifyXVelocity(gameMode.getSpeedIncreaseFactor() * deltaTick);
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
	public Table createTutorialWindow(Skin skin, GameScreen screen, int maxWidth, int maxHeight) {
		final int maxLabelWidth = maxWidth - 60;

		Table table = new Table(skin);
		table.setBackground(Textures.BUTTON_UP);
		table.defaults().padBottom(20);

		Label lbl = new Label("Touch the screen to move up or down.", skin, StyleNames.LABEL_WHITE);
		lbl.setColor(Color.BLUE);
		lbl.setWrap(true);
		lbl.pack();

		Label lbl2_1 = new Label("You will always be at the position of your finger", skin, StyleNames.LABEL_WHITE);
		lbl2_1.setWrap(true);
		lbl2_1.setColor(Color.BLUE);
		lbl2_1.pack();

		table.add(lbl).center().width(maxLabelWidth);
		table.row();
		table.add(lbl2_1).center().width(maxLabelWidth);
		return table;
	}
}
