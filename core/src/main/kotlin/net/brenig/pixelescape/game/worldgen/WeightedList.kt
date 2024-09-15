package net.brenig.pixelescape.game.worldgen

import net.brenig.pixelescape.lib.FilteredElementProvider
import java.util.*

/**
 * Used to provide weighted randomness<br></br>
 * to provide this functionality an underlying [HashMap] is used
 */
class WeightedList<T> : FilteredElementProvider<T> {

    var totalWeight = 0
        private set
    private val values: MutableMap<T, Int> = HashMap()

    fun add(weight: Int, value: T) {
        if (weight <= 0) {
            throw IllegalArgumentException("weight has to be greater than 0!")
        }
        totalWeight += weight
        values.put(value, weight)
    }

    /**
     * creates a copy of this instance
     */
    fun createCopy(): WeightedList<T> {
        val out = WeightedList<T>()
        out.values.putAll(values)
        out.totalWeight = totalWeight
        return out
    }

    fun entryIterator(): Iterator<Map.Entry<T, Int>> {
        return EntryIterator()
    }

    fun size(): Int {
        return values.size
    }

    /**
     * @return a value which totalWeight is more of equal to the given parameter (ceil)
     */
    operator fun get(value: Int): T {
        if (value > totalWeight) {
            throw IllegalArgumentException("the given value has to be less that the total weight")
        }
        var remainingWeight = value
        for ((key, value1) in values) {
            remainingWeight -= value1
            if (remainingWeight <= 0) {
                return key
            }
        }
        throw IllegalStateException("the sum of all weights is not equal to the calculated totalWeight!")
    }

    /**
     * get a random value of this list (taking weight into consideration)
     *
     * @param rand used [Random] instance
     * @return a random value, null if the list is empty
     */
    override fun getRandomValue(rand: Random): T? {
        return if (size() == 0) {
            null
        } else get(rand.nextInt(totalWeight))
    }

    /**
     * creates a new [WeightedList] containing all elements of this list, that are validated by the [net.brenig.pixelescape.game.worldgen.WeightedList.Filter]
     */
    fun createFilteredList(filter: Filter<T>): WeightedList<T> {
        val list = WeightedList<T>()
        for ((key, value) in values) {
            if (filter.isValid(key)) {
                list.add(value, key)
            }
        }
        return list
    }

    /**
     * returns a random value from this list, but filtered<br></br>
     * same as calling `createFilteredList(filter).getRandomValue(random);`
     *
     * @param random used [Random] instance
     * @param filter filter do exclude some values
     */
    fun getRandomValueWithFilter(random: Random, filter: Filter<T>): T? {
        return createFilteredList(filter).getRandomValue(random)
    }


    private inner class EntryIterator : MutableIterator<Map.Entry<T, Int>> {

        private val parent = values.entries.iterator()
        private var current: Map.Entry<T, Int>? = null

        override fun hasNext(): Boolean {
            return parent.hasNext()
        }

        override fun next(): Map.Entry<T, Int> {
            current = parent.next()
            return current!!
        }

        override fun remove() {
            parent.remove()
            totalWeight -= current!!.value
        }
    }

    interface Filter<in T> {
        fun isValid(value: T): Boolean
    }
}
