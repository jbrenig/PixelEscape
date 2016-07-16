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
 * page can be changed with gestures or programmatically
 */
public class SwipeTabbedStack extends Stack {

	public static final int DEFAULT_ANIMATION_X_OFFSET = 400;
	public static final float DEFAULT_ANIMATION_DURATION = 0.2F;

	public static final float DEFAULT_FLING_VELOCITY = 800;

	public static final int DEFAULT_PAN_X_PADDING = 400;

	public static final float DEFAULT_X_OFFSET_FACTOR = 0.16F;


	private float flingVelocity = DEFAULT_FLING_VELOCITY;

	private int currentElement = 0;
	private int currentOffsetX = 0;

	private int panXOffset = 0;
	private float panXOffsetFactor = DEFAULT_X_OFFSET_FACTOR;
	private int panXPadding = DEFAULT_PAN_X_PADDING;

	private int animationXOffset = DEFAULT_ANIMATION_X_OFFSET;
	private float animationDuration = DEFAULT_ANIMATION_DURATION;

	private boolean cycle = true;

	private IOverSwipeListener overSwipeListener;
	private IElementChangedListener elementChangedListener;

	private boolean overDrawLeft = true;
	private boolean overDrawRight = true;

	private float slowOverDrawFactor = 0.5F;


	public SwipeTabbedStack() {
		this(true);
	}

	public SwipeTabbedStack(int panXPadding) {
		this(true);
		this.panXPadding = panXPadding;
	}

	public SwipeTabbedStack(boolean touchEnabled) {
		super();
		if(touchEnabled) {
			this.setTouchable(Touchable.enabled);
			addListener(new ActorGestureListener() {
				@Override
				public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
					//move current element
					int oldOffsetX = currentOffsetX;
					currentOffsetX += deltaX;
					Actor actor = getChildren().get(currentElement);

					if(!cycle && ((!overDrawLeft && currentOffsetX > 0 && !hasLastElement()) || (!overDrawRight && currentOffsetX < 0 && !hasNextElement()))) {
						actor.setPosition(currentOffsetX * slowOverDrawFactor, actor.getY());
						actor.getColor().a = 1;
					} else {
						actor.setPosition(currentOffsetX, actor.getY());
						actor.getColor().a = 1 - Math.min(1, Math.abs(currentOffsetX) / (float) animationXOffset);
					}

					//remove old element
					if (oldOffsetX <= 0 && currentOffsetX > 0) {
						if (hasNextElement()) {
							final int nextElement = (currentElement + 1) % getChildren().size;
							final Actor old = getChildren().get(nextElement);
							old.setVisible(false);
						}
					} else if (oldOffsetX > 0 && currentOffsetX <= 0) {
						if (hasLastElement()) {
							final int nextElement = (currentElement - 1 + getChildren().size) % getChildren().size;
							final Actor old = getChildren().get(nextElement);
							old.setVisible(false);
						}
					}
					//get next element
					if (currentOffsetX <= 0) {
						if (cycle || hasNextElement()) {
							final Actor next = setupNextElement();
							final float pos = currentOffsetX + (getWidth()) + panXPadding;
							next.setPosition(pos, next.getY());
							next.getColor().a = 1 - Math.min(1, Math.abs(pos) / animationXOffset);
						}
					} else {
						if (cycle || hasLastElement()) {
							final Actor next = setupLastElement();
							final float pos = currentOffsetX - (getWidth() / 2F) - panXPadding;
							next.setPosition(pos, next.getY());
							next.getColor().a = 1 - Math.min(1, Math.abs(pos) / animationXOffset);
						}
					}
				}

				@Override
				public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
					if (currentOffsetX > panXOffset) {
						if (cycle || hasLastElement()) {
							//swipe to last
							last();
						} else {
							resetPositions();
						}
						checkBeforeFirst();
					} else if (currentOffsetX < panXOffset * -1) {
						if (cycle || hasNextElement()) {
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
					if (velocityX > flingVelocity && (cycle || hasLastElement())) {
						last();
						currentOffsetX = 0;
					} else if (velocityX * -1 > flingVelocity && (cycle || hasNextElement())) {
						next();
						currentOffsetX = 0;
					}
				}
			});
		}
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
		panXOffset = (int) (getWidth() * panXOffsetFactor);
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

			fireElementChanged();
		} else {
			checkAfterLast();
		}
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

			fireElementChanged();
		} else {
			checkBeforeFirst();
		}
	}

	public void setOverSwipeListener(IOverSwipeListener overSwipeListener) {
		this.overSwipeListener = overSwipeListener;
	}

	public void setElementChangedListener(IElementChangedListener elementChangedListener) {
		this.elementChangedListener = elementChangedListener;
	}

	private void fireElementChanged() {
		if(elementChangedListener != null) {
			elementChangedListener.onElementChanged(currentElement);
		}
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
	 * sets the currently displayed element (no transition)
	 */
	public void setCurrentElement(int currentElement) {
		getChildren().get(this.currentElement).setVisible(false);
		this.currentElement = currentElement;
		getChildren().get(this.currentElement).setVisible(true);

		fireElementChanged();
	}

	public int getCurrentElement() {
		return currentElement;
	}

	/**
	 * sets padding between two elements when panning (note: that does not change anything for other means of changing the current element (eg. {@link #next()})
	 */
	public void setElementPadding(int panXPadding) {
		this.panXPadding = panXPadding;
	}

	/**
	 * sets factor (in relation to width of this actor) which calculates the amount of screen tre user has to travel to go to the next (or preceding) element
	 */
	public void setPanXOffsetFactor(float panXOffsetFactor) {
		this.panXOffsetFactor = panXOffsetFactor;
	}

	/**
	 * xOffset used when the next element gets moved in by an animation
	 * @see #setElementPadding(int)
	 */
	public void setAnimationXOffset(int animationXOffset) {
		this.animationXOffset = animationXOffset;
	}

	/**
	 * time the swipe in animation needs to play
	 */
	public void setAnimationDuration(float animationDuration) {
		this.animationDuration = animationDuration;
	}

	/**
	 * sets the speed the user has to swipe to go to the next (or preceding) element
	 */
	public void setFlingVelocity(float flingVelocity) {
		this.flingVelocity = flingVelocity;
	}

	/**
	 * sets whether panning animation to the left will be slowed (false) or not (true) when no panning past the last element
	 * <br/>
	 * this will also disable blending out in that case
	 */
	public void setAllowLeftOverdraw(boolean overDrawLeft) {
		this.overDrawLeft = overDrawLeft;
	}

	/**
	 * sets whether panning animation to the left will be slowed (false) or not (true) when no panning past the first element
	 * <br/>
	 * this will also disable blending out in that case
	 */
	public void setAllowRightOverdraw(boolean overDrawRight) {
		this.overDrawRight = overDrawRight;
	}

	/**
	 * gets notified when user continues after the last element (or the first when scrolling backwards)
	 */
	public interface IOverSwipeListener {

		void onAfterLast();

		void onBeforeFirst();
	}

	public interface IElementChangedListener {
		void onElementChanged(int newElement);
	}
}
