package net.brenig.pixelescape.game.data;

import net.brenig.pixelescape.lib.Reference;

/**
 * Allows for platform specific game settings
 */
public class GameConfiguration {

	/**
	 * @return whether this platform should provide a button to quit the game
	 */
	public boolean canQuitGame() {
		return true;
	}

	/**
	 * @return whether this platform is able to switch to fullscreen
	 */
	public boolean canGoFullScreen() {
		return false;
	}

	/**
	 * @return whether this platform should use bigger buttons (eg. to be optimized for touchscreens)
	 */
	public boolean useBiggerButtons() {
		return false;
	}

	/**
	 * @return whether this platform has a cursor that can be hidden
	 */
	public boolean canHideCursor() {
		return false;
	}

	/**
	 * @return whether debugsettings should be enabled
	 */
	public boolean debugSettingsAvailable() {
		return Reference.DEBUG_SETTINGS_AVAILABLE;
	}
}
