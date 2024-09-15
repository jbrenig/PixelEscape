package net.brenig.pixelescape.lib.utils

/**
 *
 */
object AnimationUtils {
    fun easeOut(timePassed: Float, maxTime: Float, intensity: Int, target: Float): Float {
        return easeOut(timePassed, maxTime, intensity) * target
    }

    fun easeOut(timePassed: Float, maxTime: Float, intensity: Int = 3): Float {
        return if (timePassed > maxTime) {
            1f
        } else (1 - Math.pow((1 - timePassed / maxTime).toDouble(), intensity.toDouble())).toFloat()
    }

    fun easeInAndOut(timePassed: Float, maxTime: Float): Float {
        if (timePassed > maxTime) {
            return 1f
        }
        val timeProgress = timePassed / maxTime
        return if (timeProgress < 0.5) {
            2f * timeProgress * timeProgress
        } else {
            -1 + (4 - 2 * timeProgress) * timeProgress
        }
    }

    fun easeIn(timePassed: Float, maxTime: Float, intensity: Int = 2): Float {
        return if (timePassed > maxTime) {
            1f
        } else Math.pow((timePassed / maxTime).toDouble(), intensity.toDouble()).toFloat()
    }

    fun easeIn(timePassed: Float, maxTime: Float, intensity: Int, target: Float): Float {
        return easeIn(timePassed, maxTime, intensity) * target
    }
}