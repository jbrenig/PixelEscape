package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextTooltip
import com.badlogic.gdx.scenes.scene2d.utils.Disableable

/**
 *
 */
class DisabledTextTooltip : TextTooltip {

    constructor(text: String?, style: TextTooltipStyle) : super(text, style)
    constructor(text: String?, skin: Skin) : super(text, skin)
    constructor(text: String?, skin: Skin, styleName: String) : super(text, skin, styleName)

    override fun enter(event: InputEvent?, x: Float, y: Float, pointer: Int, fromActor: Actor?) {
        if (event?.listenerActor is Disableable && !(event.listenerActor as Disableable).isDisabled) {
            return
        }
        super.enter(event, x, y, pointer, fromActor)
    }
}