package net.brenig.pixelescape.game.player.movement

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.game.data.constants.Textures
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.render.WorldRenderer
import net.brenig.pixelescape.screen.GameScreen

/**
 * Movement Controller for Drag GameMode
 */
class DragMovementController : PlayerMovementController {

    private var acceleration: Float = 0f

    private var isTouched: Boolean = false
    private var touchX: Float = 0f
    private var touchY: Float = 0f

    @Suppress("LiftReturnOrAssignment")
    override fun updatePlayerMovement(game: PixelEscape, manager: InputManager, gameMode: GameMode, world: World, player: EntityPlayer, deltaTick: Float, yVelocityFactor: Float) {
        player.modifyXVelocity(gameMode.speedIncreaseFactor * deltaTick)
        player.modifyYVelocity(acceleration * deltaTick)
        if (isTouched) {
            if (!manager.isTouched) {
                //Confirm
                if (touchX > 0) {
                    acceleration = world.convertMouseYToScreenCoordinate(game.scaledMouseY) - touchY
                    if (acceleration > 0) {
                        acceleration = Math.max(0f, acceleration - DEAD_ZONE)
                    } else {
                        acceleration = Math.min(0f, acceleration + DEAD_ZONE)
                    }
                }
                isTouched = false
                touchX = Float.MIN_VALUE
                touchY = Float.MIN_VALUE
            }
        } else if (manager.isTouched) {
            touchX = game.scaledMouseX
            touchY = world.convertMouseYToScreenCoordinate(game.scaledMouseY)
            isTouched = true
        }
    }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float) {
        renderer.renderManager.begin()
        val ySize = Reference.PLAYER_ENTITY_SIZE * acceleration / 40
        renderer.renderManager.setColor(Color.GRAY)
        renderer.renderRect((world.player.xPosScreen - Reference.PATH_ENTITY_SIZE / 2).toFloat(), world.player.yPos + if (ySize > 0) Reference.PLAYER_ENTITY_SIZE / 2 else -Reference.PLAYER_ENTITY_SIZE / 2, Reference.PATH_ENTITY_SIZE.toFloat(), ySize)

    }

    override fun renderForeground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float) {
        renderer.renderManager.begin()
        if (isTouched && touchX > 0) {
            val color = if (world.convertMouseYToScreenCoordinate(game.scaledMouseY) < touchY) Color.RED else Color.BLACK
            renderer.renderManager.line(touchX, touchY, game.scaledMouseX, world.convertMouseYToScreenCoordinate(game.scaledMouseY), 2, color)
        }
    }

    override fun reset(mode: GameMode) {
        acceleration = 0f
        isTouched = false
        touchX = Float.MIN_VALUE
        touchY = Float.MIN_VALUE
    }

    override fun createTutorialWindow(skin: Skin, screen: GameScreen, maxWidth: Int, maxHeight: Int): Table {
        val maxLabelWidth = maxWidth - 60
        val table = Table(skin)
        table.setBackground(Textures.BUTTON_UP)
        table.defaults().padBottom(20f)

        val lbl = Label("Drag across the screen to change your height.", skin, StyleNames.LABEL_WHITE)
        lbl.setWrap(true)
        lbl.color = Color.GREEN
        lbl.pack()

        val lbl2 = Label("Be careful, you need to react fast!", skin, StyleNames.LABEL_WHITE)
        lbl2.setWrap(true)
        lbl2.color = Color.GREEN
        lbl2.pack()

        table.add(lbl).center().width(maxLabelWidth.toFloat())
        table.row()
        table.add(lbl2).center().width(maxLabelWidth.toFloat())
        return table
    }

    companion object {

        private val DEAD_ZONE = 4
    }
}
