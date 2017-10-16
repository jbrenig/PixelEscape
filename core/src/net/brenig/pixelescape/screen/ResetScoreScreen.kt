package net.brenig.pixelescape.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.lib.utils.Utils
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer
import net.brenig.pixelescape.render.ui.general.PixelDialog
import net.brenig.pixelescape.render.ui.general.StageManager
import java.util.*

/**
 * Screen used to reset scores of different gamemodes
 */
class ResetScoreScreen(game: PixelEscape) : PixelScreen(game) {

    private val uiStage: StageManager = StageManager(game.renderManager)
    private val headLayout: Table
    private val contentLayout: Table
    private val pane: ScrollPane

    private val gamemodeCheckboxes: MutableMap<CheckBox, GameMode>
    private val resetAllCheckBox: CheckBox


    init {
        //Setting up stage

        game.renderManager.resetFontSize()

        //configure main layout
        val uiLayout = Table()
        uiLayout.setFillParent(true)
        uiLayout.setPosition(0f, 0f)
        uiLayout.center()
        uiLayout.padTop(30f).padBottom(20f)

        contentLayout = Table()
        contentLayout.center()
        contentLayout.padBottom(30f).padTop(30f)

        //Header
        val header = Label("Reset Score:", game.skin)
        header.setFontScale(1.2f)
        uiLayout.add(header)
        uiLayout.row()

        //Head controls
        uiStage.rootTable.top().right().pad(4f)
        headLayout = Utils.createDefaultUIHeadControls()
        uiStage.add(headLayout)

        //content (scrollpane)
        gamemodeCheckboxes = HashMap(game.gameConfig.availableGameModes.size)


        resetAllCheckBox = CheckBox("Reset ALL", game.skin)
        resetAllCheckBox.isChecked = false
        resetAllCheckBox.imageCell.padBottom(8f).padRight(10f).size(32f)
        resetAllCheckBox.label.setFontScale(0.7f)
        resetAllCheckBox.addListener(object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                for (chbx in gamemodeCheckboxes.keys) {
                    chbx.isChecked = resetAllCheckBox.isChecked
                }
            }
        })
        //suppress events caused by setChecked()
        resetAllCheckBox.setProgrammaticChangeEvents(false)
        contentLayout.add(resetAllCheckBox).padBottom(20f).left().row()


        val chbxListener = object : ChangeListener() {
            override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                for (chbx in gamemodeCheckboxes.keys) {
                    if (!chbx.isChecked) {
                        resetAllCheckBox.isChecked = false
                        return
                    }
                }
                resetAllCheckBox.isChecked = true
            }
        }

        for (mode in game.gameConfig.availableGameModes) {
            val chbx = CheckBox(mode.gameModeName, game.skin)
            chbx.isChecked = false
            chbx.imageCell.padBottom(8f).padRight(10f).size(32f)
            chbx.label.setFontScale(0.7f)
            chbx.addListener(chbxListener)
            //suppress events caused by setChecked()
            chbx.setProgrammaticChangeEvents(false)
            gamemodeCheckboxes.put(chbx, mode)
            contentLayout.add(chbx).left()
            val lbl = Label("" + game.userData.getHighScore(mode), game.skin)
            lbl.setFontScale(0.7f)
            contentLayout.add(lbl)
            contentLayout.row()
        }

        //configure scollpane
        pane = ScrollPane(contentLayout, game.skin)
        uiLayout.add(pane).expand().fillX().padTop(8f).padLeft(20f).padRight(20f).center().row()
        //set scroll focus
        uiStage.uiStage.scrollFocus = pane

        val buttonLayout = Table()

        //Back Button
        run {
            val btnBack = TextButton("Go Back", game.skin)
            btnBack.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    game.screen = SettingsScreen(game)
                }
            })

            buttonLayout.add(btnBack).padTop(8f).padLeft(20f)
        }
        buttonLayout.add(HorizontalSpacer())
        //OK Button
        run {
            val btnFinish = TextButton("Finish", game.skin)
            btnFinish.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    val d = PixelDialog("Reset Scores", game.skin)
                    d.label("Are you sure to reset scores?")
                    d.buttonYes(object : ClickListener() {
                        override fun clicked(event: InputEvent, x: Float, y: Float) {
                            apply()
                            game.screen = SettingsScreen(game)
                            d.hide()
                        }
                    })
                    d.buttonNo(object : ClickListener() {
                        override fun clicked(event: InputEvent, x: Float, y: Float) {
                            d.hide()
                        }
                    })
                    d.init()
                    d.show(uiStage.uiStage)
                }
            })

            buttonLayout.add(btnFinish).padTop(8f).padRight(20f)
        }
        uiLayout.add(buttonLayout)

        //Add main ui
        uiStage.addActorToStage(uiLayout)
    }

    private fun apply() {
        for ((key, value) in gamemodeCheckboxes) {
            if (key.isChecked) {
                game.userData.setHighScore(value, 0)
                game.userData.setTutorialSeen(value, false)
            }
        }
    }


    override fun show() {
        Gdx.input.inputProcessor = uiStage.inputProcessor
        game.renderManager.resetFontSize()
        uiStage.updateViewportToScreen()
        pane.invalidateHierarchy()
        headLayout.invalidateHierarchy()
    }

    override fun render(delta: Float) {
        uiStage.act(delta)
        uiStage.draw(game.renderManager)
    }

    override fun resize(width: Int, height: Int) {
        uiStage.updateViewport(width, height, true)
        contentLayout.invalidateHierarchy()
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
