package net.brenig.pixelescape.game.gamemode;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.lib.Names;

/**
 * Gamemode with high speed gain
 */
public class GameModeSpeed extends GameMode {

	@Override
	public String getScoreboardName() {
		return Names.SCOREBOARD_SPEED;
	}

	@Override
	public String getGameModeName() {
		return "Speed";
	}

	@Override
	public TextureRegion getIcon(GameAssets assets) {
		return assets.getTextureAtlas().findRegion("gamemode_speed");
	}

	@Override
	public float getStartingSpeed() {
		return super.getStartingSpeed() * 2;
	}

	@Override
	public float getSpeedIncreaseFactor() {
		return super.getSpeedIncreaseFactor() * 3;
	}

	@Override
	public float getMaxEntitySpeed() {
		return super.getMaxEntitySpeed() * 1.2F;
	}
}
