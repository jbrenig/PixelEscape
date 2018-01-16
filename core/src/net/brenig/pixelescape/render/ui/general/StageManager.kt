package net.brenig.pixelescape.render.ui.general

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Cell
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.Viewport
import net.brenig.pixelescape.game.data.GameDebugSettings
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.render.GameRenderManager

/**
 * Manager that includes a rootTable to arrange ui Elements conveniently<br></br>
 * provides common methods to organize root layouts
 */
open class StageManager(view: Viewport) {

    val uiStage: Stage = Stage(view)
    /**
     * @return the rootTable for further modification
     */
    val rootTable: Table

    val inputProcessor: InputProcessor
        get() = uiStage

    val stageViewport: Viewport
        get() = uiStage.viewport

    /**
     * default stagemanager using a [com.badlogic.gdx.utils.viewport.ExtendViewport]
     *
     * @param renderManager game rendermanager
     */
    constructor(renderManager: GameRenderManager) : this(ExtendViewport(Reference.TARGET_RESOLUTION_X.toFloat(), Reference.TARGET_RESOLUTION_Y.toFloat(), renderManager.camera))

    init {
        uiStage.setDebugAll(GameDebugSettings["DEBUG_UI"])

        rootTable = Table()
        rootTable.setFillParent(true)
        uiStage.addActor(rootTable)
    }

    /**
     * act-method
     *
     * @see Stage.act
     */
    fun act(delta: Float) {
        uiStage.act(delta)
    }

    /**
     * draw the ui
     *
     * @see Stage.draw
     */
    fun draw(renderManager: GameRenderManager) {
        renderManager.end()
        uiStage.draw()
    }

    /**
     * Special method to add actors directly to the stage<br></br>
     * note: Actors do not get added to the rootTable
     *
     * @see Stage.addActor
     */
    fun addActorToStage(actor: Actor) {
        uiStage.addActor(actor)
    }

    /**
     * adds an actor to the rootTable
     *
     * @see Table.add
     */
    fun add(actor: Actor): Cell<Actor> {
        return rootTable.add(actor)
    }

    /**
     * @see Stage.dispose
     */
    fun dispose() {
        uiStage.dispose()
    }

    /**
     * updates viewport
     *
     * @see Viewport.update
     */
    fun updateViewport(width: Int, height: Int, centerCamera: Boolean) {
        stageViewport.update(width, height, centerCamera)
    }

    /**
     * updates Viewport to screen bounds and centers camera
     *
     * @see Viewport.update
     */
    fun updateViewportToScreen() {
        updateViewport(Gdx.graphics.width, Gdx.graphics.height, true)
    }

    fun row() {
        rootTable.row()
    }

    /**
     * creates a new table that should be used for the main menu bar and adds it to the stage
     *
     * @return the table created
     */
    fun createHeadUiLayoutTable(): Table {
        val table = Table()
        table.defaults().height(Reference.GAME_UI_Y_SIZE_BUTTON_PANEL).fillY()
        table.pad(Reference.GAME_UI_Y_PADDING)
        table.top().left()
        add(table).height(Reference.GAME_UI_Y_SIZE.toFloat()).fillY()
        row()
        return table
    }


    /**
     * creates a new table that should be used for the current screen header and adds it (directly) to the stage
     *
     * @return the table created
     */
    fun createHeaderLayoutTable(): Table {
        val table = Table()
        table.setFillParent(true)
        table.setPosition(0f, 0f)
        table.top()
        addActorToStage(table)
        table.toBack()
        return table
    }

    /**
     * creates a new table that should be used for the main content and adds it to the stage
     *
     *
     * this should be created AFTER the head layout menu bar was added
     *
     * @return the table created
     */
    open fun createContentUiLayoutTable(): Table {
        val table = Table()
        table.pad(Reference.GAME_UI_Y_PADDING)
        table.top().left()
        add(table).expand().fill().center()
        return table
    }
}
