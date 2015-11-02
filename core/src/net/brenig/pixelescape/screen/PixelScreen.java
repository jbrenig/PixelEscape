package net.brenig.pixelescape.screen;

import com.badlogic.gdx.Screen;

import net.brenig.pixelescape.PixelEscape;

/**
 * Created by Jonas Brenig on 31.10.2015.
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
