package net.brenig.pixelescape.render.overlay;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Overlay that is displayed at the first start of any gamemode explaining controls and features to the player
 */
public class TutorialOverlay extends OverlayWithUi {

	public TutorialOverlay(GameScreen screen) {
		super(screen);


	}


	@Override
	public void renderFirst(float delta) {
		//noinspection PointlessBooleanExpression,ConstantConditions
		if(Reference.SCREEN_TINT_STRENGTH > 0) {
			renderScreenTint(Reference.SCREEN_TINT_STRENGTH);
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);
	}
}
