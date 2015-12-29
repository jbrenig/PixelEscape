package net.brenig.pixelescape.render.overlay;

import net.brenig.pixelescape.screen.GameScreen;

/**
 * Overlay that renders a simple countdown (from 3)<br></br>
 * pauses the game until countdown is finished
 */
public class CountDownOverlay extends Overlay {

	private static final int COUNT_FROM = 3;
	private static final String GO_TEXT = "GO!";

	private long startedAt = 0;

	public CountDownOverlay(GameScreen screen) {
		super(screen);
	}

	@Override
	public void show() {
		super.show();
		startedAt = System.currentTimeMillis();
	}

	@Override
	public void render(float delta) {
		if (delta > 0.1) {
			//compensate low frame rate
			startedAt += delta * 1000;
		}
		long timePassed = screen.isScreenPaused() ? 0 : System.currentTimeMillis() - startedAt;
		long secondsRemaining = COUNT_FROM - ((timePassed) / 1000);
		if (secondsRemaining <= -1) {
			//end
			screen.resetToEmptyOverlay();
			return;
		}
		screen.game.batch.begin();
		int mod = (int) (timePassed % 1000) + 1; //fraction of the current second
		screen.game.getFont().setColor(0.2F, 0.8F, 0, 1000 / (mod));
		if (mod < 100) {
			float scaleMod = 1 / ((mod) * 10);
			screen.game.getFont().getData().setScale(5 + scaleMod * 2);
		} else if (mod > 900) {
			mod -= 900;
			float scaleMod = 1 / ((mod) * 10);
			screen.game.getFont().getData().setScale(3 + scaleMod);
		} else {
			screen.game.getFont().getData().setScale(5);
		}
		if (secondsRemaining <= 0) {
			screen.getFontLayout().setText(screen.game.getFont(), GO_TEXT);
		} else {
			screen.getFontLayout().setText(screen.game.getFont(), "" + secondsRemaining);
		}

		float xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		float yPos = screen.world.getWorldHeight() / 2 + screen.getFontLayout().height / 2 + screen.getUiPos();

		screen.game.getFont().draw(screen.game.batch, screen.getFontLayout(), xPos, yPos);
		screen.game.batch.end();
	}

	@Override
	public boolean doesPauseGame() {
		return COUNT_FROM - (((int) (System.currentTimeMillis() - startedAt)) / 1000) > 0;
	}

	@Override
	public boolean shouldHideGameUI() {
		return false;
	}

	@Override
	public boolean shouldPauseOnEscape() {
		return true;
	}
}
