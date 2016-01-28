package net.brenig.pixelescape.game.gamemode;


import net.brenig.pixelescape.game.entity.player.abliity.AbilityBlink;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;
import net.brenig.pixelescape.lib.Names;

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

	@Override
	public String getScoreboardName() {
		return Names.SCOREBOARD_ARCADE;
	}
}
