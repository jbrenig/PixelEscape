package net.brenig.pixelescape.game.gamemode;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;
import net.brenig.pixelescape.lib.Reference;

public abstract class GameMode {

	/**
	 * @return whether barricades should be generated
	 * @param world the world to generate in
	 */
	public boolean shouldGenerateBarricades(World world) {
		return true;
	}

	/**
	 * @return the speed the player has when the game starts
	 */
	public double getStartingSpeed() {
		return Reference.STARTING_SPEED;
	}

	/**
	 * @return the amount of extra lives the player has when the game starts
	 */
	public int getExtraLives() {
		return 0;
	}

	/**
	 * whether abilities are enabled (note: this value must not change!)
	 */
	public boolean abilitiesEnabled() {
		return false;
	}

	/**
	 * @return the ability that is available when the game begins (returns null if abilities are disabled)
	 */
	public IAbility getStartingAbility() {
		return null;
	}

	/**
	 * add WorldFeatures to the given worldGenerator
	 */
	public void registerWorldGenerators(WorldGenerator worldGenerator) {
		worldGenerator.registerDefaultWorldGenerators();
	}

	/**
	 * @return the name of the scoreboard that should be used
	 */
	public abstract String getScoreboardName();

	/**
	 * @return the name of this gamemode (unlocalized)
	 */
	public abstract String getGameModeName();

	/**
	 * @return the icon of this gamemode
	 */
	public abstract TextureRegion getIcon(GameAssets assets);
}
