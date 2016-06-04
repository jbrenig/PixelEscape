package net.brenig.pixelescape.render.ui.general;

import com.badlogic.gdx.scenes.scene2d.ui.Widget;

/**
 * Simple Vertical Spacer
 */
public class VerticalSpacer extends Widget {

	private final float minHeight;
	private final float prefHeight;
	private final float maxHeight;

	public VerticalSpacer() {
		this(0, Float.MAX_VALUE, Float.MAX_VALUE);
	}

	public VerticalSpacer(float minHeight) {
		this(minHeight, Float.MAX_VALUE, Float.MAX_VALUE);
	}

	public VerticalSpacer(float minHeight, float prefHeight, float maxHeight) {
		this.minHeight = minHeight;
		this.prefHeight = prefHeight;
		this.maxHeight = maxHeight;
	}

	@Override
	public float getMinHeight() {
		return minHeight;
	}

	@Override
	public float getMaxHeight() {
		return maxHeight;
	}

	@Override
	public float getPrefHeight() {
		return prefHeight;
	}
}
