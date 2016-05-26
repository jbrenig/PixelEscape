package net.brenig.pixelescape.game.worldgen.special;

import net.brenig.pixelescape.game.World;
import net.brenig.pixelescape.game.gamemode.GameMode;
import net.brenig.pixelescape.game.worldgen.WorldGenerator;

import java.util.Random;

/**
 * Spawn special World features<br/>
 * Gets called every tick
 */
public interface ISpecialWorldGenerator {

	/**
	 * generate this element
	 * @param generator current worldgenerator
	 * @param world current world
	 * @param rand Random instance to use
	 * @param mode current gamemode
	 */
	void generate(WorldGenerator generator, World world, Random rand, GameMode mode);

	/**
	 * reset worldgenerator (--> eg. on game restart)
	 * @param world the new world
	 */
	void reset(World world);
}
