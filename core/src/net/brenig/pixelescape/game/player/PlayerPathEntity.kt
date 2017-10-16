package net.brenig.pixelescape.game.player

import net.brenig.pixelescape.game.entity.IMovingEntity
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.data.constants.Reference

/**
 * Player path entity
 */
class PlayerPathEntity(yPos: Float, xPosScreen: Int) : IMovingEntity {
    private var yVelocity = 0f
    override var yPos = (Reference.GAME_RESOLUTION_Y / 2).toFloat()
        private set
    var xPosScreen = 0

    val size: Int
        get() = Reference.PATH_ENTITY_SIZE

    val sizeRadius: Int
        get() = size / 2

    init {
        this.yPos = yPos
        this.xPosScreen = xPosScreen
    }

    fun update(e: IMovingEntity, delta: Float, playerEntity: EntityPlayer) {
        this.yPos += yVelocity * delta
        this.yVelocity = (e.yPos - this.yPos) * Reference.PATH_ENTITY_ACCELERATION_MOD * playerEntity.xVelocity
    }

    fun reset(yPos: Float, xPosScreen: Int) {
        this.yPos = yPos
        this.yVelocity = 0f
        this.xPosScreen = xPosScreen
    }
}
