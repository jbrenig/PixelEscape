package net.brenig.pixelescape.lib

import java.util.*

/**
 * provides an object based upon an instance of [Random]
 *
 * @see net.brenig.pixelescape.game.worldgen.WeightedList
 */
interface FilteredElementProvider<out T> {

    /**
     * get a random value of this provider (taking weight into consideration)
     *
     * @param rand used [Random] instance
     * @return a random value, null if no element was found
     */
    fun getRandomValue(rand: Random): T?

    class SingleElementProvider<out T>(private val element: T?) : FilteredElementProvider<T> {

        override fun getRandomValue(rand: Random): T? {
            return element
        }
    }
}
