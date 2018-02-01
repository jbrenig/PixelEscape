package net.brenig.pixelescape.game.entity.impl

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.CollisionType
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameDebugSettings
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.entity.Entity
import net.brenig.pixelescape.game.entity.IMovingEntity
import net.brenig.pixelescape.game.entity.impl.particle.EntityCrashParticle
import net.brenig.pixelescape.game.player.PlayerPathEntity
import net.brenig.pixelescape.game.player.abliity.Ability
import net.brenig.pixelescape.game.player.effects.StatusEffect
import net.brenig.pixelescape.game.player.movement.PlayerMovementController
import net.brenig.pixelescape.render.WorldRenderer
import java.util.*
import kotlin.math.PI

/**
 * the Player
 */
class EntityPlayer(world: World, gameMode: GameMode) : Entity(), IMovingEntity {
    val movementController: PlayerMovementController = gameMode.createPlayerMovementController()

    /**
     * sets new x velocity
     */
    var xVelocity: Float = 0.toFloat()
    /**
     * sets new y velocity
     */
    var yVelocity: Float = 0.toFloat()

    var xVelocityModifier = 0f
        private set

    private var yVelocityFactor = 1f

    var xPosScreen: Int = 0
        set(xPosScreen) {
            field = xPosScreen
            for (i in pathEntities.indices) {
                pathEntities[i].xPosScreen = xPosScreen - Reference.PATH_ENTITY_OFFSET * (i + 1)
            }
        }

    var extraLives: Int = 0

    private var immortal = 0f

    var bonusScore: Int = 0
        private set

    val pathEntities: Array<net.brenig.pixelescape.game.player.PlayerPathEntity> = Array(4, { PlayerPathEntity(0F, 0) })

    override var isDead = false

    var currentAbility: Ability? = null
        private set
    private var remainingAbilityUses: Int = 0

    var cooldownRemaining = 0f
        private set

    private val effects = HashSet<StatusEffect>()

    override val minX: Float
        get() = xPos - RADIUS

    override val minY: Float
        get() = yPos - RADIUS

    override val maxX: Float
        get() = xPos + RADIUS

    override val maxY: Float
        get() = yPos + RADIUS

    private var realXPos: Float = 0F

    /**
     * @return player global x coordinate
     */
    override var xPos: Float
        get() = realXPos + xPosScreen
        set(value) { realXPos = value}


    val score: Int
        get() = realXPos.toInt() + bonusScore

    /**
     * @return progress the player made (distance travelled)
     */
    val progress: Float
        get() = realXPos

    val playerSize: Int
        get() = Reference.PLAYER_ENTITY_SIZE

    val playerSizeRadius: Int
        get() = playerSize / 2

    val cooldownRemainingScaled: Float
        get() = if (currentAbility != null) {
            cooldownRemaining / currentAbility!!.cooldown
        } else 0f

    val statusEffects: Collection<StatusEffect>
        get() = effects

    init {
        reset(gameMode)
        super.world = world
    }

    override fun update(delta: Float, inputManager: InputManager, gameMode: GameMode): Boolean {
        realXPos += delta * (xVelocity + xVelocityModifier)
        if (!GameDebugSettings["DEBUG_GOD_MODE"]) {
            yPos += delta * yVelocity
            //make sure player doesn't leave the screen
            if (yPos < playerSizeRadius) {
                yPos = playerSizeRadius.toFloat()
                yVelocity = 0f
            } else if (yPos > world.worldHeight - playerSizeRadius) {
                yPos = (world.worldHeight - playerSizeRadius).toFloat()
                yVelocity = 0f
            }
        }

        //trigger ability by key press
        if (hasAbility()) {
            if (cooldownRemaining == 0f) {
                if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    useAbility()
                }
            } else {
                cooldownRemaining = Math.max(0f, cooldownRemaining - delta)
            }
        }

        //speed update
        movementController.updatePlayerMovement(world.screen.game, inputManager, gameMode, world, this, delta, yVelocityFactor)

        xVelocity = Math.min(gameMode.maxEntitySpeed, xVelocity)
        yVelocity = Math.min(gameMode.maxEntitySpeed, yVelocity)

