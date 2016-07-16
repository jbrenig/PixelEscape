package net.brenig.pixelescape.render.ui.general;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;

/**
 * A {@link Stack} that only shows one children at a time
 * It cycles through its children in the given order (animated)
 */
public class TabbedStack extends Stack {

	private int currentElement = 0;

	private final static int animationXOffset = 400;
	private final static float animationDuration = 0.2F;

	public TabbedStack() {
		super();
	}

	@Override
	public void add(Actor actor) {
		if (getChildren().size >= 1) {
			actor.setVisible(false);
		}
		super.add(actor);
	}

	/**
	 * sets the currently displayed element (no transition)
	 */
	public void setCurrentElement(int currentElement) {
		getChildren().get(this.currentElement).setVisible(false);
		this.currentElement = currentElement;
		getChildren().get(this.currentElement).setVisible(true);
	}

	private Actor setupNextElement() {
		final int nextElement = (currentElement + 1) % getChildren().size;
		final Actor next = getChildren().get(nextElement);
		next.setVisible(true);
		next.setPosition(animationXOffset, 0);
		next.clearActions();
		return next;
	}

	private Actor setupLastElement() {
		final int nextElement = (currentElement - 1 + getChildren().size) % getChildren().size;
		final Actor next = getChildren().get(nextElement);
		next.setVisible(true);
		next.setPosition(-animationXOffset, 0);
		next.clearActions();
		return next;
	}

	/**
	 * cycles to the next element
	 */
	public void next() {
		final int nextElement = (currentElement + 1) % getChildren().size;
		final Actor next = setupNextElement();
		next.addAction(Actions.parallel(Actions.moveTo(0, 0, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)));

		final Actor old = getChildren().get(currentElement);
		old.clearActions();
		old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(-animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));

		currentElement = nextElement;
	}

	/**
	 * cycles to the last element
	 */
	public void last() {
		final int nextElement = (currentElement - 1 + getChildren().size) % getChildren().size;
		final Actor next = setupLastElement();
		next.addAction(Actions.parallel(Actions.moveTo(0, 0, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)));

		final Actor old = getChildren().get(currentElement);
		old.clearActions();
		old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset, 0, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)));

		currentElement = nextElement;
	}

	/**
	 * @return the currently displayed (locked in) element
	 */
	public int getCurrentElement() {
		return currentElement;
	}
}

