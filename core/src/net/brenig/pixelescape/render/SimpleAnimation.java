package net.brenig.pixelescape.render;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * simple animation using {@link com.badlogic.gdx.graphics.g2d.Animation}
 */
public class SimpleAnimation {

	private float frameTime = 0;
	private Animation animation;

	public SimpleAnimation(final int cols, final int rows, final TextureRegion texture, final float frameTime) {
		this(cols, rows, texture, frameTime, Animation.PlayMode.LOOP);
	}

	public SimpleAnimation(final int cols, final int rows, final TextureRegion texture, final float frameTime, Animation.PlayMode playMode) {
		animation = createAnimationFromTexture(cols, rows, texture, frameTime);
		animation.setPlayMode(playMode);
	}

	/**
	 * renders the animation
	 *
	 * @param renderer renderer instance
	 * @param xPos     the x-position on screen
	 * @param yPos     the y-position on screen
	 * @param width    width of the animation
	 * @param height   height of the animation
	 * @param delta    time since last frame
	 */
	public void render(GameRenderManager renderer, float xPos, float yPos, float width, float height, float delta) {
		frameTime += delta;
		renderer.begin();
		renderer.draw(animation.getKeyFrame(frameTime), xPos, yPos, width, height);
	}

	/**
	 * renders the animation
	 *
	 * @param renderer renderer instance
	 * @param xPos     the x-position on screen
	 * @param yPos     the y-position on screen
	 * @param delta    time since last frame
	 */
	public void render(GameRenderManager renderer, float xPos, float yPos, float delta) {
		frameTime += delta;
		renderer.begin();
		renderer.draw(animation.getKeyFrame(frameTime), xPos, yPos);
	}

	/**
	 * increments internal frame timer and returns current frame
	 */
	public TextureRegion getFrameAfterTimePassed(float delta) {
		frameTime += delta;
		return animation.getKeyFrame(frameTime);
	}

	public Animation getAnimation() {
		return animation;
	}

	/**
	 * creates an {@link Animation} from the given texture
	 */
	public static Animation createAnimationFromTexture(final int cols, final int rows, final TextureRegion texture, final float frameTime) {
		TextureRegion[][] tmp = texture.split(texture.getRegionWidth() / cols, texture.getRegionHeight() / rows);
		TextureRegion[] frames = new TextureRegion[cols * rows];
		int index = 0;
		for (int y = 0; y < rows; y++) {
			for (int x = 0; x < cols; x++) {
				frames[index++] = tmp[y][x];
			}
		}
		return new Animation(frameTime, frames);
	}
}
