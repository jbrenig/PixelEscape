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

	/**
	 * @return whether this platform should use bigger buttons (eg. to be optimized for touchscreens)
	 */
	public boolean useBiggerButtons() {
		return false;
	}
}
