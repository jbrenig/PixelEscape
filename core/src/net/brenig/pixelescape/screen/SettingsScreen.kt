package net.brenig.pixelescape.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Touchable
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.lib.Utils
import net.brenig.pixelescape.render.ui.general.PixelDialog
import net.brenig.pixelescape.render.ui.general.StageManager

/**
 * Screen that provides user settings<br></br>
 * Currently used for Music/Sound volume
 */
class SettingsScreen(game: PixelEscape) : PixelScreen(game) {

    private val uiStage: StageManager = StageManager(game.renderManager)
    private val uiLayout: Table
    private val headLayout: Table


    init {
        //Setting up stage

        game.renderManager.resetFontSize()

        //configure main layout
        uiLayout = Table()
        uiLayout.setFillParent(true)
        uiLayout.setPosition(0f, 0f)
        uiLayout.center()
        uiLayout.padTop(30f).padBottom(20f)

        val header = Label("Settings", game.skin)
        header.touchable = Touchable.disabled
        header.height = 150f
        header.setScale(2f)

        uiLayout.add(header).padBottom(60f)
        uiLayout.row()

        //Sound
        run {
            val soundControl = Table()
            soundControl.pad(4f)

            val txtSound = Label("Sound:", game.skin)
            txtSound.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)
            soundControl.add(txtSound).padRight(8.0f)

            val sliderSound = Slider(0f, 1f, 0.01f, false, game.skin, "default")
            sliderSound.value = game.gameSettings.soundVolume
            sliderSound.setAnimateDuration(0.2f)
            sliderSound.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    game.gameSettings.soundVolume = (actor as Slider).value
                    if (!actor.isDragging) {
                        game.gameAssets.playerCrashedSound.play(game.gameSettings.soundVolume)
                    }
                }
            })
            soundControl.add(sliderSound).fillX().padBottom(4.0f)

            uiLayout.add(soundControl).fillX().padBottom(20f)
            uiLayout.row()
        }

        //Music
        run {
            val musicControl = Table()
            musicControl.pad(4f)

            val txtMusic = Label("Music:", game.skin)
            txtMusic.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)
            musicControl.add(txtMusic).padRight(8.0f)
            val sliderMusic = Slider(0f, 1f, 0.01f, false, game.skin, "default")
            sliderMusic.value = game.gameSettings.musicVolume
            sliderMusic.setAnimateDuration(0.2f)
            sliderMusic.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    game.gameSettings.musicVolume = (actor as Slider).value
                    game.gameMusic.updateMusicVolume()
                }
            })
            musicControl.add(sliderMusic).fillX().padBottom(4.0f)

            uiLayout.add(musicControl).fillX().padBottom(20f)
            uiLayout.row()
        }

        //Short Countdown
        run {
            val chbx = CheckBox("Short Countdown", game.skin)
            chbx.isChecked = game.gameSettings.shortCountdownEnabled
            chbx.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    game.gameSettings.shortCountdownEnabled = (actor as CheckBox).isChecked
                }
            })
            chbx.imageCell.padBottom(8f).padRight(10f).size(32f)
            chbx.label.setFontScale(0.7f)

            uiLayout.add(chbx).fillX().padBottom(20f)
            uiLayout.row()
        }

        //Highscore in world
        run {
            val chbx = CheckBox("Show Highscore in world", game.skin)
            chbx.isChecked = game.gameSettings.showHighscoreInWorld
            chbx.addListener(object : ChangeListener() {
                override fun changed(event: ChangeListener.ChangeEvent, actor: Actor) {
                    game.gameSettings.showHighscoreInWorld = (actor as CheckBox).isChecked
                }
            })
            chbx.imageCell.padBottom(8f).padRight(10f).size(32f)
            chbx.label.setFontScale(0.7f)

            uiLayout.add(chbx).fillX().padBottom(20f)
            uiLayout.row()
        }

        //Reset Scores
        run {
            val button = TextButton("Reset Scores...", game.skin)
            button.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    game.screen = ResetScoreScreen(game)
                }
            })
            button.label.setFontScale(0.7f)
            uiLayout.add(button).fillX().padBottom(20f)
            uiLayout.row()
        }

        //Back Button
        run {
            val btnBack = TextButton("Go Back", game.skin)
            btnBack.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    game.showMainMenu()
                }
            })
            btnBack.label.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)

            uiLayout.add(btnBack)
        }
        //Add ui elements to stage

        //Head controls
        uiStage.rootTable.top().right().pad(4f)
        headLayout = Utils.createUIHeadLayout(game)

        Utils.addSoundAndMusicControllerToLayout(game, headLayout)

        if (game.gameConfig.debugSettingsAvailable) {
            val btnSettings = ImageButton(game.skin, "settings")
            btnSettings.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    val d = PixelDialog("Debug Settings", game.skin)
                    d.prefWidth = uiStage.stageViewport.worldWidth * 0.8f
                    d.width = uiStage.stageViewport.worldWidth * 0.8f
                    d.isMovable = false
                    d.label("Do you want to open DEBUG Settings?")
                    d.label("(This is only useful to beta testers)")
                    run {
                        val btnYes = TextButton("Yes", game.skin)
                        btnYes.addListener(object : ClickListener() {
                            override fun clicked(event: InputEvent, x: Float, y: Float) {
                                game.screen = DebugSettingsScreen(game)
                                d.hide()
                            }
                        })
                        d.button(btnYes)
                    }
                    run {
                        val btnNo = TextButton("No", game.skin)
                        btnNo.addListener(object : ClickListener() {
                            override fun clicked(event: InputEvent, x: Float, y: Float) {
                                //show debug screen
                                d.hide()
                            }
                        })
                        d.button(btnNo)
                    }
                    d.init()
                    d.show(uiStage.uiStage)
                }
            })
            btnSettings.imageCell.fill().expand()
            headLayout.add(btnSettings)

        }

        Utils.addFullScreenButtonToTable(game, headLayout)
        uiStage.add(headLayout)
        //Main Layout
        uiStage.addActorToStage(uiLayout)
    }

    override fun show() {
        Gdx.input.inputProcessor = uiStage.inputProcessor
        game.renderManager.resetFontSize()
        uiStage.updateViewportToScreen()
        uiLayout.invalidateHierarchy()
        headLayout.invalidateHierarchy()
    }

    override fun render(delta: Float) {
        uiStage.act(delta)
        uiStage.draw(game.renderManager)
    }

    override fun resize(width: Int, height: Int) {
        uiStage.updateViewport(width, height, true)
        uiLayout.invalidateHierarchy()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {
        dispose()
        game.gameSettings.saveToDisk()
    }

    override fun dispose() {
        uiStage.dispose()
    }
}