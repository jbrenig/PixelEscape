package net.brenig.pixelescape.render.ui.general;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.BaseDrawable;
import net.brenig.pixelescape.render.SimpleAnimation;

/**
 *
 */
public class AnimationDrawable extends BaseDrawable {

	private float frameTime = 0;
	private Animation<TextureRegion> animation;

	public AnimationDrawable(SimpleAnimation animation) {
		super();
		this.animation = animation.getAnimation();
	}

	public AnimationDrawable(Animation<TextureRegion> animation) {
		super();
		this.animation = animation;
	}

	@Override
	public void draw(Batch batch, float x, float y, float width, float height) {
		frameTime += Gdx.graphics.getDeltaTime();
		batch.draw(animation.getKeyFrame(frameTime), x, y, width, height);
	}
}
