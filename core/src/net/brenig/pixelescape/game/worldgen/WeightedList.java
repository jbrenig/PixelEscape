package net.brenig.pixelescape.game.worldgen;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * Used to provide weighted randomness<br/>
 * to provide this functionality an underlying {@link HashMap} is used
 */
public class WeightedList<T> {

	private int totalWeight = 0;
	private final Map<T, Integer> values;

	public WeightedList() {
		values = new HashMap<T, Integer>();
	}

	public void add(int weight, T value) {
		if (weight <= 0) {
			throw new IllegalArgumentException("weight has to be greater than 0!");
		}
		totalWeight += weight;
		values.put(value, weight);
	}

	public int getTotalWeight() {
		return totalWeight;
	}

	/**
	 * creates a copy of this instance
	 */
	public WeightedList<T> createCopy() {
		WeightedList<T> out = new WeightedList<T>();
		out.values.putAll(values);
		out.totalWeight = totalWeight;
		return out;
	}

	public Iterator<Map.Entry<T, Integer>> entryIterator() {
		return new EntryIterator();
	}

	public int size() {
		return values.size();
	}

	/**
	 * @return a value which totalWeight is more of equal to the given parameter (ceil)
	 */
	public T get(int value) {
		if(value > totalWeight) {
			throw new IllegalArgumentException("the given value has to be less that the total weight");
		}
		int remainingWeight = value;
		for(Map.Entry<T, Integer> entry : values.entrySet()) {
			remainingWeight -= entry.getValue();
			if (remainingWeight <= 0) {
				return entry.getKey();
			}
		}
		throw new IllegalStateException("the sum of all weights is not equal to the calculated totalWeight!");
	}

	/**
	 * get a random value of this list (taking weight into consideration)
	 *
	 * @param random used {@link Random} instance
	 *
	 * @return a random value, null if the list is empty
	 */
	public T getRandomValue(Random random) {
		if(size() == 0) {
			return null;
		}
		return get(random.nextInt(getTotalWeight()));
	}

	/**
	 * creates a new {@link WeightedList} containing all elements of this list, that are validated by the {@link net.brenig.pixelescape.game.worldgen.WeightedList.Filter}
	 */
	public WeightedList<T> createFilteredList(Filter<T> filter) {
		WeightedList<T> list = new WeightedList<T>();
		for (Map.Entry<T, Integer> entry : values.entrySet()) {
			if(filter.isValid(entry.getKey())) {
				list.add(entry.getValue(), entry.getKey());
			}
		}
		return list;
	}

	/**
	 * returns a random value from this list, but filtered<br/>
	 * same as calling {@code createFilteredList(filter).getRandomValue(random);}
	 *
	 * @param random used {@link Random} instance
	 * @param filter filter do exclude some values
	 */
	public T getRandomValueWithFilter(Random random, Filter<T> filter) {
		return createFilteredList(filter).getRandomValue(random);
	}



	private class EntryIterator implements Iterator<Map.Entry<T, Integer>> {

		private Iterator<Map.Entry<T, Integer>> parent = values.entrySet().iterator();
		private Map.Entry<T, Integer> current;

		@Override
		public boolean hasNext() {
			return parent.hasNext();
		}

		@Override
		public Map.Entry<T, Integer> next() {
			current = parent.next();
			return current;
		}

		@Override
		public void remove() {
			parent.remove();
			totalWeight -= current.getValue();
		}
	}

	public interface Filter<T> {
		boolean isValid(T value);
	}
}
