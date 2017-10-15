package net.brenig.pixelescape.game


enum class CollisionType constructor(val collideRight: Boolean, val collideLeft: Boolean, val collideTop: Boolean, val collideBot: Boolean) {
    TERRAIN_TOP_RIGHT(true, false, true, false), TERRAIN_TOP_LEFT(false, true, true, false),
    TERRAIN_BOT_RIGHT(true, false, false, true), TERRAIN_BOT_LEFT(false, true, false, true),
    TERRAIN_TOP(false, false, true, false), TERRAIN_BOTTOM(false, false, true, false),
    TERRAIN_RIGHT(true, false, false, false), TERRAIN_LEFT(false, true, false, false),
    ENTITY(true), OTHER(true), NONE(false);

    constructor(collides: Boolean) : this(collides, collides, collides, collides)


    fun doesCollideRight(): Boolean {
        return collideRight
    }

    fun doesCollideLeft(): Boolean {
        return collideLeft
    }

    fun doesCollideTop(): Boolean {
        return collideTop
    }

    fun doesCollideBot(): Boolean {
        return collideBot
    }

    fun doesCollide(): Boolean {
        return collideBot || collideLeft || collideRight || collideTop
    }

    fun doesCollideHorizontally(): Boolean {
        return collideRight || collideLeft
    }

    fun doesCollideVertically(): Boolean {
        return collideBot || collideTop
    }
}
