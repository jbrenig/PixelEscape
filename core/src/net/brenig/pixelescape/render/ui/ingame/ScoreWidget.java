package net.brenig.pixelescape.render.ui.ingame;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Widget that displays current score<br></br>
 * gets draw on a white backgound texture
 */
public class ScoreWidget extends Widget {

	private EntityPlayer player;
	private static final String SCORE_TEXT = "Score: ";

	private final PixelEscape game;
	private final GlyphLayout fontLayout;
	private float lastScoreScreenWidth = 0;

	public ScoreWidget(GameScreen screen) {
		this(screen.world.getPlayer(), screen.getFontLayout(), screen.game);
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
		game.getButtonNinePatch().draw(batch, getX(), getY(), getWidth(), getHeight());

		//Score text
		game.getFont().setColor(0, 0, 0, 1);
		setScoreText();
		game.getFont().draw(batch, fontLayout, getX() + getWidth() / 2 - fontLayout.width / 2, getY() + getHeight() / 2 + fontLayout.height / 2);
	}

	private void setScoreText() {
		String score = SCORE_TEXT + player.getScore();
//		game.font.getData().setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
		fontLayout.setText(game.getFont(), score);
		if (fontLayout.width > lastScoreScreenWidth || lastScoreScreenWidth - fontLayout.width > Reference.GAME_UI_SCORE_SCREEN_SIZE_BUFFER) {
			lastScoreScreenWidth = fontLayout.width;
			invalidateHierarchy();
		}
	}

	@Override
	public float getPrefWidth() {
		setScoreText();
		return game.getButtonNinePatch().getPadLeft() + game.getButtonNinePatch().getPadRight() + lastScoreScreenWidth;
	}

	@Override
	public float getPrefHeight() {
		setScoreText();
		return game.getButtonNinePatch().getPadBottom() + game.getButtonNinePatch().getPadTop() + fontLayout.height;
	}
}
