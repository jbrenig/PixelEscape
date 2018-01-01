package net.brenig.pixelescape.screen

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
        val dialog = PixelDialog("Error", game.skin)
        dialog.text(et.toString() + ": " + msg)
        dialog.show(uiStage.uiStage)
    }
}