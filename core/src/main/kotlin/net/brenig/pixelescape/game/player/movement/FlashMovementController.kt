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
 * Movement Controller for Flash GameMode
 */
class FlashMovementController : PlayerMovementController {

    override fun updatePlayerMovement(game: PixelEscape, manager: InputManager, gameMode: GameMode, world: World, player: EntityPlayer, deltaTick: Float, yVelocityFactor: Float) {
        if (manager.isTouched) {
            player.setYPosition(world.convertMouseYToWorldCoordinate(game.scaledMouseY))
        }
        player.modifyXVelocity(gameMode.speedIncreaseFactor * deltaTick)
    }

    override fun reset(mode: GameMode) {

    }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float) {
        if (world.screen.input.isTouched) {
            renderer.renderManager.begin()
            renderer.renderManager.setColor(Color.CYAN)
            renderer.renderRect(world.player.xPosScreen.toFloat(), world.player.yPos - Reference.PATH_ENTITY_SIZE / 2, game.scaledMouseX - world.player.xPosScreen, Reference.PATH_ENTITY_SIZE.toFloat())
            renderer.renderManager.setColor(Color.GRAY)
            renderer.renderRect(game.scaledMouseX - Reference.PLAYER_ENTITY_SIZE / 2, world.player.yPos - Reference.PLAYER_ENTITY_SIZE / 2, Reference.PLAYER_ENTITY_SIZE.toFloat(), Reference.PLAYER_ENTITY_SIZE.toFloat())
        }
    }

    override fun renderForeground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float) {

    }

    override fun createTutorialWindow(skin: Skin, screen: GameScreen, maxWidth: Int, maxHeight: Int): Table {
        val maxLabelWidth = maxWidth - 60

        val table = Table(skin)
        table.setBackground(Textures.BUTTON_UP)
        table.defaults().padBottom(20f)

        val lbl = Label("Touch the screen to move up or down.", skin, StyleNames.LABEL_WHITE)
        lbl.color = Color.BLUE
        lbl.setWrap(true)
        lbl.pack()

        val lbl2 = Label("You will always be at the position of your finger", skin, StyleNames.LABEL_WHITE)
        lbl2.setWrap(true)
        lbl2.color = Color.BLUE
        lbl2.pack()

        table.add(lbl).center().width(maxLabelWidth.toFloat())
        table.row()
        table.add(lbl2).center().width(maxLabelWidth.toFloat())
        return table
    }
}
