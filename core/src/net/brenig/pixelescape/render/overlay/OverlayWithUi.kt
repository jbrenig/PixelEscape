package net.brenig.pixelescape.render.overlay

import com.badlogic.gdx.scenes.scene2d.ui.Skin
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.render.ui.ingame.StageManagerGame
import net.brenig.pixelescape.screen.GameScreen

/**
 *
 */
abstract class OverlayWithUi(screen: GameScreen) : Overlay(screen) {

    protected val stage: StageManagerGame = StageManagerGame(screen)

    protected val skin: Skin
        get() = screen.game.skin

    override fun render(delta: Float) {
        screen.game.font.data.setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)
        stage.act(delta)
        stage.draw(screen.game.renderManager)
    }

    override fun show() {
        screen.setOverlayInputProcessor(stage.inputProcessor)
    }

    override fun onResize(width: Int, height: Int) {
        stage.updateStageToGameBounds(width, height)
        super.onResize(width, height)
    }

    override fun dispose() {
        stage.dispose()
    }

    override fun shouldHideGameUI(): Boolean {
        return true
    }
}
