package net.brenig.pixelescape.lib;

import java.util.Random;

/**
 * provides an object based upon an instance of {@link Random}
 * @see net.brenig.pixelescape.game.worldgen.WeightedList
 */
public interface FilteredElementProvider<T> {

	/**
	 * get a random value of this provider (taking weight into consideration)
	 *
	 * @param rand used {@link Random} instance
	 *
	 * @return a random value, null if no element was found
	 */
	T getRandomValue(Random rand);

	class SingleElementProvider<T> implements FilteredElementProvider<T> {
		private T element;


		public SingleElementProvider(T element) {
			this.element = element;
		}

		@Override
		public T getRandomValue(Random rand) {
			return element;
		}
	}
}
