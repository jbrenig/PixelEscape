package net.brenig.pixelescape.render.ui.general;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;

/**
 * Group that only shows on table at a time.
 * <p/>
 * page can be changed with gestures or programatically
 */
public class SwipeTabbedStack extends Stack {

	private static final float flingVelocity = 100;

	private int currentElement = 0;

	private final static int animationXOffset = 400;
	private final static float animationDuration = 0.2F;

	public SwipeTabbedStack() {
		super();
		addListener(new ActorGestureListener() {
			@Override
			public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
				super.pan(event, x, y, deltaX, deltaY);
			}

			@Override
			public void fling(InputEvent event, float velocityX, float velocityY, int button) {
				super.fling(event, velocityX, velocityY, button);
			}
		});
	}

}
