package net.brenig.pixelescape.game.player.movement

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.Table
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.game.data.constants.Textures
import net.brenig.pixelescape.game.entity.impl.EntityPlayer
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.render.WorldRenderer
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer
import net.brenig.pixelescape.screen.GameScreen

/**
 * default implementation of a [PlayerMovementController], standard behaviour
 *
 * @see GameMode.CLASSIC
 */
class DefaultMovementController : PlayerMovementController {
    private var lastTouched = false

    override fun updatePlayerMovement(game: PixelEscape, manager: InputManager, gameMode: GameMode, world: World, player: EntityPlayer, deltaTick: Float, yVelocityFactor: Float) {
        if (manager.isTouched || manager.isSpaceDown) {
            if (!lastTouched) {
                player.modifyYVelocity(Reference.CLICK_ACCELERATION * yVelocityFactor)
                lastTouched = true
            } else {
                player.modifyYVelocity(Reference.TOUCH_ACCELERATION * deltaTick * yVelocityFactor)
                lastTouched = true
            }
        } else {
            player.modifyYVelocity(Reference.GRAVITY_ACCELERATION * deltaTick * yVelocityFactor)
            lastTouched = false
        }
        player.modifyXVelocity(gameMode.speedIncreaseFactor * deltaTick)
    }

    override fun reset(mode: GameMode) {
        lastTouched = false
    }

    override fun renderBackground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float) {

    }

    override fun renderForeground(game: PixelEscape, renderer: WorldRenderer, world: World, delta: Float) {

    }

    override fun createTutorialWindow(skin: Skin, screen: GameScreen, maxWidth: Int, maxHeight: Int): Table {
        val table = Table(skin)
        table.setBackground(Textures.BUTTON_UP)
        table.defaults().padBottom(20f)

        val lbl = Label("Touch the screen to move up.", skin, StyleNames.LABEL_WHITE)
        lbl.color = Color.GREEN
        val lbl2_1 = Label("Gravity will make you", skin, StyleNames.LABEL_WHITE)
        lbl2_1.color = Color.RED

        val crashLine = HorizontalGroup()

        val lbl2_2 = Label("CRASH", skin, StyleNames.LABEL_WHITE)
        lbl2_2.color = Color.RED
        lbl2_2.setFontScale(1.2f)

        val lbl2_3 = Label("otherwise!", skin, StyleNames.LABEL_WHITE)
        lbl2_3.color = Color.RED

        crashLine.addActor(lbl2_2)
        crashLine.addActor(HorizontalSpacer(20f, 20f, 20f))
        crashLine.addActor(lbl2_3)

        table.add(lbl).center()
        table.row()
        table.add(lbl2_1).center()
        table.row()
        table.add(crashLine).center()
        return table
    }
}
