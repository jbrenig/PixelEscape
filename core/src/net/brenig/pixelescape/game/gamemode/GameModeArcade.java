package net.brenig.pixelescape.game.gamemode;


public class GameModeArcade extends GameMode {
	@Override
	public int getExtraLives() {
		return 3;
	}

	@Override
	public boolean abilitiesEnabled() {
		return true;
	}
}
