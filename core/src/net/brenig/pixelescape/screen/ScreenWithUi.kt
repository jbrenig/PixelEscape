package net.brenig.pixelescape.screen

import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import de.golfgl.gdxgamesvcs.IGameServiceListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.render.ui.general.PixelDialog
import net.brenig.pixelescape.render.ui.general.StageManager

/**
 *
 */
abstract class ScreenWithUi(game: PixelEscape) : PixelScreen(game) {
    protected open val uiStage: StageManager = StageManager(game.renderManager)

    override fun gsShowErrorToUser(et: IGameServiceListener.GsErrorType?, msg: String?, t: Throwable?) {
        super.gsShowErrorToUser(et, msg, t)
        val d = PixelDialog("Error", game.skin)
        d.prefWidth = uiStage.stageViewport.worldWidth * 0.8f
        d.width = uiStage.stageViewport.worldWidth * 0.8f
        d.isMovable = false
        d.label(et.toString() + ": " + msg)
        val btnYes = TextButton("Ok", game.skin)
        btnYes.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                d.hide()
            }
        })
        d.button(btnYes)
        d.init()
        d.show(uiStage.uiStage)
    }
}