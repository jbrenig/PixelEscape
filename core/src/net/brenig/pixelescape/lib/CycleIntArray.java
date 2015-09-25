package net.brenig.pixelescape.lib;

/**
 * Created by Jonas Brenig on 02.08.2015.
 */
public class CycleIntArray {

	private int[] data;
	private int index;


	public CycleIntArray(int size, int initValue) {
		if(size < 1) {
			throw new IllegalArgumentException("The specified size has to be 1 or greater!");
		}
		data = new int[size];

		fill(initValue);
		index = size - 1;
	}

	private int convertToLocalIndex(int globalIndex) {
		return (index + globalIndex) % data.length;
	}

	private void updateIndexBounds() {
		index = index % data.length;
	}

	/**
	 * returns the object at the given index
	 * an index of 0 returns the oldest object, an index of size - 1 the newest
	 */
	public int get(int index) {
		return data[convertToLocalIndex(index)];
	}

	public int getFromNewest(int index) {
		return get(data.length - index - 1);
	}

	public void add(int element) {
		index++;
		updateIndexBounds();
		data[index] = element;
	}

	public void set(int index, int element) {
		data[convertToLocalIndex(index)] = element;
	}

	public int getNewest() {
		return data[index];
	}

	public int getOldest() {
		return data[(index + 1) % data.length];
	}

	@Override
	public String toString() {
		int iMax = data.length - 1;
		StringBuilder b = new StringBuilder();
		b.append('[');
		for (int i = 0; ; i++) {
			b.append(String.valueOf(data[convertToLocalIndex(i)]));
			if (i == iMax)
				return b.append(']').toString();
			b.append(", ");
		}
	}

	public void fill(int value) {
		for(int i = 0; i < data.length; i++) {
			data[i] = value;
		}
	}
}
