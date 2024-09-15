package net.brenig.pixelescape.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameDebugSettings
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.lib.utils.UiUtils
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer

/**
 * Screen to adjust DEBUG features
 */
class DebugSettingsScreen(game: PixelEscape) : ScreenWithUi(game) {

    private val uiLayout: Table
    private val headerControls: Table
    private val pane: ScrollPane


    init {
        //Setting up stage

        game.renderManager.resetFontSize()

        //configure main layout
        uiLayout = Table()
        uiLayout.center()
        uiLayout.padBottom(30f).padTop(30f)

        uiLayout.add(createDebugSettingCheckBox("Show FPS", "SHOW_FPS")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Pause when window looses focus", "AUTO_PAUSE")).left().row()
        uiLayout.add(createDebugSettingCheckBox("show debug information", "DEBUG_MODE_COORDS")).left().row()
        uiLayout.add(createDebugSettingCheckBox("validate world gen", "DEBUG_WORLD_GEN_VALIDATE")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Debug screen bounds", "DEBUG_SCREEN_BOUNDS")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Debug UI", "DEBUG_UI")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Enable debug Logging", "DEBUG_LOGGING")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Enable Godmode", "DEBUG_GOD_MODE")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Show music Debug information", "DEBUG_MUSIC")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Enable Screen-Shake", "SCREEN_SHAKE")).left().row()
        uiLayout.add(createDebugSettingCheckBox("Enable Cheats", GameDebugSettings.ENABLE_CHEATS)).left().row()


        //Add ui elements to stage

        //Head controls

        val headerLayout = uiStage.createHeaderLayoutTable()
        val header = Label("Debug Settings", game.skin, StyleNames.LABEL_BIG)
        headerLayout.add(header).height(Reference.GAME_UI_Y_SIZE.toFloat()).center()


        val headerControlsLayout = uiStage.createHeadUiLayoutTable()
        headerControlsLayout.add(HorizontalSpacer())

        headerControls = UiUtils.createDefaultUIHeadControls()
        headerControlsLayout.add(headerControls)

        //Main Layout

        val mainLayoutTable = uiStage.createContentUiLayoutTable()

        //configure scollpane
        pane = ScrollPane(uiLayout, game.skin)
        mainLayoutTable.add(pane).expand().fill().center().padLeft(10f).padRight(10f).colspan(3).row()
        uiStage.uiStage.scrollFocus = pane

        //Back Button
        run {
            val btnBack = TextButton("Go Back", game.skin)
            btnBack.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    game.gameDebugSettings.saveToDisk()
                    game.screen = SettingsScreen(game)
                }
            })

            mainLayoutTable.add(btnBack).colspan(3).padTop(8f)
        }
    }

    private fun createDebugSettingCheckBox(text: String, property: String): CheckBox {
        val chbx = CheckBox(text, game.skin)
        chbx.isChecked = game.gameDebugSettings.getBoolean(property)
        chbx.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                game.gameDebugSettings.setBoolean(property, (actor as CheckBox).isChecked)
            }
        })
        chbx.imageCell.padRight(10f).size(32f)
        return chbx
    }

    override fun show() {
        Gdx.input.inputProcessor = uiStage.inputProcessor
        game.renderManager.resetFontSize()
        uiStage.updateViewportToScreen()
        //		uiLayout.invalidateHierarchy();
        pane.invalidateHierarchy()
        headerControls.invalidateHierarchy()
    }

    override fun render(delta: Float) {
        uiStage.act(delta)
        uiStage.draw(game.renderManager)
    }

    override fun resize(width: Int, height: Int) {
        uiStage.updateViewport(width, height, true)
        uiLayout.invalidateHierarchy()
        pane.invalidateHierarchy()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        uiStage.dispose()
    }
}
