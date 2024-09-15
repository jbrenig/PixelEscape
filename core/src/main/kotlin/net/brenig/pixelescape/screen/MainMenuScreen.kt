package net.brenig.pixelescape.screen


import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.*
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.Scaling
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.lib.LangKeys
import net.brenig.pixelescape.lib.translate
import net.brenig.pixelescape.lib.utils.UiUtils
import net.brenig.pixelescape.lib.utils.horizontalSpacer
import net.brenig.pixelescape.render.ui.CurrentHighscoreLabel
import net.brenig.pixelescape.render.ui.general.PlayServiceLoginButton
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack

/**
 * PixelEscape MainMenu
 */
class MainMenuScreen(game: PixelEscape) : ScreenWithUi(game) {

    private val highscoreLabel: CurrentHighscoreLabel
    private val gmImageStack: SwipeTabbedStack

    private val playServiceButton: PlayServiceLoginButton?
    private val btnLeaderboards: ImageTextButton?

    private val gameMode: GameMode
        get() {
            return game.gameConfig.availableGameModes[gmImageStack.currentElement]
        }

    init {
        //region header

        //PixelEscape Heading
        val headerLayoutTable = uiStage.createHeaderLayoutTable()
        val header = Label(LangKeys.MainMenu.TITLE.translate(), game.skin, StyleNames.LABEL_BIG)
        headerLayoutTable.add(header).top().padTop(40f)

        //endregion

        //region Header Bar

        val headerBar = uiStage.createHeadUiLayoutTable()

        if (game.gameConfig.gameServiceAvailable) {
            playServiceButton = PlayServiceLoginButton(game.skin, StyleNames.SERVICE_LOGIN, StyleNames.SERVICE_WORKING, StyleNames.SERVICE_LOGOUT, game.gameConfig.gameService)
            headerBar.add(playServiceButton)
        } else {
            playServiceButton = null
        }

        headerBar.horizontalSpacer()

        //music and sound
        val buttonPanelLayout = UiUtils.addSoundAndMusicControllerToLayout(game, UiUtils.createUIHeadLayout(game))

        //settings
        val btnSettings = ImageButton(game.skin, StyleNames.BUTTON_SETTINGS)
        btnSettings.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = SettingsScreen(game)
            }
        })
        buttonPanelLayout.add(btnSettings)

        //fullscreen
        UiUtils.addFullScreenButtonToTable(game, buttonPanelLayout)

        headerBar.add(buttonPanelLayout)

        //endregion

        //region Content

        //Main UI Table
        val mainUiLayout = uiStage.createContentUiLayoutTable().padBottom(Reference.GAME_UI_Y_SIZE.toFloat()).center()

        //Center UI Table
        val centerTable = Table()

        //GameMode Image
        gmImageStack = SwipeTabbedStack(SwipeTabbedStack.DEFAULT_ANIMATION_X_OFFSET)
        //init gamemodes
        for (mode in game.gameConfig.availableGameModes) {
            val gameModeImage = Image(mode.createIcon(game.gameAssets))
            gameModeImage.setScaling(Scaling.fit)
            gmImageStack.add(gameModeImage)
        }

        gmImageStack.replaceCurrentElement(game.userData.lastGameMode)

        centerTable.add(gmImageStack).padBottom(10f).height(48f).fillX()
        centerTable.row()

        //Highscore Label
        highscoreLabel = CurrentHighscoreLabel(gameMode)
        centerTable.add(highscoreLabel).padBottom(10f)
        centerTable.row()

        gmImageStack.setElementChangedListener(object : SwipeTabbedStack.IElementChangedListener {
            override fun onElementChanged(newElement: Int) {
                highscoreLabel.setGameMode(gameMode)
            }
        })

        //Buttons
        val centerButtons = Table()

        //Start Button
        val btnStart = TextButton(LangKeys.MainMenu.START.translate(), game.skin)
        btnStart.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = GameScreen(game, gameMode)
            }
        })
        centerButtons.add(btnStart).padBottom(8f).fillX()

        if (game.gameConfig.gameServiceAvailable) {
            btnLeaderboards = UiUtils.createLeaderboardsButton(game, uiStage, ::gameMode, ::playServiceStateUpdate)

            playServiceButton!!.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent?, x: Float, y: Float) {
                    btnLeaderboards.isDisabled = !game.gameConfig.gameService.isSessionActive
                }
            })
            centerButtons.row()
            centerButtons.add(btnLeaderboards).padBottom(8f).fillX()
        } else {
            btnLeaderboards = null
        }

        //Quit Button
        if (game.gameConfig.canQuitGame) {
            val btnQuit = TextButton(LangKeys.MainMenu.EXIT.translate(), game.skin)
            btnQuit.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    Gdx.app.exit()
                }
            })
            btnQuit.label.setFontScale(1f)
            centerButtons.row()
            centerButtons.add(btnQuit).fillX()
        }
        centerTable.add(centerButtons)

        //Left Arrow
        val arrowLeft = Button(game.skin, StyleNames.BUTTON_ARROW_LEFT)
        arrowLeft.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                gmImageStack.last()
            }
        })
        mainUiLayout.add(arrowLeft).size(96f, 256f)

        //Main UI
        mainUiLayout.add(centerTable).padRight(20f).padLeft(20f)

        //Right Arrow
        val arrowRight = Button(game.skin, StyleNames.BUTTON_ARROW_RIGHT)
        arrowRight.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                gmImageStack.next()
            }
        })
        mainUiLayout.add(arrowRight).size(96f, 256f)

        //Move Arrows to front
        arrowLeft.toFront()
        arrowRight.toFront()

        //endregion
    }

    override fun gsOnSessionActive() {
        super.gsOnSessionActive()
        playServiceStateUpdate()
    }

    override fun gsOnSessionInactive() {
        super.gsOnSessionInactive()
        playServiceStateUpdate()
    }

    private fun playServiceStateUpdate() {
        playServiceButton?.updateInfo()
        btnLeaderboards?.isDisabled = !game.gameConfig.gameService.isSessionActive
    }

    override fun show() {
        game.renderManager.resetFontSize()
        uiStage.updateViewportToScreen()
        if (game.gameConfig.musicAvailable) game.gameMusic.playOrFadeInto(game.gameAssets.mainMenuMusic)
        Gdx.input.inputProcessor = uiStage.inputProcessor
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
        game.userData.lastGameMode = gmImageStack.currentElement
        uiStage.dispose()
    }
}
