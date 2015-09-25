package net.brenig.pixelescape.lib;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Created by Jonas Brenig on 21.08.2015.
 */
public class Utils {

	public static void updateTableToGameBounds(Table table, World world, int uiPos) {
		table.setPosition(0, uiPos);
		table.setSize(world.getWorldWidth(), world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		table.invalidateHierarchy();
	}

	/**
	 * Helper function to update gui Elements (eg. onResize)
	 */
	public static void updateUIElementsToScreen(GameScreen screen, Stage stage, Table table, int width, int height) {
		screen.game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		stage.getViewport().update(width, height, true);
		updateTableToGameBounds(table, screen.world, screen.uiPos);
	}
}
