package net.brenig.pixelescape.game.entity

import net.brenig.pixelescape.game.data.GameMode

abstract class EntityMoving : Entity() {

    protected open var xVel: Float = 0F
    protected open var yVel: Float = 0F

    protected fun checkMaxVelocity(gameMode: GameMode) {
        xVel = Math.min(xVel, gameMode.maxEntitySpeed)
        yVel = Math.min(yVel, gameMode.maxEntitySpeed)
    }

    fun setVelocity(xVel: Float, yVel: Float) {
        this.xVel = xVel
        this.yVel = yVel
    }

    /**
     * moves the entity
     *
     *
     * note: does note check velocity limits
     *
     * @param delta time passed
     */
    protected fun move(delta: Float) {
        xPos += xVel * delta
        yPos += yVel * delta
    }

    override fun reset() {
        super.reset()
        xVel = 0f
        yVel = 0f
    }
}