        //update trail
        for (i in pathEntities.indices) {
            if (i == 0) {
                pathEntities[i].update(this, delta, this)
            } else {
                pathEntities[i].update(pathEntities[i - 1], delta, this)
            }
        }
        //update effects
        val iterEffect = effects.iterator()
        while (iterEffect.hasNext()) {
            val effect = iterEffect.next()
            effect.update(delta)
            if (!effect.effectActive()) {
                effect.onEffectRemove(this)
                iterEffect.remove()
            }
        }
        if (immortal <= 0) {
            if (!GameDebugSettings["DEBUG_GOD_MODE"] && collide()) {
                return true
            }
        } else {
            immortal -= delta
        }
        return false
    }

    /**
     * use the current ability of the player
     */
    fun useAbility() {
        if (currentAbility == null) return
        if (currentAbility!!.onActivate(world.screen, world, this)) {
            remainingAbilityUses--
            if (remainingAbilityUses == 0) {
                currentAbility = null
                cooldownRemaining = 0f
            } else {
                cooldownRemaining = currentAbility!!.cooldown
            }
        }
    }

    /**
     * resets this player entity to starting position
     */
    fun reset(gameMode: GameMode) {
        reviveAfterCrash()

        movementController.reset(gameMode)

        realXPos = 0f
        xPos = 0f
        bonusScore = 0
        xVelocity = gameMode.startingSpeed
        xVelocityModifier = 0f

        isDead = false
        extraLives = gameMode.extraLives

        currentAbility = gameMode.startingAbility
        remainingAbilityUses = gameMode.startingAbilityUses
        cooldownRemaining = 0f

        for (effect in effects) {
            effect.onEffectRemove(this)
        }
        effects.clear()
    }

    /**
     * resets player to start in the y-center of the screen<br></br>
     * used for reviving player after crashing (when he still has a life left)
     */
    fun reviveAfterCrash() {
        yPos = (Reference.GAME_RESOLUTION_Y / 2).toFloat()
        yVelocity = 0f
        for (i in pathEntities.indices) {
            pathEntities[i].reset(yPos, this.xPosScreen - Reference.PATH_ENTITY_OFFSET * (i + 1))
        }
    }

    /**
     * adds the given value to the xVelocity ignoring maximum speed limit
     *
     * a negative value will decrease player speed
     */
    fun addXVelocityModifier(xVelocityModifier: Float) {
        this.xVelocityModifier += xVelocityModifier
    }

    private fun collide(): Boolean {
        val col = world.doesAreaCollideWithWorld(xPos - playerSizeRadius, yPos - playerSizeRadius, xPos + playerSizeRadius, yPos + playerSizeRadius)
        if (col !== CollisionType.NONE) {
            var collide = true
            @Suppress("LoopToCallChain")
            for (effect in effects) {
                if (!effect.onPlayerCollide()) {
                    collide = false
                }
            }
            return collide && onPlayerCollide(col)
        }
        return false
    }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        if (this.isDead) {
            return
        }
        renderStatusEffectTimes(game, renderer)
        movementController.renderBackground(game, renderer, world, delta)
    }

    private fun renderStatusEffectTimes(game: PixelEscape, renderer: WorldRenderer) {
        val thickness = 2
        for ((index, effect) in statusEffects.sortedBy(StatusEffect::scaledTime).reversed().withIndex()) {
            effect.updateRenderColor(game.renderManager)
            renderer.renderRectangularCircle(xPosScreen.toFloat(), yPos, (this.playerSizeRadius + index * thickness).toFloat(), thickness.toFloat(), (2 * PI * effect.scaledTime).toFloat())
        }
    }

    override fun render(game: PixelEscape, renderer: WorldRenderer, gameMode: GameMode, delta: Float) {
        if (this.isDead) {
            return
        }

        game.renderManager.begin()

        // Draw Background color
        if (immortal % 1f < 0.5f) {
            game.renderManager.setColor(0f, 0f, 0f, 1f)
        } else {
            game.renderManager.setColor(Color.LIGHT_GRAY)
        }
        renderer.renderRect((this.xPosScreen - this.playerSize / 2).toFloat(), this.yPos - this.playerSize / 2, this.playerSize.toFloat(), this.playerSize.toFloat())

        for (e in this.pathEntities) {
            renderer.renderRect((e.xPosScreen - e.sizeRadius).toFloat(), e.yPos - e.sizeRadius, e.size.toFloat(), e.size.toFloat())
        }

        for (effect in effects) {
            effect.render(game, renderer, this, delta)
        }

        movementController.renderForeground(game, renderer, world, delta)
    }

    /**
     * makes player immortal
     *
     * @param time time in seconds to remain immortal
     */
    fun setImmortal(time: Float) {
        immortal = time
    }

    /**
     * sets the currently available ability, amount of uses will increase, should the player already have this ability
     *
     * @param ability new ability
     * @param uses    amount of times the player can use this ability (-1 for unlimited uses)
     */
    fun addAbility(ability: Ability, uses: Int) {
        if (this.currentAbility === ability) {
            if (uses > 0) {
                this.remainingAbilityUses += uses
            } else {
                this.remainingAbilityUses = uses
            }
        } else {
            this.cooldownRemaining = 0f
            this.currentAbility = ability
            this.remainingAbilityUses = uses
        }
    }

    fun hasAbility(): Boolean {
        return currentAbility != null && remainingAbilityUses != 0
    }

    fun increaseXPos(x: Float) {
        realXPos += x
    }

    fun spawnPlayerEntityExplosion(random: Random) {
        for (i in 0..59) {
            val x = Math.sin(i.toDouble()).toFloat() + (random.nextFloat() - 0.5f)
            val y = Math.cos(i.toDouble()).toFloat() + (random.nextFloat() - 0.5f)
            val e = world.createEntity(EntityCrashParticle::class.java)
            e.setPosition(xPos - xVelocity * Gdx.graphics.deltaTime + x, yPos - yVelocity * Gdx.graphics.deltaTime + y)
            e.color = (if (world.screen.game.gameDebugSettings.getBoolean("PLAYER_EXPLOSION_RED")) Color.RED else Color.BLACK)
            val xVel = (x * 2 + (random.nextFloat() - 0.5f)) * 70 + xVelocity * 0.4f
            val yVel = (y * 2 + (random.nextFloat() - 0.5f)) * 70
            e.setVelocity(xVel, yVel)
            world.spawnEntity(e)
        }
    }

    /**
     * Gets called when player collides<br></br>
     * used to spawn explosion and other effects as well as reducing lives/showing gameover screen
     *
     * @param col   type of collision
     * @param world the world instance
     */
    private fun onPlayerCollide(col: CollisionType): Boolean {
        //Spawn particles
        val random = world.random
        spawnPlayerEntityExplosion(random)

        //apply screenshake
        //increase effect with higher score
        val scoreModifier = 1 - 1 / (xVelocity * 0.4f)
        val forceX = 0.5f + random.nextFloat() * 0.5f * scoreModifier
        val forceY = 0.5f + random.nextFloat() * 0.5f * scoreModifier
        //when colliding with Barricades, shake horizontally
        world.screen.worldRenderer.applyForceToScreen(if (col.doesCollideHorizontally()) forceX else 0F, if (col.doesCollideVertically()) forceY else 0F)

        //play sound
        if (world.screen.game.gameSettings.isSoundEnabled) {
            world.screen.game.gameAssets.playerCrashedSound.play(world.screen.game.gameSettings.soundVolume)
        }

        //explode life icon
        if (world.screen.gameMode.extraLives > 0) {
            //We have a live system (and therefor have a lives icon)
            val lifeX = world.convertScreenToWorldCoordinate((world.screen.game.gameSizeX - 36 * extraLives - 16).toFloat())
            val lifeY = (world.worldHeight - 28 + 16).toFloat()
            //Spawn crash particles
            for (i in 0..59) {
                val x = Math.sin(i.toDouble()).toFloat() + (random.nextFloat() - 0.5f)
                val y = Math.cos(i.toDouble()).toFloat() + (random.nextFloat() - 0.5f)
                val e = world.createEntity(EntityCrashParticle::class.java)
                e.setPosition(lifeX + x, lifeY + y)
                e.color = Color.RED
                e.collideTop = false
                val xVel = (x * 2 + (random.nextFloat() - 0.5f)) * 70
                val yVel = (y * 2 + (random.nextFloat() - 0.5f)) * 70
                e.setVelocity(xVel, yVel)
                world.spawnEntity(e)
            }
        }
        //use lives
        @Suppress("LiftReturnOrAssignment")
        if (extraLives > 0) {
            extraLives--
            setImmortal(3f)
            reviveAfterCrash()
            return false
        } else {
            isDead = true
            world.screen.onGameOver()
            return true
        }
    }

    /**
     * adds a statuseffect to this player
     *
     * DO NOT CALL THIS WITHIN A [StatusEffect]!!!
     *
     * @throws java.util.ConcurrentModificationException when access while player is updating status effects
     */
    fun addEffect(effect: StatusEffect) {
        effects.add(effect)
        effect.onEffectAdded(this)
    }

    /**
     * adds a statuseffect to this player
     *
     * also removes existing instances of this effect (instances of the same class)
     *
     * DO NOT CALL THIS WITHIN A [StatusEffect]!!!
     *
     * @throws java.util.ConcurrentModificationException when access while player is updating status effects
     */
    fun addOrUpdateEffect(effect: StatusEffect) {
        val effectIterator = effects.iterator()
        val clazz = effect.javaClass
        while (effectIterator.hasNext()) {
            val old = effectIterator.next()
            if (clazz == old.javaClass) {
                old.onEffectRemove(this)
                effectIterator.remove()
            }
        }
        addEffect(effect)
    }

    /**
     * gets a statuseffect if possible
     *
     * DO NOT CALL THIS WITHIN A [StatusEffect]!!!
     *
     * @return found statuseffect, null if none was found
     * @throws java.util.ConcurrentModificationException when access while player is updating status effects
     */
    fun tryGetStatusEffect(clazz: Class<out StatusEffect>): StatusEffect? {
        return effects.firstOrNull { clazz == it.javaClass }
    }

    /**
     * additive modifies yVelocityFactor
     */
    fun addYVelocityFactor(change: Float) {
        this.yVelocityFactor += change
    }

    /**
     * sets yVelocityFactor
     */
    fun setYVelocityFactor(yVelocityFactor: Float) {
        this.yVelocityFactor = yVelocityFactor
    }

    /**
     * adds given value to the players x-velocity
     */
    fun modifyXVelocity(xVelocity: Float) {
        this.xVelocity += xVelocity
    }

    /**
     * adds given value to the players y-velocity
     */
    fun modifyYVelocity(yVelocity: Float) {
        this.yVelocity += yVelocity
    }

    /**
     * sets new y- position
     */
    fun setYPosition(pos: Float) {
        this.yPos = pos
    }

    fun addBonusScore(score: Int) {
        bonusScore += score
    }

    companion object {
        private const val SIZE = Reference.PLAYER_ENTITY_SIZE
        private const val RADIUS = SIZE / 2
    }
}
