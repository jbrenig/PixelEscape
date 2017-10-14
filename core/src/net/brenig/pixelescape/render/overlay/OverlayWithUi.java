package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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
		screen.getGame().getFont().getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		stage.act(delta);
		stage.draw(screen.getGame().getRenderManager());
	}

	@Override
	public void show() {
		screen.setOverlayInputProcessor(stage.getInputProcessor());
	}

	@Override
	public void onResize(int width, int height) {
		stage.updateStageToGameBounds(width, height);
		super.onResize(width, height);
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	@Override
	public boolean shouldHideGameUI() {
		return true;
	}

	protected Skin getSkin() {
		return screen.getGame().getSkin();
	}
}
