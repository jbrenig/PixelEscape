package net.brenig.pixelescape.lib;

/**
 * Array that is arranged in a Ring<br></br>
 * when new elements get added the oldest elements get lost
 */
public class CycleArray<T> {

	private Object[] data;
	/**
	 * index of the last added object
	 */
	private int index;


	public CycleArray(int size) {
		if(size < 1) {
			throw new IllegalArgumentException("The specified size has to be 1 or greater!");
		}
		data = new Object[size];
		index = size - 1;
	}

	private int convertToLocalIndex(int globalIndex) {
		if(globalIndex < 0) {
			throw new IllegalArgumentException("The index cannot be lower than 0!");
		}
		return (index + globalIndex + 1) % data.length;
	}

	private void updateIndexBounds() {
		index = index % data.length;
	}

	/**
	 * returns the object at the given index
	 * an index of 0 returns the oldest object, an index of size - 1 the newest
	 */
	@SuppressWarnings("unchecked")
	public T get(int index) {
		return (T) data[convertToLocalIndex(index)];
	}

	public void add(T element) {
		index++;
		updateIndexBounds();
		data[index] = element;
	}

	public void set(int index, T element) {
		data[convertToLocalIndex(index)] = element;
	}

	@SuppressWarnings("unchecked")
	public T getNewest() {
		return (T) data[index];
	}

	@SuppressWarnings("unchecked")
	public T getOldest() {
		return get(0);
//		return (T) data[(index + 1) % data.length];
	}

	@SuppressWarnings("unchecked")
	public T getOldestNonNull() {
		int i = (index + 1) % data.length;
		while (data[i] == null) {
			i = (i + 1) % data.length;
			if(i == index) {
				break;
			}
		}
		return (T) data[i];
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

	/**
	 * realizes the array to the given length<br>
	 * fields get added at the end of the array, filled with null<br>
	 * when removing fields the oldest ones get removed first
	 * @param newWidth the new length
	 */
	public void resize(int newWidth) {
		if(newWidth > data.length) {
			Object[] oldData = data;
			data = new Object[newWidth];
			System.arraycopy(oldData, 0, data, 0, index + 1);
			if(oldData.length > index + 1) {
				System.arraycopy(oldData, index + 1, data, data.length - oldData.length + index + 1, oldData.length - index - 1);
			}
		} else if(newWidth < data.length) {
			Object[] oldData = data;
			int oldIndex = index;
			data = new Object[newWidth];
			index = newWidth - 1;
			int dif = newWidth - oldIndex - 1;
			if (dif <= 0) {
				System.arraycopy(oldData, 0 - dif, data, 0, oldIndex + dif + 1);
			} else {
				System.arraycopy(oldData, 0, data, dif - 1, oldIndex + 1);
				System.arraycopy(oldData, oldData.length - dif, data, 0, dif);
			}
		}
	}

	/**
	 * cycles the array amount steps forward, putting the oldest elements back to the front
	 * @param amount the amount of entries that get re-added to the front
	 */
	public void cycleForward(int amount) {
		index += amount;
		updateIndexBounds();
	}

	/**
	 * cycles the array 1 step forward, putting the oldest elements back to the front
	 */
	public void cycleForward() {
		index ++;
		updateIndexBounds();
	}

	public int size() {
		return data.length;
	}

	public void clear() {
		for(int i = 0; i < data.length; i++) {
			data[i] = null;
		}
	}
}
