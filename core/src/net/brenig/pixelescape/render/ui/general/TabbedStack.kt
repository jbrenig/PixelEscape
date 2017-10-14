package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Stack

/**
 * A [Stack] that only shows one children at a time
 * It cycles through its children in the given order (animated)
 */
class TabbedStack : Stack() {

    private var currentElement = 0

    override fun add(actor: Actor) {
        if (children.size >= 1) {
            actor.isVisible = false
        }
        super.add(actor)
    }

    /**
     * sets the currently displayed element (no transition)
     */
    fun setCurrentElement(currentElement: Int) {
        children.get(this.currentElement).isVisible = false
        this.currentElement = currentElement
        children.get(this.currentElement).isVisible = true
    }

    private fun setupNextElement(): Actor {
        val nextElement = (currentElement + 1) % children.size
        val next = children.get(nextElement)
        next.isVisible = true
        next.setPosition(animationXOffset.toFloat(), 0f)
        next.clearActions()
        return next
    }

    private fun setupLastElement(): Actor {
        val nextElement = (currentElement - 1 + children.size) % children.size
        val next = children.get(nextElement)
        next.isVisible = true
        next.setPosition((-animationXOffset).toFloat(), 0f)
        next.clearActions()
        return next
    }

    /**
     * cycles to the next element
     */
    operator fun next() {
        val nextElement = (currentElement + 1) % children.size
        val next = setupNextElement()
        next.addAction(Actions.parallel(Actions.moveTo(0f, 0f, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)))

        val old = children.get(currentElement)
        old.clearActions()
        old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo((-animationXOffset).toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))

        currentElement = nextElement
    }

    /**
     * cycles to the last element
     */
    fun last() {
        val nextElement = (currentElement - 1 + children.size) % children.size
        val next = setupLastElement()
        next.addAction(Actions.parallel(Actions.moveTo(0f, 0f, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)))

        val old = children.get(currentElement)
        old.clearActions()
        old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset.toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))

        currentElement = nextElement
    }

    /**
     * @return the currently displayed (locked in) element
     */
    fun getCurrentElement(): Int {
        return currentElement
    }

    companion object {

        private const val animationXOffset = 400
        private const val animationDuration = 0.2f
    }
}

