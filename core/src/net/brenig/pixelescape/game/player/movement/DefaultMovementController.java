package net.brenig.pixelescape.game.player.movement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
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
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;

/**
 * default implementation of a {@link PlayerMovementController}, standart behaviour
 *
 * @see GameMode#CLASSIC
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

	@Override
	public Table createTutorialWindow(Skin skin) {
		Table table = new Table(skin);
		table.setBackground(Textures.BUTTON_UP);
		table.defaults().padBottom(20);

		Label lbl = new Label("Touch the screen to move up.", skin, StyleNames.LABEL_WHITE);
		lbl.setColor(Color.GREEN);
		Label lbl2_1 = new Label("Gravity will make you", skin, StyleNames.LABEL_WHITE);
		lbl2_1.setColor(Color.RED);

		HorizontalGroup crashLine = new HorizontalGroup();

		Label lbl2_2 = new Label("CRASH", skin, StyleNames.LABEL_WHITE);
		lbl2_2.setColor(Color.RED);
		lbl2_2.setFontScale(1.2F);

		Label lbl2_3 = new Label("otherwise!", skin, StyleNames.LABEL_WHITE);
		lbl2_3.setColor(Color.RED);

		crashLine.addActor(lbl2_2);
		crashLine.addActor(new HorizontalSpacer(20, 20, 20));
		crashLine.addActor(lbl2_3);

		table.add(lbl).center();
		table.row();
		table.add(lbl2_1).center();
		table.row();
		table.add(crashLine).center();
		return table;
	}
}
