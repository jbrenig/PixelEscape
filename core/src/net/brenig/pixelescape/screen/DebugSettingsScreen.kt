package net.brenig.pixelescape.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.lib.Utils
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer
import net.brenig.pixelescape.render.ui.general.StageManager

/**
 * Screen to adjust DEBUG features
 */
class DebugSettingsScreen(game: PixelEscape) : PixelScreen(game) {

    private val uiStage: StageManager = StageManager(game.renderManager)
    private val uiLayout: Table
    private val headLayout: Table
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


        //Add ui elements to stage

        //Head controls
        uiStage.rootTable.top().right().pad(4f)
        headLayout = Utils.createDefaultUIHeadControls()

        val header = Label("DEBUG Settings", game.skin)
        header.setFontScale(1.2f)

        uiStage.add(HorizontalSpacer()).width(headLayout.prefWidth)
        uiStage.add(header).pad(8f).fillX()
        uiStage.add(headLayout)
        uiStage.row()


        //Main Layout

        //configure scollpane
        pane = ScrollPane(uiLayout, game.skin)
        //		pane.setFillParent(true);
        //		uiStage.addActorToStage(pane);
        uiStage.add(pane).expand().fillX().padTop(8f).padLeft(20f).padRight(20f).center().colspan(3).row()
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

            uiStage.add(btnBack).colspan(3).padTop(8f)
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
        chbx.imageCell.padBottom(8f).padRight(10f).size(32f)
        chbx.label.setFontScale(0.7f)
        return chbx
    }

    override fun show() {
        Gdx.input.inputProcessor = uiStage.inputProcessor
        game.renderManager.resetFontSize()
        uiStage.updateViewportToScreen()
        //		uiLayout.invalidateHierarchy();
        pane.invalidateHierarchy()
        headLayout.invalidateHierarchy()
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