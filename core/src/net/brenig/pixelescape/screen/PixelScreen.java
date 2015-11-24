package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Screen;

import net.brenig.pixelescape.PixelEscape;

/**
 * Abstract GameScreen providing shared functionality for all Screens of PixelEscape
 */
public abstract class PixelScreen implements Screen {
	public final PixelEscape game;

	public PixelScreen(PixelEscape game) {
		this.game = game;
	}

	public void updateMusic(boolean play) {
		if(play) {
			game.gameMusic.play(true);
		}
	}
}
