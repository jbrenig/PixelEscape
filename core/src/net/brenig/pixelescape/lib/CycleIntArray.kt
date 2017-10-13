package net.brenig.pixelescape.lib

/**
 * int array that is arranged in a Ring<br></br>
 * when new elements get added the oldest elements get lost
 */
class CycleIntArray(size: Int, initValue: Int) {

    private val data: IntArray
    private var index: Int = 0

    val newest: Int
        get() = data[index]

    val oldest: Int
        get() = data[(index + 1) % data.size]


    init {
        if (size < 1) {
            throw IllegalArgumentException("The specified size has to be 1 or greater!")
        }
        data = IntArray(size)

        fill(initValue)
        index = size - 1
    }

    private fun convertToLocalIndex(globalIndex: Int): Int {
        return (index + globalIndex) % data.size
    }

    private fun updateIndexBounds() {
        index = index % data.size
    }

    /**
     * returns the object at the given index
     * an index of 0 returns the oldest object, an index of size - 1 the newest
     */
    operator fun get(index: Int): Int {
        return data[convertToLocalIndex(index)]
    }

    /**
     * @return the object at the given index reversed (--> index of 0 returns the newest object, an index of size-1 the oldest)
     * @see .get
     */
    fun getFromNewest(index: Int): Int {
        return get(data.size - index - 1)
    }

    fun add(element: Int) {
        index++
        updateIndexBounds()
        data[index] = element
    }

    operator fun set(index: Int, element: Int) {
        data[convertToLocalIndex(index)] = element
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

    fun fill(value: Int) {
        for (i in data.indices) {
            data[i] = value
        }
    }
}
