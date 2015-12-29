package net.brenig.pixelescape.game.gamemode;


import net.brenig.pixelescape.game.entity.player.abliity.AbilityBlink;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;

public class GameModeArcade extends GameMode {
	@Override
	public int getExtraLives() {
		return 3;
	}

	@Override
	public boolean abilitiesEnabled() {
		return true;
	}

	@Override
	public IAbility getStartingAbility() {
		return new AbilityBlink();
	}
}
