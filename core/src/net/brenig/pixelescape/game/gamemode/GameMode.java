package net.brenig.pixelescape.game.gamemode;

import net.brenig.pixelescape.game.World;
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
	 * Create and initialize the WorldGenerator for this GameMode
	 */
	public WorldGenerator createWorldGenerator() {
		WorldGenerator gen = new WorldGenerator(this);
		gen.init();
		return gen;
	}

	/**
	 * @return the amount of extra lives the player has when the game starts
	 */
	public int getExtraLives() {
		return 0;
	}

	public boolean abilitiesEnabled() {
		return false;
	}
}
