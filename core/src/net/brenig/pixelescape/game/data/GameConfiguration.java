package net.brenig.pixelescape.game.data;

import net.brenig.pixelescape.lib.Reference;

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

	public boolean canHideCursor() {
		return false;
	}

	public boolean debugSettingsAvailable() {
		return Reference.DEBUG_SETTINGS_AVAILABLE;
	}
}
