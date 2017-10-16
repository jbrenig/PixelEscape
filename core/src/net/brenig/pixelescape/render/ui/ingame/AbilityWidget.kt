package net.brenig.pixelescape.render.ui.ingame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.lib.utils.Utils
import net.brenig.pixelescape.screen.GameScreen

/**
 * Gui Button that triggers use of the current player Ability
 */
class AbilityWidget : Button {


    private val gameScreen: GameScreen
    private lateinit var player: EntityPlayer

    private var animCounter = 0f

    constructor(skin: Skin, style: String, player: EntityPlayer, gameScreen: GameScreen) : this(skin.get<AbilityButtonStyle>(style, AbilityButtonStyle::class.java), player, gameScreen)

    constructor(skin: Skin, player: EntityPlayer, gameScreen: GameScreen) : this(skin.get<AbilityButtonStyle>(AbilityButtonStyle::class.java), player, gameScreen)

    constructor(style: AbilityButtonStyle, player: EntityPlayer, gameScreen: GameScreen) : super(style) {
        this.gameScreen = gameScreen
        setPlayer(player)
        initialize()
    }

    constructor(player: EntityPlayer, gameScreen: GameScreen) : super() {
        this.gameScreen = gameScreen
        setPlayer(player)
        initialize()
    }

    /**
     * initialized click listener etc.
     */
    private fun initialize() {
        addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                if (!gameScreen.isGamePaused && player.hasAbility() && player.cooldownRemaining == 0f) {
                    player.useAbility()
                }
            }
        })
    }

    /**
     * sets the player that has the ability
     */
    fun setPlayer(player: EntityPlayer) {
        this.player = player
    }

    override fun draw(batch: Batch, parentAlpha: Float) {
        super.draw(batch, parentAlpha)
        if (player.hasAbility()) {
            val itemFrame = width * item_frame_border
            val abilityIcon = player.currentAbility!!.getDrawable(gameScreen.game.gameAssets)
            abilityIcon.draw(batch, x + itemFrame, y + itemFrame, width - itemFrame * 2, height - itemFrame * 2)
            if (player.cooldownRemaining != 0f) {
                animCounter = ANIM_DURATION
                gameScreen.game.renderManager.setColor(0.7f, 0.7f, 1f, 0.4f)
                gameScreen.game.renderManager.rect(batch, x + itemFrame, y + itemFrame, width - itemFrame * 2, (height - itemFrame * 2) * player.cooldownRemainingScaled)
            } else if (animCounter > 0) {
                animCounter -= Gdx.graphics.deltaTime
                val alpha = Utils.easeInAndOut(animCounter, ANIM_DURATION) * 0.7f
                gameScreen.game.renderManager.setColor(1f, 1f, 1f, alpha)
                gameScreen.game.renderManager.rect(batch, x + itemFrame, y + itemFrame, width - itemFrame * 2, height - itemFrame * 2)
            }
        }
    }

    class AbilityButtonStyle : Button.ButtonStyle {

        constructor()

        constructor(up: Drawable, down: Drawable, checked: Drawable) : super(up, down, checked)

        constructor(style: AbilityButtonStyle) : super(style)
    }

    companion object {
        private const val ANIM_DURATION = 0.5f

        private const val item_frame_border = 0.21875f
    }
}
