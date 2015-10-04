package net.brenig.pixelescape.game.worldgen.terrain;

import net.brenig.pixelescape.game.worldgen.ITerrainGenerator;

/**
 * Created by Jonas Brenig on 25.09.2015.
 */
public abstract class AbstractTerrainGenerator implements ITerrainGenerator {

	protected int weight;

	public AbstractTerrainGenerator(int weight) {
		this.weight = weight;
	}

	public int getWeight() {
		return weight;
	}
}
