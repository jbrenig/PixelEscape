package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Simple Horizontal Spacer
 */
public class HorizontalSpacer extends Widget {

	@Override
	public float getMinWidth() {
		return 0;
	}

	@Override
	public float getMaxWidth() {
		return Float.MAX_VALUE;
	}

	@Override
	public float getPrefWidth() {
		return Float.MAX_VALUE;
	}
}
