package net.brenig.pixelescape.render.ui.ingame

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.lib.utils.AnimationUtils
import net.brenig.pixelescape.render.PixelTextureRegion
import net.brenig.pixelescape.screen.GameScreen
import kotlin.math.min

/**
 * Gui Button that triggers use of the current player Ability
 */
class AbilityWidget : Button {


    private val gameScreen: GameScreen
    private val player: EntityPlayer


    private var animCounter = 0f

    constructor(skin: Skin, style: String, player: EntityPlayer, gameScreen: GameScreen) : this(skin.get<AbilityButtonStyle>(style, AbilityButtonStyle::class.java), player, gameScreen)

    constructor(skin: Skin, player: EntityPlayer, gameScreen: GameScreen) : this(skin.get<AbilityButtonStyle>(AbilityButtonStyle::class.java), player, gameScreen)

    constructor(style: AbilityButtonStyle, player: EntityPlayer, gameScreen: GameScreen) : super(style) {
        this.gameScreen = gameScreen
        this.player = player
        initialize()
    }

    /**
     * initialized click listener etc.
     */
    private fun initialize() {
        addListener(object : ClickListener() {
            override fun touchDown(event: InputEvent?, x: Float, y: Float, pointer: Int, button: Int): Boolean {
                if (!gameScreen.isGamePaused && player.hasAbility() && player.cooldownRemaining == 0f) {
                    player.useAbility()
                }
                return super.touchDown(event, x, y, pointer, button)
            }
        })
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
                val alpha = AnimationUtils.easeInAndOut(animCounter, ANIM_DURATION) * 0.7f
                gameScreen.game.renderManager.setColor(1f, 1f, 1f, alpha)
                gameScreen.game.renderManager.rect(batch, x + itemFrame, y + itemFrame, width - itemFrame * 2, height - itemFrame * 2)
            }

            if (player.remainingAbilityUses > 0) {
                val scale = min(player.remainingAbilityUses, 5).toFloat() / 5F
                style.usesBar?.draw(batch, x + charge_bar_x_pos * width, y + charge_bar_y_pos * height,
                        width * charge_bar_width * scale, height * charge_bar_height,
                        0, 0, (scale * charge_bar_texture_width.toFloat()).toInt(), charge_bar_texture_height)
            }
        }
    }
    override fun getStyle(): AbilityButtonStyle {
        return super.getStyle() as AbilityButtonStyle
    }

    class AbilityButtonStyle : Button.ButtonStyle {

        var usesBar: PixelTextureRegion? = null

        constructor() : super()

        constructor(up: Drawable, down: Drawable, checked: Drawable, usesBar: PixelTextureRegion?) : super(up, down, checked) {
            this.usesBar = usesBar
        }

        constructor(style: AbilityButtonStyle) : super(style)
    }

    companion object {
        private const val ANIM_DURATION = 0.5f

        private const val item_frame_border = 0.21875f
        private const val charge_bar_x_pos = 0.15625f
        private const val charge_bar_y_pos = 0.09375f
        private const val charge_bar_width = 0.625f
        private const val charge_bar_height = 0.0625f
        private const val charge_bar_texture_width = 20
        private const val charge_bar_texture_height = 2
    }
}
