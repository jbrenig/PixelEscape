package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;

/**
 * Created by Jonas Brenig on 05.11.2015.
 */
public class PixelDialog extends Dialog {

	private static final float defaultFontScale = 0.7F;

	private float prefWidth = -1;
	private float prefHeight = -1;


	public PixelDialog(String title, Skin skin) {
		super(title, skin);
	}

	public PixelDialog(String title, Skin skin, String windowStyleName) {
		super(title, skin, windowStyleName);
	}

	public PixelDialog(String title, WindowStyle windowStyle) {
		super(title, windowStyle);
	}

	public void setPrefHeight(float prefHeight) {
		this.prefHeight = prefHeight;
	}

	public void setPrefWidth(float prefWidth) {
		this.prefWidth = prefWidth;
	}

	public void init() {
		getTitleLabel().setAlignment(Align.center);
		padTop(50);
		padBottom(10);
		getContentTable().left().padTop(10);
		getButtonTable().padTop(10);
		invalidateHierarchy();
		invalidate();
		layout();
	}

	@Override
	public void layout() {
//		updateCells(getPrefWidth());
		super.layout();
	}

	private void updateCells(float width) {
		if(getContentTable() != null) {
			for (Cell c : getContentTable().getCells()) {
				c.width(width);
			}
		}
	}

	public Dialog label(String text) {
		Label l = new Label(text, getSkin());
		l.setWrap(true);
		l.setFontScale(defaultFontScale);
		getContentTable().add(l).width(getPrefContentWidth()).row();
		return this;
	}

	public float getPrefContentWidth() {
		if(prefWidth != -1) return prefWidth;
		return getTitleLabel().getPrefWidth();
	}

	@Override
	public float getPrefHeight() {
		if(prefHeight != -1) return prefHeight;
		return super.getPrefHeight();
	}

	@Override
	public float getPrefWidth() {
		if(prefWidth != -1) return prefWidth + getPadLeft() + getPadRight();
		return super.getPrefWidth();
	}
}
