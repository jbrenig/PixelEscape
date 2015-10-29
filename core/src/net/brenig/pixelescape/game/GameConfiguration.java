package net.brenig.pixelescape.game;

/**
 * Allows for platform specific game settings
 */
public class GameConfiguration {

	public boolean canQuitGame() {
		return true;
	}

	public boolean canGoFullScreen() {
		return false;
	}
}
