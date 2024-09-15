package net.brenig.pixelescape.render.ui.ingame

import com.badlogic.gdx.scenes.scene2d.ui.Table
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.render.ui.general.StageManager
import net.brenig.pixelescape.screen.GameScreen

/**
 * StageManager that manages ui elements for the GameScreen<br></br>
 * especially useful in Overlays
 */
class StageManagerGame(private val screen: GameScreen) : StageManager(screen.game.renderManager) {

    init {
        rootTable.setFillParent(false)
        rootTable.setPosition(0f, screen.uiPos.toFloat())
        rootTable.setSize(screen.world.worldWidth.toFloat(), (screen.world.worldHeight + Reference.GAME_UI_Y_SIZE).toFloat())
        rootTable.left().top()
    }

    fun updateStageToGameBounds(width: Int, height: Int) {
        //reset font size for measuring
        screen.game.renderManager.resetFontSizeToDefaultGuiSize()

        updateViewport(width, height, true)

        rootTable.setPosition(0f, screen.uiPos.toFloat())
        rootTable.setSize(screen.world.worldWidth.toFloat(), (screen.world.worldHeight + Reference.GAME_UI_Y_SIZE).toFloat())
        rootTable.invalidateHierarchy()
    }


    override fun createContentUiLayoutTable(): Table {
        val table = Table()
        table.pad(Reference.GAME_UI_Y_PADDING)
        table.top().left()
        add(table)
                .height(Reference.GAME_RESOLUTION_Y.toFloat())
                .maxHeight(Reference.GAME_RESOLUTION_Y.toFloat())
                .maxWidth(screen.world.worldWidth.toFloat())
                .fill()
                .expand()
        return table
    }
}
