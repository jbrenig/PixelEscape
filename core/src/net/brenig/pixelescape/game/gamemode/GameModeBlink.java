package net.brenig.pixelescape.game.gamemode;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.player.abliity.Ability;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.game.worldgen.special.BarricadeGenerator;
import net.brenig.pixelescape.lib.Names;

/**
 * GameMode that provides the player with blink ability, but spawns a lot of obstacles
 */
public class GameModeBlink extends GameMode {

	@Override
	public String getScoreboardName() {
		return Names.SCOREBOARD_BLINK;
	}

	@Override
	public String getGameModeName() {
		return "Blink";
	}

	@Override
	public TextureRegion getIcon(GameAssets assets) {
		return assets.getHeart();
	}

	@Override
	public void registerWorldGenerators(WorldGenerator worldGenerator) {
		worldGenerator.registerDefaultTerrainGenerators();
		worldGenerator.addSpecialGenerator(new BarricadeGenerator(400));
		worldGenerator.obstacleSizeModifier = 1.5F;
	}

	@Override
	public Ability getStartingAbility() {
		return Ability.BLINK;
	}

	@Override
	public boolean abilitiesEnabled() {
		return true;
	}
}
