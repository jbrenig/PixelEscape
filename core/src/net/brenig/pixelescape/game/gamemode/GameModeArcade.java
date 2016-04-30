package net.brenig.pixelescape.game.gamemode;


import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.player.abliity.Ability;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.game.worldgen.special.ItemGenerator;
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
	public Ability getStartingAbility() {
		return null;
	}

	@Override
	public void registerWorldGenerators(WorldGenerator worldGenerator) {
		super.registerWorldGenerators(worldGenerator);
//		worldGenerator.addSpecialGenerator(new ItemGenerator(3000, 5000, 4000, 6000, ItemGenerator.createDefaultItemList()));
		worldGenerator.addSpecialGenerator(new ItemGenerator(600, 1000, 800, 1600, ItemGenerator.createDefaultItemList()));
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
