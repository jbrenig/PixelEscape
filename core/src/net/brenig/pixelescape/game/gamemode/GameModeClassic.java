package net.brenig.pixelescape.game.gamemode;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.lib.Names;

public class GameModeClassic extends GameMode {

	@Override
	public String getScoreboardName() {
		return Names.SCOREBOARD_CLASSIC;
	}

	@Override
	public String getGameModeName() {
		return "Classic";
	}

	@Override
	public TextureRegion getIcon(GameAssets assets) {
		return assets.getTextureAtlas().findRegion("gamemode_classic");
	}
}
