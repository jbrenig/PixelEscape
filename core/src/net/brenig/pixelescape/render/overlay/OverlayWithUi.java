package net.brenig.pixelescape.render.overlay;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.ui.ingame.StageManagerGame;
import net.brenig.pixelescape.screen.GameScreen;

/**
 *
 */
public abstract class OverlayWithUi extends Overlay {

	protected final StageManagerGame stage;

	public OverlayWithUi(GameScreen screen) {
		super(screen);
		stage = new StageManagerGame(screen);
	}

	@Override
	public void render(float delta) {
		screen.game.getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		stage.act(delta);
		stage.draw(screen.game.getRenderManager());
	}

	@Override
	public void show() {
		screen.setOverlayInputProcessor(stage.getInputProcessor());
	}

	@Override
	public void onResize(int width, int height) {
		stage.updateStageToGameBounds(width, height);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public boolean shouldHideGameUI() {
		return true;
	}
}
