package net.brenig.pixelescape.lib

/**
 *
 */
class DisplayValue<V> {
    val displayName: String
    val value: V

    constructor(value: V, converter: (V) -> String) {
        this.value = value
        this.displayName = converter(value)
    }

    constructor(value: V, displayName: String) {
        this.value = value
        this.displayName = displayName
    }

    constructor(value: V) {
        this.value = value
        this.displayName = value.toString()
    }

    override fun toString(): String {
        return displayName
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DisplayValue<*>

        if (displayName != other.displayName) return false
        if (value != other.value) return false

        return true
    }

}