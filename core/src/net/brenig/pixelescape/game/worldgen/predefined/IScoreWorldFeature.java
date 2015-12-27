package net.brenig.pixelescape.game.worldgen.predefined;

import net.brenig.pixelescape.game.entity.Entity;

public interface IScoreWorldFeature {

	/**
	 * create the entity to be spawned
	 */
	Entity createWorldFeature();

	/**
	 * @return the world coordinate which, when visible should trigger the spawn of the WorldFeature-Entity
	 */
	float getSpawnX();

	void reset();

}
