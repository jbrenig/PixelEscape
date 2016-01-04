package net.brenig.pixelescape.screen.ui.general;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Simple Horizontal Spacer
 */
public class HorizontalSpacer extends Widget {

	private final float minWidth;
	private final float prefWidth;
	private final float maxWidth;

	public HorizontalSpacer() {
		this(0, Float.MAX_VALUE, Float.MAX_VALUE);
	}

	public HorizontalSpacer(float minWidth) {
		this(minWidth, Float.MAX_VALUE, Float.MAX_VALUE);
	}

	public HorizontalSpacer(float minWidth, float prefWidth, float maxWidth) {
		this.minWidth = minWidth;
		this.prefWidth = prefWidth;
		this.maxWidth = maxWidth;
	}

	@Override
	public float getMinWidth() {
		return minWidth;
	}

	@Override
	public float getMaxWidth() {
		return maxWidth;
	}

	@Override
	public float getPrefWidth() {
		return prefWidth;
	}
}
