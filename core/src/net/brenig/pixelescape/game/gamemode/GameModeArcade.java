package net.brenig.pixelescape.game.gamemode;


import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.player.abliity.AbilityBlink;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;
import net.brenig.pixelescape.lib.Names;

public class GameModeArcade extends GameMode {
	@Override
	public int getExtraLives() {
		return 2;
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

	@Override
	public String getGameModeName() {
		return "Arcade";
	}

	@Override
	public TextureRegion getIcon(GameAssets assets) {
		return assets.getTextureAtlas().findRegion("gamemode_arcade");
	}
}
