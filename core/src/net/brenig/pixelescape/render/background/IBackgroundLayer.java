package net.brenig.pixelescape.render.background;

import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Draws somthing in the game's background
 */
public interface IBackgroundLayer {

	void draw(WorldRenderer renderer);

	/**
	 * @return where a texture would need to be rendered to be {@code ratio} time slower than the foreground
	 */
	default float getBackgroundWorldStart(WorldRenderer renderer, float ratio, int textureSize) {
		return renderer.getWorldCameraXPos() + ((renderer.getWorldCameraXPos() * ratio) % textureSize);
	}

}
