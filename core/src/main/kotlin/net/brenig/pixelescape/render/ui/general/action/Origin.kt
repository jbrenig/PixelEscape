package net.brenig.pixelescape.render.ui.general.action

import com.badlogic.gdx.scenes.scene2d.Action

/**
 *
 */
class Origin(val align: Int) : Action() {

    override fun act(delta: Float): Boolean {
        actor.setOrigin(align)
        return true
    }
}