package net.brenig.pixelescape.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.lib.LangKeys
import net.brenig.pixelescape.lib.translate
import net.brenig.pixelescape.lib.utils.UiUtils
import net.brenig.pixelescape.lib.utils.horizontalSpacer
import net.brenig.pixelescape.render.ui.general.PixelDialog
import java.util.*

/**
 * Screen used to reset scores of different gamemodes
 */
class ResetScoreScreen(game: PixelEscape) : ScreenWithUi(game) {

    private val gamemodeCheckboxes: MutableMap<CheckBox, GameMode>
    private val resetAllCheckBox: CheckBox


    init {
        //Setting up stage
        game.renderManager.resetFontSize()

        createDefaultHeading(LangKeys.ResetScores.TITLE.translate(), 0.8f)

        val headerControlsLayout = uiStage.createHeadUiLayoutTable()
        val headControls = UiUtils.createDefaultUIHeadControls()
        headerControlsLayout.horizontalSpacer()
        headerControlsLayout.add(headControls)


        //configure main layout
        val uiLayout = uiStage.createContentUiLayoutTable()

        val contentLayout = Table()
        contentLayout.center()
        contentLayout.padBottom(30f).padTop(30f)

        //content (scrollpane)
        gamemodeCheckboxes = HashMap(game.gameConfig.availableGameModes.size)


        resetAllCheckBox = CheckBox(LangKeys.ResetScores.RESET_ALL.translate(), game.skin)
        resetAllCheckBox.isChecked = false
        resetAllCheckBox.imageCell.size(32f)
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
            chbx.imageCell.size(32f)
            chbx.addListener(chbxListener)
            //suppress events caused by setChecked()
            chbx.setProgrammaticChangeEvents(false)
            gamemodeCheckboxes.put(chbx, mode)
            contentLayout.add(chbx).left()
            val lbl = Label("" + game.userData.getHighScore(mode), game.skin)
            contentLayout.add(lbl)
            contentLayout.row()
        }

        //configure scollpane
        val pane = ScrollPane(contentLayout, game.skin)
        uiLayout.add(pane).expand().fillX().padTop(8f).padLeft(10f).padRight(10f).center().row()
        //set scroll focus
        uiStage.uiStage.scrollFocus = pane

        val buttonLayout = Table()

        //Back Button
        run {
            val btnBack = TextButton(LangKeys.BTN_BACK.translate(), game.skin)
            btnBack.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    game.screen = SettingsScreen(game)
                }
            })

            buttonLayout.add(btnBack).padTop(8f).padLeft(40f)
        }
        buttonLayout.horizontalSpacer()
        //OK Button
        run {
            val btnFinish = TextButton(LangKeys.BTN_FINISH.translate(), game.skin)
            btnFinish.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    val d = PixelDialog(LangKeys.ResetScores.DIALOG_TITLE.translate(), game.skin)
                    d.label(LangKeys.ResetScores.DIALOG_TEXT.translate())
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

            buttonLayout.add(btnFinish).padTop(8f).padRight(40f)
        }
        uiLayout.add(buttonLayout)
        game.renderManager.resetFontSize()
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
    }

    override fun render(delta: Float) {
        uiStage.act(delta)
        uiStage.draw(game.renderManager)
    }

    override fun resize(width: Int, height: Int) {
        uiStage.updateViewport(width, height, true)
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
