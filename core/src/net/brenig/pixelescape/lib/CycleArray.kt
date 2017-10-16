package net.brenig.pixelescape.lib

import java.util.*

@Suppress("UNCHECKED_CAST")
/**
 * Array that is arranged in a Ring<br></br>
 * when new elements get added the oldest elements get lost
 */
class CycleArray<T>(size: Int, private val constructor: (Int) -> Any) {

    private var data: Array<T>
    /**
     * index of the last added object
     */
    private var index: Int = 0
    private var modCount = 0

    val newest: T get() = data[index] as T

    val oldest: T get() = get(0)

    init {
        if (size < 1) {
            throw IllegalArgumentException("The specified size has to be 1 or greater!")
        }
        data = Array(size, constructor) as Array<T>
        index = size - 1
    }

    private fun convertToLocalIndex(globalIndex: Int): Int {
        if (globalIndex < 0) {
            throw IllegalArgumentException("The index cannot be lower than 0!")
        }
        return (index + globalIndex + 1) % data.size
    }

    private fun updateIndexBounds() {
        index %= data.size
    }

    /**
     * returns the object at the given index
     * an index of 0 returns the oldest object, an index of size - 1 the newest
     */
    operator fun get(index: Int): T {
        return data[convertToLocalIndex(index)]
    }


    fun add(element: T) {
        index++
        updateIndexBounds()
        data[index] = element
        modCount++
    }

    operator fun set(index: Int, element: T) {
        data[convertToLocalIndex(index)] = element
        modCount++
    }

    override fun toString(): String {
        val iMax = data.size - 1
        val b = StringBuilder()
        b.append('[')
        var i = 0
        while (true) {
            b.append(data[convertToLocalIndex(i)].toString())
            if (i == iMax)
                return b.append(']').toString()
            b.append(", ")
            i++
        }
    }

    /**
     * resizes the array to the given length<br></br>
     * fields get added at the end of the array, filled with null<br></br>
     * when removing fields the oldest ones get removed first
     *
     * @param newWidth the new length
     */
    fun resize(newWidth: Int) {
        if (newWidth > data.size) {
            val oldData = data
            data = Array(newWidth, constructor) as Array<T>
            System.arraycopy(oldData, 0, data, 0, index + 1)
            if (oldData.size > index + 1) {
                val oldDataRemains = oldData.size - index - 1
                System.arraycopy(oldData, index + 1, data, data.size - oldDataRemains, oldDataRemains)
            }
        } else if (newWidth < data.size) {
            val oldData = data
            val oldIndex = index
            data = Array(newWidth, constructor) as Array<T>
            index = newWidth - 1
            val dif = newWidth - oldIndex - 1
            if (dif <= 0) {
                System.arraycopy(oldData, 0 - dif, data, 0, oldIndex + dif + 1)
            } else {
                System.arraycopy(oldData, 0, data, dif - 1, oldIndex + 1)
                System.arraycopy(oldData, oldData.size - dif, data, 0, dif)
            }
        }
        modCount++
    }

    /**
     * cycles the array amount steps forward, putting the oldest elements back to the front
     *
     * @param amount the amount of entries that get re-added to the front
     */
    fun cycleForward(amount: Int) {
        index += amount
        updateIndexBounds()
        modCount++
    }

    /**
     * cycles the array 1 step forward, putting the oldest elements back to the front
     */
    fun cycleForward() {
        index++
        updateIndexBounds()
        modCount++
    }

    fun size(): Int {
        return data.size
    }

    fun refill() {
        for (i in data.indices) {
            data[i] = constructor as T
        }
    }

    operator fun iterator(): Iterator<*> {
        return Itr()
    }

    /**
     * An optimized version of AbstractList.Itr
     */
    private inner class Itr : Iterator<T> {
        internal var cursor: Int = 0       // index of next element to return (unshifted / relative)
        internal var lastRet = -1 // index of last element returned; -1 if no such
        internal var expectedModCount = modCount

        override fun hasNext(): Boolean {
            return cursor != data.size
        }

        override fun next(): T {
            checkForComodification()
            val i = cursor
            if (i >= data.size)
                throw NoSuchElementException()
            val elementData = data
            if (i >= elementData.size)
                throw ConcurrentModificationException()
            cursor = i + 1
            lastRet = i
            return elementData[convertToLocalIndex(i)]
        }

        internal fun checkForComodification() {
            if (modCount != expectedModCount)
                throw ConcurrentModificationException()
        }
    }
}
