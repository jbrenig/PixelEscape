package net.brenig.pixelescape.render.ui.general;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
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
	private int currentOffsetX = 0;

	private int panXOffset = 0;
	private final static int panXPadding = 20;

	private final static int animationXOffset = 400;
	private final static float animationDuration = 0.2F;

	private boolean cycle = true;

	private IOverSwipeListener overSwipeListener;

	public SwipeTabbedStack() {
		super();
		this.setTouchable(Touchable.enabled);
		addListener(new ActorGestureListener() {
			@Override
			public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
				int oldOffsetX = currentOffsetX;
				currentOffsetX += deltaX;
				Actor actor = getChildren().get(currentElement);
				actor.setPosition(currentOffsetX, actor.getY());
				actor.getColor().a = 1 - Math.min(1, Math.abs(currentOffsetX) / (float) animationXOffset);

				//remove old element
				if(oldOffsetX <= 0 && currentOffsetX > 0) {
					if(hasNextElement()) {
						final int nextElement = (currentElement + 1) % getChildren().size;
						final Actor old = getChildren().get(nextElement);
						old.setVisible(false);
					}
				} else if(oldOffsetX > 0 && currentOffsetX <= 0) {
					if(hasLastElement()) {
						final int nextElement = (currentElement - 1 + getChildren().size) % getChildren().size;
						final Actor old = getChildren().get(nextElement);
						old.setVisible(false);
					}
				}
				if(currentOffsetX <= 0) {
					if(cycle || hasNextElement()) {
						final Actor next = setupNextElement();
						final float pos = currentOffsetX + (getWidth() / 2F) + panXPadding;
						next.setPosition(pos, next.getY());
						next.getColor().a = 1 - Math.min(1, Math.abs(pos) / animationXOffset);
					}
				} else {
					if(cycle || hasLastElement()) {
						final Actor next = setupLastElement();
						final float pos = currentOffsetX - (getWidth() / 2F) - panXPadding;
						next.setPosition(pos, next.getY());
						next.getColor().a = 1 - Math.min(1, Math.abs(pos) / animationXOffset);
					}
				}
			}

			@Override
			public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
				if(currentOffsetX > panXOffset) {
					if(cycle || hasLastElement()) {
						//swipe to last
						last();
					} else {
						resetPositions();
					}
					checkBeforeFirst();
				} else if(currentOffsetX < panXOffset * -1) {
					if(cycle || hasNextElement()) {
						//swipe to next
						next();
					} else {
						resetPositions();
					}
					checkAfterLast();
				} else {
					resetPositions();
				}
				currentOffsetX = 0;
			}

			@Override
			public void fling(InputEvent event, float velocityX, float velocityY, int button) {
				if(velocityX > flingVelocity  && (cycle || hasLastElement())) {
					last();
					currentOffsetX = 0;
				} else if(velocityX * -1 > flingVelocity && (cycle || hasNextElement())) {
					next();
					currentOffsetX = 0;
				}
			}
		});
	}

	public void swipeOut(boolean direction) {
		final Actor old = getChildren().get(currentElement);
		old.clearActions();
		if(direction) {
			old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));
		} else {
			old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(-animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));
		}
	}

	/**
	 * resets actor positions after eg. panning
	 */
	private void resetPositions() {
		//reset positions
		Actor actor = getChildren().get(currentElement);
		actor.addAction(Actions.parallel(Actions.moveTo(0, 0, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)));

		if(currentOffsetX <= 0 && (cycle || hasNextElement())) {
			final Actor next = setupNextElement();
			next.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));
		} else if((cycle || hasLastElement())) {
			final Actor next = setupLastElement();
			next.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(-animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));
		}
	}

	private void checkAfterLast() {
		if(overSwipeListener != null && !hasNextElement()) {
			overSwipeListener.onAfterLast();
		}
	}

	private void checkBeforeFirst() {
		if(overSwipeListener != null && !hasLastElement()) {
			overSwipeListener.onBeforeFirst();
		}
	}

	@Override
	public void layout() {
		super.layout();
		panXOffset = (int) (getWidth() / 6F);
	}

	@Override
	public void add(Actor actor) {
		if(getChildren().size >= 1) {
			actor.setVisible(false);
		}
		super.add(actor);
	}

	private Actor setupNextElement() {
		final int nextElement = (currentElement + 1) % getChildren().size;
		final Actor next = getChildren().get(nextElement);
		if(!next.isVisible()) {
			next.getColor().a = 0;
			next.setVisible(true);
			next.setPosition(animationXOffset, 0);
			next.clearActions();
		}
		return next;
	}

	private Actor setupLastElement() {
		final int nextElement = (currentElement - 1 + getChildren().size) % getChildren().size;
		final Actor next = getChildren().get(nextElement);
		if(!next.isVisible()) {
			next.getColor().a = 0;
			next.setVisible(true);
			next.setPosition(-animationXOffset, 0);
			next.clearActions();
		}
		return next;
	}

	/**
	 * cycles to the next element
	 */
	public void next() {
		if(cycle || hasNextElement()) {
			final int nextElement = (currentElement + 1) % getChildren().size;
			final Actor next = setupNextElement();
			next.addAction(Actions.parallel(Actions.moveTo(0, 0, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)));

			final Actor old = getChildren().get(currentElement);
			old.clearActions();
			old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(-animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));

			currentElement = nextElement;
		}
		checkAfterLast();
	}

	/**
	 * cycles to the last element
	 */
	public void last() {
		if(cycle || hasLastElement()) {
			final int nextElement = (currentElement - 1 + getChildren().size) % getChildren().size;
			final Actor next = setupLastElement();
			next.addAction(Actions.parallel(Actions.moveTo(0, 0, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)));

			final Actor old = getChildren().get(currentElement);
			old.clearActions();
			old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));

			currentElement = nextElement;
		}
		checkBeforeFirst();
	}

	public void setOverSwipeListener(IOverSwipeListener overSwipeListener) {
		this.overSwipeListener = overSwipeListener;
	}

	/**
	 * @return true if the currently displayed element is not the first
	 */
	public boolean hasLastElement() {
		return currentElement > 0;
	}

	/**
	 * @return true if the currently displayed element is not the last
	 */
	public boolean hasNextElement() {
		return currentElement < getChildren().size - 1;
	}

	public void setCycle(boolean cycle) {
		this.cycle = cycle;
	}

	/**
	 * gets notified when user continues after the last element (or the first when scrolling backwards)
	 */
	public interface IOverSwipeListener {

		void onAfterLast();

		void onBeforeFirst();
	}
}
