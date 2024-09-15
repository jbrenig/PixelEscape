package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Scaling

/**
 * ImageButton that can have two states<br></br>
 * draws different texture depending on state
 */
open class TwoStateImageButton() : Button() {

    var state = false

    val image: Image = Image()

    @Suppress("LeakingThis")
    constructor(style: TwoStateImageButtonStyle) : this() {
        setStyle(style)
        setSize(prefWidth, prefHeight)
    }

    constructor(skin: Skin) : this(skin.get<TwoStateImageButtonStyle>(TwoStateImageButtonStyle::class.java))

    constructor(skin: Skin, styleName: String) : this(skin.get<TwoStateImageButtonStyle>(styleName, TwoStateImageButtonStyle::class.java))

    constructor(image1: Drawable, image2: Drawable) : this(TwoStateImageButtonStyle(null, null, null, image1, null, image2, null))

    constructor(image1Up: Drawable, image2Up: Drawable, image1Down: Drawable, image2Down: Drawable) : this(TwoStateImageButtonStyle(null, null, null, image1Up, image1Down, image2Up, image2Down))

    init {
        image.setScaling(Scaling.fit)
        @Suppress("LeakingThis")
        add(image).fill().expand()
    }

    override fun setStyle(style: Button.ButtonStyle) {
        if (style !is TwoStateImageButtonStyle)
            throw IllegalArgumentException("style must be an TwoStateImageButtonStyle.")
        super.setStyle(style)
        updateImage()
    }

    override fun getStyle(): TwoStateImageButtonStyle {
        return super.getStyle() as TwoStateImageButtonStyle
    }

    private fun updateImage() {
        var drawable: Drawable? = null
        if (!state) {
            if (isDisabled && style.imageDisabled != null)
                drawable = style.imageDisabled
            else if (isPressed && style.imageDown != null)
                drawable = style.imageDown
            else if (isOver && style.imageOver != null)
                drawable = style.imageOver
            else if (style.imageUp != null)
                drawable = style.imageUp
        } else {
            if (isDisabled && style.image2Disabled != null)
                drawable = style.image2Disabled
            else if (isPressed && style.image2Down != null)
                drawable = style.image2Down
            else if (isOver && style.image2Over != null)
                drawable = style.image2Over
            else if (style.image2Up != null)
                drawable = style.image2Up
            else if (style.imageUp != null)
                //reset to default image if state two is not set
                drawable = style.imageUp
        }
        image.drawable = drawable
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        updateImage()
        super.draw(batch, parentAlpha)
    }

    /**
     * The style for an two state image button
     */
    class TwoStateImageButtonStyle : Button.ButtonStyle {
        /**
         * Optional.
         */
        var imageUp: Drawable? = null
        var imageDown: Drawable? = null
        var imageOver: Drawable? = null
        var imageDisabled: Drawable? = null
        var image2Up: Drawable? = null
        var image2Down: Drawable? = null
        var image2Over: Drawable? = null
        var image2Disabled: Drawable? = null

        constructor()

        constructor(up: Drawable?, down: Drawable?, checked: Drawable?,
                    imageUp: Drawable?, imageDown: Drawable?,
                    image2Up: Drawable?, image2Down: Drawable?) : super(up, down, checked) {
            this.imageUp = imageUp
            this.imageDown = imageDown
            this.image2Up = image2Up
            this.image2Down = image2Down
        }

        constructor(style: TwoStateImageButtonStyle) : super(style) {
            this.imageUp = style.imageUp
            this.imageDown = style.imageDown
            this.imageOver = style.imageOver
            this.imageDisabled = style.imageDisabled
            this.image2Up = style.image2Up
            this.image2Down = style.image2Down
            this.image2Over = style.image2Over
            this.image2Disabled = style.image2Disabled
        }

        constructor(style: Button.ButtonStyle) : super(style)
    }
}
