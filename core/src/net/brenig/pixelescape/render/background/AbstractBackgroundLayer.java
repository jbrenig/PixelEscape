package net.brenig.pixelescape.render.background;

import net.brenig.pixelescape.render.WorldRenderer;

/**
 *
 */
public abstract class AbstractBackgroundLayer implements IBackgroundLayer {
	@Override
	public float getBackgroundWorldStart(WorldRenderer renderer, float ratio, int textureSize) {
		return renderer.getWorldCameraXPos() + ((renderer.getWorldCameraXPos() * ratio) % textureSize);
	}

	@Override
	public void onResize(WorldRenderer renderer) {

	}
}
