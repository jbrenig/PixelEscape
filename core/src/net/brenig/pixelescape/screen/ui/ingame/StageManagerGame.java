package net.brenig.pixelescape.screen.ui.ingame;

import com.badlogic.gdx.scenes.scene2d.ui.Table;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;
import net.brenig.pixelescape.screen.ui.general.StageManager;

/**
 * StageManager that manages ui elements for the GameScreen<br></br>
 * escpecially useful in Overlays
 */
public class StageManagerGame extends StageManager {

	private final GameScreen screen;

	public StageManagerGame(GameScreen screen) {
		super(screen.game.getRenderManager());
		this.screen = screen;
		rootTable.setFillParent(false);
		rootTable.setPosition(0, screen.getUiPos());
		rootTable.setSize(screen.world.getWorldWidth(), screen.world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		rootTable.left().top();
	}

	public void updateStageToGameBounds(int width, int height) {
		//reset font size for measuring
		screen.game.getRenderManager().resetFontSizeToDefaultGuiSize();

		updateViewport(width, height, true);

		rootTable.setPosition(0, screen.getUiPos());
		rootTable.setSize(screen.world.getWorldWidth(), screen.world.getWorldHeight() + Reference.GAME_UI_Y_SIZE);
		rootTable.invalidateHierarchy();
	}

	/**
	 * creates a new table that should be used for the main menu bar and adds it to the stage
	 * @return the table created
	 */
	public Table createHeadUiLayoutTable() {
		Table table = new Table();
		table.defaults().height(screen.getUiSize() - 2 * screen.getUiPadding()).fillY();
		table.pad(screen.getUiPadding(), screen.getUiPadding(), screen.getUiPadding(), screen.getUiPadding()); //top, left, bottom, right
		table.top().left();
		add(table).height(screen.getUiSize()).fillY();
		return table;
	}

	/**
	 * creates a new table that should be used for the main content and adds it to the stage
	 * <p/>
	 * this should be created AFTER the head layout menu bar was added
	 * @return the table created
	 */
	public Table createContentUiLayoutTable() {
		Table table = new Table();
		table.pad(screen.getUiPadding(), screen.getUiPadding(), screen.getUiPadding(), screen.getUiPadding()); //top, left, bottom, right
		table.top().left();
		row();
		add(table).height(Reference.GAME_RESOLUTION_Y).fill();
		return table;
	}
}
