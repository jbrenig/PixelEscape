package net.brenig.pixelescape.render.overlay;

import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Overlay that renders a simple countdown (from 3)<br></br>
 * pauses the game until countdown is finished
 */
public class CountDownOverlay extends Overlay {

	private static final int COUNT_FROM = 3;
	private static final String GO_TEXT = "GO!";
	/**
	 * only used for short countdown
	 */
	private static final String READY_TEXT = "Ready!";

	private boolean isShort;
	private long startedAt = 0;

	public CountDownOverlay(GameScreen screen) {
		super(screen);
		isShort = screen.game.gameSettings.shortCountdownEnabled();
	}

	@Override
	public void show() {
		super.show();
		startedAt = System.currentTimeMillis();
	}

	@Override
	public void render(float delta) {
		final long timePassed = screen.isScreenPaused() ? 0 : (System.currentTimeMillis() - startedAt);
		final long secondsPassed = (timePassed / 1000L);
		final long secondsRemaining = isShort ? 1 - secondsPassed : COUNT_FROM - secondsPassed;
		if (secondsRemaining <= -1L) {
			//end
			screen.resetToEmptyOverlay();
			return;
		}

		int fractionOfCurrentSecond = ((int) (timePassed % 1000L)) + 1; //fraction of the current second
		if(fractionOfCurrentSecond <= 0) {
			LogHelper.error("Unknown error when calculating passed time!!, / by zero");
			LogHelper.error("timePassed: " + timePassed);
			LogHelper.error("secondsPassed: " + secondsPassed);
			LogHelper.error("secondsRemaining: " + secondsRemaining);
			LogHelper.error("fractionOfCurrentSecond: " + fractionOfCurrentSecond);
			//restore time
			startedAt = System.currentTimeMillis();
			fractionOfCurrentSecond = 1;
		}
		float fontScale = 5F;
		if (fractionOfCurrentSecond < 100) {
			fontScale = (2 / (fractionOfCurrentSecond * 10)) + 5;
		} else if (fractionOfCurrentSecond > 900) {
			fontScale = (1 / ((fractionOfCurrentSecond - 900) * 10)) + 3;
		}

		screen.game.getRenderManager().begin();
		screen.game.getFont().setColor(0.2F, 0.8F, 0, 1000 / (fractionOfCurrentSecond));
		screen.game.getFont().getData().setScale(isShort ? fontScale * 0.7F : fontScale);

		if (secondsRemaining <= 0) {
			screen.getFontLayout().setText(screen.game.getFont(), GO_TEXT);
		} else if(!isShort) {
			screen.getFontLayout().setText(screen.game.getFont(), "" + secondsRemaining);
		} else {
			screen.getFontLayout().setText(screen.game.getFont(), READY_TEXT);
		}

		final float xPos = screen.world.getWorldWidth() / 2 - screen.getFontLayout().width / 2;
		final float yPos = screen.world.getWorldHeight() / 2 + screen.getFontLayout().height / 2 + screen.getUiPos();

		screen.game.getFont().draw(screen.game.getBatch(), screen.getFontLayout(), xPos, yPos);
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
