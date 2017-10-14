package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener

/**
 * Group that only shows on table at a time.
 *
 *
 * page can be changed with gestures or programmatically
 */
class SwipeTabbedStack @JvmOverloads constructor(touchEnabled: Boolean = true) : Stack() {


    private var flingVelocity = DEFAULT_FLING_VELOCITY

    var currentElement = 0
        set(value) {
            children.get(field).isVisible = false
            field = value
            children.get(field).isVisible = true
            fireElementChanged()
        }

    private var currentOffsetX = 0

    private var panXOffset = 0
    private var panXOffsetFactor = DEFAULT_X_OFFSET_FACTOR
    private var panXPadding = DEFAULT_PAN_X_PADDING

    private var animationXOffset = DEFAULT_ANIMATION_X_OFFSET
    private var animationDuration = DEFAULT_ANIMATION_DURATION

    private var cycle = true

    private var overSwipeListener: IOverSwipeListener? = null
    private var elementChangedListener: IElementChangedListener? = null

    private var overDrawLeft = true
    private var overDrawRight = true

    private val slowOverDrawFactor = 0.5f

    constructor(panXPadding: Int) : this(true) {
        this.panXPadding = panXPadding
    }

    init {
        if (touchEnabled) {
            this.touchable = Touchable.enabled
            addListener(object : ActorGestureListener() {
                override fun pan(event: InputEvent, x: Float, y: Float, deltaX: Float, deltaY: Float) {
                    //move current element
                    val oldOffsetX = currentOffsetX
                    currentOffsetX += deltaX.toInt()
                    val actor = children.get(currentElement)

                    if (!cycle && (!overDrawLeft && currentOffsetX > 0 && !hasLastElement() || !overDrawRight && currentOffsetX < 0 && !hasNextElement())) {
                        actor.setPosition(currentOffsetX * slowOverDrawFactor, actor.y)
                        actor.color.a = 1f
                    } else {
                        actor.setPosition(currentOffsetX.toFloat(), actor.y)
                        actor.color.a = 1 - Math.min(1f, Math.abs(currentOffsetX) / animationXOffset.toFloat())
                    }

                    //remove old element
                    if (oldOffsetX <= 0 && currentOffsetX > 0) {
                        if (hasNextElement()) {
                            val nextElement = (currentElement + 1) % children.size
                            val old = children.get(nextElement)
                            old.isVisible = false
                        }
                    } else if (oldOffsetX > 0 && currentOffsetX <= 0) {
                        if (hasLastElement()) {
                            val nextElement = (currentElement - 1 + children.size) % children.size
                            val old = children.get(nextElement)
                            old.isVisible = false
                        }
                    }
                    //get next element
                    if (currentOffsetX <= 0) {
                        if (cycle || hasNextElement()) {
                            val next = setupNextElement()
                            val pos = currentOffsetX.toFloat() + width + panXPadding.toFloat()
                            next.setPosition(pos, next.y)
                            next.color.a = 1 - Math.min(1f, Math.abs(pos) / animationXOffset)
                        }
                    } else {
                        if (cycle || hasLastElement()) {
                            val next = setupLastElement()
                            val pos = currentOffsetX.toFloat() - width / 2f - panXPadding.toFloat()
                            next.setPosition(pos, next.y)
                            next.color.a = 1 - Math.min(1f, Math.abs(pos) / animationXOffset)
                        }
                    }
                }

                override fun touchUp(event: InputEvent, x: Float, y: Float, pointer: Int, button: Int) {
                    if (currentOffsetX > panXOffset) {
                        if (cycle || hasLastElement()) {
                            //swipe to last
                            last()
                        } else {
                            resetPositions()
                        }
                        checkBeforeFirst()
                    } else if (currentOffsetX < panXOffset * -1) {
                        if (cycle || hasNextElement()) {
                            //swipe to next
                            next()
                        } else {
                            resetPositions()
                        }
                        checkAfterLast()
                    } else {
                        resetPositions()
                    }
                    currentOffsetX = 0
                }

                override fun fling(event: InputEvent, velocityX: Float, velocityY: Float, button: Int) {
                    if (velocityX > flingVelocity && (cycle || hasLastElement())) {
                        last()
                        currentOffsetX = 0
                    } else if (velocityX * -1 > flingVelocity && (cycle || hasNextElement())) {
                        next()
                        currentOffsetX = 0
                    }
                }
            })
        }
    }

