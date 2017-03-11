package net.brenig.pixelescape.lib;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * provides an object based upon an instance of {@link Random}
 *
 * @see net.brenig.pixelescape.game.worldgen.WeightedList
 */
public interface FilteredElementProvider<T> {

	/**
	 * get a random value of this provider (taking weight into consideration)
	 *
	 * @param rand used {@link Random} instance
	 * @return a random value, null if no element was found
	 */
	@Nullable
	T getRandomValue(Random rand);

	class SingleElementProvider<T> implements FilteredElementProvider<T> {
		private T element;


		public SingleElementProvider(@Nullable T element) {
			this.element = element;
		}

		@Override
		@Nullable
		public T getRandomValue(Random rand) {
			return element;
		}
	}
}
