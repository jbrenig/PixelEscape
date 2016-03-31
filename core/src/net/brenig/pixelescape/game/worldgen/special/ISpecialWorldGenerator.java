package net.brenig.pixelescape.game.worldgen.special;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.gamemode.GameMode;

import java.util.Random;

/**
 * Spawn special World features<br/>
 * Gets called every tick
 */
public interface ISpecialWorldGenerator {

	void generate(World world, Random rand, GameMode mode);

	void reset(World world);
}