    fun swipeOut(direction: Boolean) {
        val old = children.get(currentElement)
        old.clearActions()
        if (direction) {
            old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset.toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))
        } else {
            old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo((-animationXOffset).toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))
        }
    }

    /**
     * resets actor positions after eg. panning
     */
    private fun resetPositions() {
        //reset positions
        val actor = children.get(currentElement)
        actor.addAction(Actions.parallel(Actions.moveTo(0f, 0f, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)))

        if (currentOffsetX <= 0 && (cycle || hasNextElement())) {
            val next = setupNextElement()
            next.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset.toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))
        } else if (cycle || hasLastElement()) {
            val next = setupLastElement()
            next.addAction(Actions.sequence(Actions.parallel(Actions.moveTo((-animationXOffset).toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))
        }
    }

    private fun checkAfterLast() {
        if (overSwipeListener != null && !hasNextElement()) {
            overSwipeListener!!.onAfterLast()
        }
    }

    private fun checkBeforeFirst() {
        if (overSwipeListener != null && !hasLastElement()) {
            overSwipeListener!!.onBeforeFirst()
        }
    }

    override fun layout() {
        super.layout()
        panXOffset = (width * panXOffsetFactor).toInt()
    }

    override fun add(actor: Actor) {
        if (children.size >= 1) {
            actor.isVisible = false
        }
        super.add(actor)
    }

    private fun setupNextElement(): Actor {
        val nextElement = (currentElement + 1) % children.size
        val next = children.get(nextElement)
        if (!next.isVisible) {
            next.color.a = 0f
            next.isVisible = true
            next.setPosition(animationXOffset.toFloat(), 0f)
            next.clearActions()
        }
        return next
    }

    private fun setupLastElement(): Actor {
        val nextElement = (currentElement - 1 + children.size) % children.size
        val next = children.get(nextElement)
        if (!next.isVisible) {
            next.color.a = 0f
            next.isVisible = true
            next.setPosition((-animationXOffset).toFloat(), 0f)
            next.clearActions()
        }
        return next
    }

    /**
     * cycles to the next element
     */
    operator fun next() {
        if (cycle || hasNextElement()) {
            val nextElement = (currentElement + 1) % children.size
            val next = setupNextElement()
            next.clearActions()
            next.addAction(Actions.parallel(Actions.moveTo(0f, 0f, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)))

            val old = children.get(currentElement)
            old.clearActions()
            old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo((-animationXOffset).toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))

            currentElement = nextElement

            fireElementChanged()
        } else {
            checkAfterLast()
        }
    }

    /**
     * cycles to the last element
     */
    fun last() {
        if (cycle || hasLastElement()) {
            val nextElement = (currentElement - 1 + children.size) % children.size
            val next = setupLastElement()
            next.clearActions()
            next.addAction(Actions.parallel(Actions.moveTo(0f, 0f, animationDuration, Interpolation.pow2In), Actions.fadeIn(animationDuration)))

            val old = children.get(currentElement)
            old.clearActions()
            old.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(animationXOffset.toFloat(), 0f, animationDuration, Interpolation.pow2In), Actions.fadeOut(animationDuration)), Actions.visible(false)))

            currentElement = nextElement

            fireElementChanged()
        } else {
            checkBeforeFirst()
        }
    }

    fun setOverSwipeListener(overSwipeListener: IOverSwipeListener) {
        this.overSwipeListener = overSwipeListener
    }

    fun setElementChangedListener(elementChangedListener: IElementChangedListener) {
        this.elementChangedListener = elementChangedListener
    }

    private fun fireElementChanged() {
        if (elementChangedListener != null) {
            elementChangedListener!!.onElementChanged(currentElement)
        }
    }

    /**
     * @return true if the currently displayed element is not the first
     */
    fun hasLastElement(): Boolean {
        return currentElement > 0
    }

    /**
     * @return true if the currently displayed element is not the last
     */
    fun hasNextElement(): Boolean {
        return currentElement < children.size - 1
    }

    fun setCycle(cycle: Boolean) {
        this.cycle = cycle
    }


    /**
     * sets padding between two elements when panning (note: that does not change anything for other means of changing the current element (eg. [.next])
     */
    fun setElementPadding(panXPadding: Int) {
        this.panXPadding = panXPadding
    }

    /**
     * sets factor (in relation to width of this actor) which calculates the amount of screen tre user has to travel to go to the next (or preceding) element
     */
    fun setPanXOffsetFactor(panXOffsetFactor: Float) {
        this.panXOffsetFactor = panXOffsetFactor
    }

    /**
     * xOffset used when the next element gets moved in by an animation
     *
     * @see .setElementPadding
     */
    fun setAnimationXOffset(animationXOffset: Int) {
        this.animationXOffset = animationXOffset
    }

    /**
     * time the swipe in animation needs to play
     */
    fun setAnimationDuration(animationDuration: Float) {
        this.animationDuration = animationDuration
    }

    /**
     * sets the speed the user has to swipe to go to the next (or preceding) element
     */
    fun setFlingVelocity(flingVelocity: Float) {
        this.flingVelocity = flingVelocity
    }

    /**
     * sets whether panning animation to the left will be slowed (false) or not (true) when no panning past the last element
     * <br></br>
     * this will also disable blending out in that case
     */
    fun setAllowLeftOverdraw(overDrawLeft: Boolean) {
        this.overDrawLeft = overDrawLeft
    }

    /**
     * sets whether panning animation to the left will be slowed (false) or not (true) when no panning past the first element
     * <br></br>
     * this will also disable blending out in that case
     */
    fun setAllowRightOverdraw(overDrawRight: Boolean) {
        this.overDrawRight = overDrawRight
    }

    /**
     * gets notified when user continues after the last element (or the first when scrolling backwards)
     */
    interface IOverSwipeListener {

        fun onAfterLast()

        fun onBeforeFirst()
    }

    interface IElementChangedListener {
        fun onElementChanged(newElement: Int)
    }

    companion object {

        const val DEFAULT_ANIMATION_X_OFFSET = 400
        const val DEFAULT_ANIMATION_DURATION = 0.2f

        const val DEFAULT_FLING_VELOCITY = 800f

        const val DEFAULT_PAN_X_PADDING = 400

        const val DEFAULT_X_OFFSET_FACTOR = 0.16f
    }
}
