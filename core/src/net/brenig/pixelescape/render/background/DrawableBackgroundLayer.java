package net.brenig.pixelescape.render.background;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import net.brenig.pixelescape.render.WorldRenderer;

/**
 * Responsible for drawing a layer of the game's background
 */
public class DrawableBackgroundLayer extends AbstractBackgroundLayer {

	private int width;
	private float scrollSpeed;
	private Drawable drawable;

	public DrawableBackgroundLayer(float scrollSpeed, Drawable drawable) {
		this.scrollSpeed = scrollSpeed;
		this.drawable = drawable;
		width = (int) drawable.getMinWidth();
	}

	public DrawableBackgroundLayer(int width, float scrollSpeed, Drawable drawable) {
		this.width = width;
		this.scrollSpeed = scrollSpeed;
		this.drawable = drawable;
	}

	@Override
	public void draw(WorldRenderer renderer) {
		renderer.renderDrawable(drawable, getBackgroundWorldStart(renderer, scrollSpeed, width), 0, renderer.getWorld().getWorldWidth(), renderer.getWorld().getWorldHeight());
	}
}
