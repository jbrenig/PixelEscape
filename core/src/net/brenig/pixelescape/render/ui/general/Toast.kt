package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Interpolation.fade
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.actions.Actions.*
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Timer
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.render.ui.general.action.Origin

/**
 *
 */
class Toast(text: String, style: ToastStyle) : Table() {

    constructor(text: String, skin: Skin, styleName: String = StyleNames.DEFAULT) : this(text, skin.get(styleName, ToastStyle::class.java))

    private val cell: Cell<Table>
    private val labelContainer: Table
    private val showTime = 2f

    private val hideTask = object : Timer.Task() {
        override fun run() {
            hideAction()
        }
    }

    init {
        setFillParent(true)
        setPosition(0f, 0f)

        val labelLayout = Table()

        labelContainer = Table()

        val label = Label(text, style.label)
        label.setWrap(true)
        label.setAlignment(Align.center)

        labelContainer.add(label).expandX().fillX()
        labelContainer.background = style.background

        labelLayout.add(labelContainer).expandX().fillX().center()

        cell = add(labelLayout).bottom().expand().fillX()
                .pad(0f, 4f, 20f,4f)

        touchable = Touchable.disabled
    }

    fun show(actor: Actor) {
        if (actor.stage == null) {
            return
        }
        show(actor.stage)
        setYPos(actor.localToStageCoordinates(Vector2(0f, -10f)).y)
    }

    fun show(stage: Stage) {
        stage.addActor(this)
        showAnimation()
        Timer.schedule(hideTask, showTime)
    }


    private fun showAnimation() {
        labelContainer.isTransform = true
        labelContainer.color.a = 0f
        labelContainer.addAction(sequence(delay(0.1f), Origin(Align.center), scaleTo(0.05f, 0.05f), alpha(0.2f), parallel(fadeIn(0.2f, fade), scaleTo(1f, 1f, 0.2f, Interpolation.fade))))
    }

    fun hideAction() {
        labelContainer.addAction(parallel(alpha(0.2f, 0.2f, fade), scaleTo(0.05f, 0.05f, 0.2f, Interpolation.fade)))
        addAction(sequence(delay(0.2f), removeActor()))
    }

    fun setYPos(pos: Float) {
        cell.padBottom(Math.max(0f, pos - 10f))
    }

    class ToastStyle {

        var label: Label.LabelStyle? = null
        /** Optional.  */
        var background: Drawable? = null

        constructor()

        constructor(label: Label.LabelStyle, background: Drawable) {
            this.label = label
            this.background = background
        }

        constructor(style: ToastStyle){
            this.label = Label.LabelStyle(style.label)
            background = style.background
        }
    }
}