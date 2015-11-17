package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Scaling;

/**
 * Created by Jonas Brenig on 25.10.2015.
 */
public class TwoStateImageButton extends Button {

	private boolean state = false;

	private final Image image;
	private TwoStateImageButtonStyle style;

	public TwoStateImageButton(Skin skin) {
		this(skin.get(TwoStateImageButtonStyle.class));
	}

	public TwoStateImageButton(Skin skin, String styleName) {
		this(skin.get(styleName, TwoStateImageButtonStyle.class));
	}

	public TwoStateImageButton(TwoStateImageButtonStyle style) {
		super(style);
		image = new Image();
		image.setScaling(Scaling.fit);
		add(image).fill().expand();
		setStyle(style);
		setSize(getPrefWidth(), getPrefHeight());
	}

	public TwoStateImageButton(Drawable image1, Drawable image2) {
		this(new TwoStateImageButtonStyle(null, null, null, image1, null, image2, null));
	}

	public TwoStateImageButton(Drawable image1Up, Drawable image2Up, Drawable image1Down, Drawable image2Down) {
		this(new TwoStateImageButtonStyle(null, null, null, image1Up, image1Down, image2Up, image2Down));
	}

	public void setStyle(ButtonStyle style) {
		if (!(style instanceof TwoStateImageButtonStyle))
			throw new IllegalArgumentException("style must be an TwoStateImageButtonStyle.");
		super.setStyle(style);
		this.style = (TwoStateImageButtonStyle) style;
		if (image != null) updateImage();
	}

	public TwoStateImageButtonStyle getStyle() {
		return style;
	}

	private void updateImage() {
		Drawable drawable = null;
		if (!state) {
			if (isDisabled() && style.imageDisabled != null)
				drawable = style.imageDisabled;
			else if (isPressed() && style.imageDown != null)
				drawable = style.imageDown;
			else if (isOver() && style.imageOver != null)
				drawable = style.imageOver;
			else if (style.imageUp != null)
				drawable = style.imageUp;
		} else {
			if (isDisabled() && style.image2Disabled != null)
				drawable = style.image2Disabled;
			else if (isPressed() && style.image2Down != null)
				drawable = style.image2Down;
			else if (isOver() && style.image2Over != null)
				drawable = style.image2Over;
			else if (style.image2Up != null)
				drawable = style.image2Up;
			else if (style.imageUp != null) //reset to default image if state two is not set
				drawable = style.imageUp;
		}
		image.setDrawable(drawable);
	}

	public void draw(Batch batch, float parentAlpha) {
		updateImage();
		super.draw(batch, parentAlpha);
	}

	public Image getImage() {
		return image;
	}

	public Cell getImageCell() {
		return getCell(image);
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean getState() {
		return state;
	}

	/**
	 * The style for an two state image button
	 */
	static public class TwoStateImageButtonStyle extends Button.ButtonStyle {
		/**
		 * Optional.
		 */
		public Drawable imageUp, imageDown, imageOver, imageDisabled;
		public Drawable image2Up, image2Down, image2Over, image2Disabled;

		public TwoStateImageButtonStyle() {
		}

		public TwoStateImageButtonStyle(Drawable up, Drawable down, Drawable checked, Drawable imageUp, Drawable imageDown, Drawable image2Up, Drawable image2Down) {
			super(up, down, checked);
			this.imageUp = imageUp;
			this.imageDown = imageDown;
			this.image2Up = image2Up;
			this.image2Down = image2Down;
		}

		public TwoStateImageButtonStyle(TwoStateImageButtonStyle style) {
			super(style);
			this.imageUp = style.imageUp;
			this.imageDown = style.imageDown;
			this.imageOver = style.imageOver;
			this.imageDisabled = style.imageDisabled;
			this.image2Up = style.image2Up;
			this.image2Down = style.image2Down;
			this.image2Over = style.image2Over;
			this.image2Disabled = style.image2Disabled;
		}

		public TwoStateImageButtonStyle(ButtonStyle style) {
			super(style);
		}
	}
}
