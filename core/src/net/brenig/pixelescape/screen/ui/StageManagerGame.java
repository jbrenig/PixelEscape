package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.utils.viewport.ExtendViewport;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Created by Jonas Brenig on 27.10.2015.
 */
public class StageManagerGame extends StageManager {

	private GameScreen screen;

	public StageManagerGame(GameScreen screen) {
		super(new ExtendViewport(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y, screen.game.cam));
		this.screen = screen;
		rootTable.setFillParent(false);
		rootTable.setPosition(0, screen.uiPos);
		rootTable.setSize(screen.world.getWorldWidth(), screen.world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		rootTable.left().top();
	}

	public void updateStageToGameBounds(int width, int height) {
		//reset font size for measuring
		screen.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);

		updateViewport(width, height, true);

		rootTable.setPosition(0, screen.uiPos);
		rootTable.setSize(screen.world.getWorldWidth(), screen.world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		rootTable.invalidateHierarchy();
	}
}
