package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Align

/**
 * Dialog for PixelEscape<br></br>
 * Provides a texture and some default settings
 */
class PixelDialog : Dialog {

    private var prefWidth = -1f
    private var prefHeight = -1f

    val prefContentWidth: Float
        get() = if (prefWidth != -1f) prefWidth else titleLabel.prefWidth


    constructor(title: String, skin: Skin) : super(title, skin)

    constructor(title: String, skin: Skin, windowStyleName: String) : super(title, skin, windowStyleName)

    constructor(title: String, windowStyle: Window.WindowStyle) : super(title, windowStyle)

    fun setPrefHeight(prefHeight: Float) {
        this.prefHeight = prefHeight
    }

    fun setPrefWidth(prefWidth: Float) {
        this.prefWidth = prefWidth
    }

    fun init() {
        titleLabel.setAlignment(Align.center)
        padTop(50f)
        padBottom(10f)
        contentTable.left().padTop(10f)
        buttonTable.padTop(10f)
        color.a = 0f
        invalidateHierarchy()
        invalidate()
        layout()
    }

    private fun updateCells(width: Float) {
        if (contentTable != null) {
            for (c in contentTable.cells) {
                c.width(width)
            }
        }
    }

    fun label(text: String): Label {
        val l = Label(text, skin)
        l.setWrap(true)
        l.setFontScale(defaultFontScale)
        contentTable.add(l).width(prefContentWidth).row()
        return l
    }

    override fun getPrefHeight(): Float {
        return if (prefHeight != -1f) prefHeight else super.getPrefHeight()
    }

    override fun getPrefWidth(): Float {
        return if (prefWidth != -1f) prefWidth + padLeft + padRight else super.getPrefWidth()
    }

    fun buttonYes(listener: ClickListener): TextButton {
        val btnYes = TextButton("Yes", skin)
        btnYes.addListener(listener)
        button(btnYes)
        return btnYes
    }

    fun buttonNo(listener: ClickListener): TextButton {
        val btnNo = TextButton("No", skin)
        btnNo.addListener(listener)
        button(btnNo)
        return btnNo
    }

    companion object {

        private val defaultFontScale = 0.7f
    }
}
