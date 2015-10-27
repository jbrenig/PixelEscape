package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.entity.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Created by Jonas Brenig on 27.10.2015.
 */
public class ScoreWidget extends Widget {

	private EntityPlayer player;
	private static final String SCORE_TEXT = "Score: ";

	private PixelEscape game;
	private GlyphLayout fontLayout;
	private float lastScoreScreenWidth = 0;
	private static final int paddingSide = 0;
	private static final int paddingHeight = 0;

	public ScoreWidget(GameScreen screen) {
		this(screen.world.player, screen.fontLayout, screen.game);
	}

	public ScoreWidget(EntityPlayer player, GlyphLayout fontLayout, PixelEscape game) {
		super();
		this.player = player;
		this.fontLayout = fontLayout;
		this.game = game;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		//validate layout
		super.draw(batch, parentAlpha);

		//Background
		batch.setColor(1, 1, 1, 1);
		game.buttonNinePatch.draw(batch, getX(), getY(), getWidth(), getHeight());

		//Score text
		game.font.setColor(0, 0, 0, 1);
		setScoreText();
		game.font.draw(batch, fontLayout, getX() + game.buttonNinePatch.getPadLeft(), getY() + fontLayout.height + game.buttonNinePatch.getPadTop());
	}

	private void setScoreText() {
		String score = SCORE_TEXT + player.getScore();
//		game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		fontLayout.setText(game.font, score);
		if (fontLayout.width > lastScoreScreenWidth || lastScoreScreenWidth - fontLayout.width > Reference.GAME_UI_SCORE_SCREEN_SIZE_BUFFER) {
			lastScoreScreenWidth = fontLayout.width;
			invalidateHierarchy();
		}
	}

	@Override
	public float getPrefWidth() {
		setScoreText();
		return game.buttonNinePatch.getPadLeft() + game.buttonNinePatch.getPadRight() + lastScoreScreenWidth;
	}

	@Override
	public float getPrefHeight() {
		setScoreText();
		return game.buttonNinePatch.getPadBottom() + game.buttonNinePatch.getPadTop() + fontLayout.height;
	}
}
