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
import net.brenig.pixelescape.lib.utils.UiUtils
import net.brenig.pixelescape.render.ui.CurrentHighscoreLabel
import net.brenig.pixelescape.render.ui.general.StageManager
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack

/**
 * PixelEscape MainMenu
 */
class MainMenuScreen(game: PixelEscape) : PixelScreen(game) {

    private val uiStage: StageManager = StageManager(game.renderManager)

    /**
     * layout used to group main ui elements
     */
    private val mainUiLayout: Table

    /**
     * layout used to group setting buttons
     */
    private val buttonPanelLayout: Table = UiUtils.createUIHeadLayout(game)
    private val highscoreLabel: CurrentHighscoreLabel
    private val gmImageStack: SwipeTabbedStack?

    private val gameMode: GameMode
        get() {
            if (gmImageStack == null) {
                throw IllegalStateException("GameMode select UI not initialized!")
            }
            return game.gameConfig.availableGameModes[gmImageStack.currentElement]
        }

    init {
        //Settings Button Panel
        //music and sound
        UiUtils.addSoundAndMusicControllerToLayout(game, buttonPanelLayout)
        //settings
        val btnSettings = ImageButton(game.skin, "settings")
        btnSettings.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = SettingsScreen(game)
            }
        })
        btnSettings.imageCell.fill().expand()
        buttonPanelLayout.add(btnSettings)
        buttonPanelLayout.invalidateHierarchy()
        //fullscreen
        UiUtils.addFullScreenButtonToTable(game, buttonPanelLayout)

        //Main UI Table
        mainUiLayout = Table()
        mainUiLayout.setFillParent(true)
        mainUiLayout.setPosition(0f, 0f)
        mainUiLayout.center()

        //Center UI Table
        val centerTable = Table()

        //PixelEscape Heading
        val header = Label("PixelEscape", game.skin)
        header.height = 150f
        header.setFontScale(1.0f)

        centerTable.padTop(0f)
        centerTable.add(header)
        centerTable.row()

        //GameMode Image
        gmImageStack = SwipeTabbedStack(SwipeTabbedStack.DEFAULT_ANIMATION_X_OFFSET)
        //init gamemodes
        for (mode in game.gameConfig.availableGameModes) {
            val gameModeImage = Image(mode.createIcon(game.gameAssets))
            //			gameModeImage.setRotation(PixelEscape.rand.nextFloat() * 10 - 5F);
            gameModeImage.setScaling(Scaling.fit)
            gmImageStack.add(gameModeImage)
        }

        gmImageStack.replaceCurrentELement(game.userData.lastGameMode)
        centerTable.add(gmImageStack).pad(20f, 0f, 10f, 0f).height(48f).fillX()
        centerTable.row()

        //Highscore Label
        highscoreLabel = CurrentHighscoreLabel(gameMode)
        centerTable.add(highscoreLabel).padBottom(40f)
        centerTable.row()

        //Buttons
        val centerButtons = Table()

        //Start Button
        val btnStart = TextButton("Start game", game.skin)
        btnStart.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                game.screen = GameScreen(game, gameMode)
            }
        })
        btnStart.label.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)
        centerButtons.add(btnStart).padBottom(8f).fillX()

        //Quit Button
        if (game.gameConfig.canQuitGame) {
            val btnQuit = TextButton("Quit game", game.skin)
            btnQuit.addListener(object : ClickListener() {
                override fun clicked(event: InputEvent, x: Float, y: Float) {
                    Gdx.app.exit()
                }
            })
            btnQuit.label.setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)
            centerButtons.row()
            centerButtons.add(btnQuit).fillX()
        }
        centerTable.add(centerButtons)

        //Left Arrow
        val arrowLeft = Button(game.skin, StyleNames.BUTTON_ARROW_LEFT)
        arrowLeft.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                gmImageStack.last()
                highscoreLabel.setGameMode(gameMode)
            }
        })
        mainUiLayout.add(arrowLeft).size(96f, 256f)

        //Main UI
        mainUiLayout.add(centerTable)

        //Right Arrow
        val arrowRight = Button(game.skin, StyleNames.BUTTON_ARROW_RIGHT)
        arrowRight.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                gmImageStack.next()
                highscoreLabel.setGameMode(gameMode)
            }
        })
        mainUiLayout.add(arrowRight).size(96f, 256f)

        //Move Arrows to front
        arrowLeft.toFront()
        arrowRight.toFront()

        //Add ui elements to stage
        uiStage.rootTable.top().right().pad(4f)
        uiStage.add(buttonPanelLayout)
        uiStage.addActorToStage(mainUiLayout)
    }

    override fun show() {
        game.renderManager.resetFontSize()
        uiStage.updateViewportToScreen()
        mainUiLayout.invalidateHierarchy()
        buttonPanelLayout.invalidateHierarchy()
        @Suppress("ConstantConditionIf")
        if (Reference.ENABLE_MUSIC) game.gameMusic.playOrFadeInto(game.gameAssets.mainMenuMusic)
        Gdx.input.inputProcessor = uiStage.inputProcessor
    }

    override fun render(delta: Float) {
        uiStage.act(delta)
        uiStage.draw(game.renderManager)
    }

    override fun resize(width: Int, height: Int) {
        uiStage.updateViewport(width, height, true)
        mainUiLayout.invalidateHierarchy()
    }

    override fun pause() {

    }

    override fun resume() {

    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        game.userData.lastGameMode = gmImageStack!!.currentElement
        uiStage.dispose()
    }
}
