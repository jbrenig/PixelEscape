package net.brenig.pixelescape.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.InputProcessor
import com.badlogic.gdx.audio.Music
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.GlyphLayout
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import net.brenig.pixelescape.PixelEscape
import net.brenig.pixelescape.game.InputManager
import net.brenig.pixelescape.game.World
import net.brenig.pixelescape.game.data.GameDebugSettings
import net.brenig.pixelescape.game.data.GameMode
import net.brenig.pixelescape.game.data.constants.StyleNames
import net.brenig.pixelescape.lib.Reference
import net.brenig.pixelescape.lib.log
import net.brenig.pixelescape.render.WorldRenderer
import net.brenig.pixelescape.render.overlay.*
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer
import net.brenig.pixelescape.render.ui.general.VerticalSpacer
import net.brenig.pixelescape.render.ui.ingame.AbilityWidget
import net.brenig.pixelescape.render.ui.ingame.ScoreWidget
import net.brenig.pixelescape.render.ui.ingame.StageManagerGame


/**
 * Main Game Screen<br></br>
 * Displays the game. Provides overlays for GamePaused, GameOver etc.
 */
class GameScreen(game: PixelEscape, val gameMode: GameMode) : PixelScreen(game) {

    /**
     * The game world
     */
    val world: World

    /**
     * Game world renderer
     */
    val worldRenderer: WorldRenderer

    /**
     * position of the ui elements / black bars height
     */
    var uiPos = 0
        private set

    /**
     * Game-Font
     */
    val fontLayout = GlyphLayout()

    /**
     * window paused (by os)
     */
    var isScreenPaused = false
        private set

    var isInitialized = true
        private set

    @Volatile private var valid = false

    //Game UI
    private val emptyOverlay: EmptyOverlay
    private val stage: StageManagerGame
    val input: InputManager
    private val inputMultiplexer: InputMultiplexer

    private var overlay: Overlay

    val gameMusic: Music?
        get() = game.gameAssets.getRandomGameMusic(PixelEscape.rand)

    val isGamePaused: Boolean
        get() = overlay.doesPauseGame() || isScreenPaused

    val uiSize: Float
        get() = Reference.GAME_UI_Y_SIZE.toFloat()

    val uiPadding: Float
        get() = Reference.GAME_UI_Y_PADDING

    init {
        log("GameScreen", "Initializing game. GameMode: " + gameMode.gameModeName)
        //init world and renderer
        this.world = World(this, game.gameSizeX)
        this.worldRenderer = WorldRenderer(game, world)
        //create default overlay
        this.emptyOverlay = EmptyOverlay(this)

        //init ui
        stage = StageManagerGame(this)

        val table = stage.createHeadUiLayoutTable()

        game.font.data.setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)
        val buttonPause = ImageTextButton("Pause", this.game.skin, StyleNames.BUTTON_PAUSE)
        buttonPause.imageCell.padRight(6f).padBottom(4f)
        buttonPause.addListener(object : ClickListener() {
            override fun clicked(event: InputEvent, x: Float, y: Float) {
                setOverlay(GamePausedOverlay(this@GameScreen, false))
            }
        })
        table.add(buttonPause)
        table.add(HorizontalSpacer())
        table.add(ScoreWidget(world.player, fontLayout, game))
        if (gameMode.abilitiesEnabled()) {
            stage.row()
            stage.add(VerticalSpacer())
            stage.row()
            stage.add(AbilityWidget(game.skin, world.player, this)).bottom().right().size(128f).pad(0f, 0f, 32f, (uiPos + 32).toFloat())
        }

        //init input
        input = InputManager()
        inputMultiplexer = InputMultiplexer(stage.inputProcessor, input)

        //set default overlay
        overlay = emptyOverlay

        log("GameScreen", "Game initialized.")

    }

    override fun show() {
        isInitialized = false
        valid = true
        game.gameMusic.playOrFadeInto(gameMusic)
        Gdx.input.inputProcessor = inputMultiplexer
    }

    override fun render(delta: Float) {
        //Cap max frame time to ensure proper simulation
        //Game will be slowed down if the frames don't get processed fast enough
        val gameDelta = Math.min(Reference.MAX_FRAME_TIME, delta)

        if (game.gameConfig.canHideCursor)
            input.updateMouseVisibility(gameDelta, game.gameSettings.fullscreen && overlay.canHideCursor())

        if (!isInitialized) {
            init()
        }

        if (!isGamePaused) {
            //update world
            world.update(gameDelta)
        }

        //Pause on escape
        if (input.isEscapeDown) {
            if (overlay.shouldPauseOnEscape()) {
                showGamePausedOverlay()
            }
        }

        //black background
        game.renderManager.disableBlending()
        renderUIBackground()
        game.renderManager.enableBlending()

        //render world
        worldRenderer.render(gameDelta)

        //draw ui

        //Overlay first callback
        overlay.renderFirst(gameDelta)

        //UI
        if (!overlay.shouldHideGameUI()) {

            //Draw lives
            renderLives()
            renderEffectTime()

            this.game.font.data.setScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE)
            stage.draw(game.renderManager)
            stage.act(gameDelta)
        }

        //Overlay
        overlay.render(gameDelta)

        renderDebugInformation()
    }

    private fun renderUIBackground() {
        game.renderManager.begin()

        if (GameDebugSettings["DEBUG_SCREEN_BOUNDS"]) {
            game.renderManager.setColor(1f, 0f, 0f, 0f)
        } else {
            game.renderManager.setColor(0f, 0f, 0f, 1f)
        }
        game.renderManager.rect(0f, 0f, world.worldWidth.toFloat(), uiPos.toFloat())
        game.renderManager.rect(0f, (world.worldHeight + uiPos).toFloat(), world.worldWidth.toFloat(), (uiPos + Reference.GAME_UI_Y_SIZE).toFloat())
    }

    private fun renderEffectTime() {
        val effects = world.player.statusEffects
        if (!effects.isEmpty()) {
            game.renderManager.begin()
            var index = 0
            val yPos = world.worldHeight + uiPos - 6
            val xPos = 10
            val xSize = 16
            val ySize = 32
            for (effect in effects) {
                val timeRemaining = effect.scaledTime
                if (timeRemaining > 0) {
                    val timeRemainingScaled = timeRemaining * ySize
                    effect.updateRenderColor(game.renderManager)
                    game.renderManager.rect((xPos + index * xSize).toFloat(), yPos - timeRemainingScaled, xSize.toFloat(), timeRemainingScaled)
                    index++
                }
            }
        }
    }

    private fun renderLives() {
        if (gameMode.extraLives > 0) {
            game.renderManager.begin()
            for (index in 1..world.player.extraLives + 1) {
                game.renderManager.batch.draw(game.gameAssets.heart, (game.gameSizeX - 36 * index).toFloat(), (uiPos + world.worldHeight - 28).toFloat())
            }
        }
    }

    /**
     * Setup world on first update
     */
    private fun init() {
        isInitialized = true
        if (game.userData.tutorialSeen(gameMode)) {
            setOverlay(CountDownOverlay(this))
        } else {
            setOverlay(TutorialOverlay(this))
        }
        world.generateWorld(true)
        world.spawnEntities()
    }

    private fun renderDebugInformation() {
        if (GameDebugSettings["DEBUG_MODE_COORDS"]) {
            val x = game.scaledMouseX
            val y = game.scaledMouseY
            val worldY = world.convertMouseYToWorldCoordinate(y)
            val screenTxt = "Screen: X: " + x.toInt() + ", Y: " + world.convertMouseYToScreenCoordinate(y).toInt() + "(" + y.toInt() + " / raw: " + Gdx.input.y + "), Player speed: " + world.player.xVelocity.toInt()
            val worldTxt = "World: X: " + world.convertScreenToWorldCoordinate(x).toInt() + ", Y: " + worldY.toInt() + ", Block: " + world.convertScreenCoordToWorldBlockIndex(x) + " (" + world.convertWorldBlockToLocalBlockIndex(world.convertScreenCoordToWorldBlockIndex(x)) + ")"
            val terrain = world.getTerrainPairForIndex(world.convertScreenCoordToLocalBlockIndex(x))
            val isTerrain = terrain.bot * Reference.BLOCK_WIDTH >= worldY || world.worldHeight - terrain.top * Reference.BLOCK_WIDTH <= worldY
            val blockInfoTxt = "Info: IsTerrain: " + isTerrain + ", BlocksGenerated: " + world.terrainBufferWorldIndex

            //Begin draw
            game.renderManager.begin()
            game.font.color = Color.LIGHT_GRAY
            game.font.data.setScale(0.5f)
            //Draw
            fontLayout.setText(game.font, screenTxt)
            var pos = 5 + fontLayout.height
            game.font.draw(game.batch, fontLayout, 5f, pos)

            pos += fontLayout.height + 5
            fontLayout.setText(game.font, worldTxt)
            game.font.draw(game.batch, fontLayout, 5f, pos)

            pos += fontLayout.height + 5
            fontLayout.setText(game.font, blockInfoTxt)
            game.font.draw(game.batch, fontLayout, 5f, pos)
            //End draw
            game.font.data.setScale(1f)
        } else if (GameDebugSettings["DEBUG_MUSIC"]) {
            game.renderManager.begin()
            game.font.color = Color.LIGHT_GRAY
            game.font.data.setScale(0.5f)
            fontLayout.setText(game.font, "Music state: " + game.gameMusic.getState())
            val pos = 5 + fontLayout.height
            game.font.draw(game.batch, fontLayout, 5f, pos)
            game.font.data.setScale(1f)
        }
    }

    override fun resize(width: Int, height: Int) {
        //update viewports and world size
        val targetHeight = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE
        uiPos = Math.ceil(((game.gameSizeY - targetHeight) / 2).toDouble()).toInt()

        world.resize(game.gameSizeX)
        worldRenderer.setWorldRendererYOffset(uiPos.toFloat())
        worldRenderer.onResize()
        //Update UI
        stage.updateStageToGameBounds(width, height)
        //update Overlay
        overlay.onResize(width, height)
    }

    override fun pause() {
        if (GameDebugSettings["AUTO_PAUSE"]) {
            isScreenPaused = true
            overlay.pause()
        }
    }

    override fun resume() {
        if (GameDebugSettings["AUTO_PAUSE"]) {
            isScreenPaused = false
            overlay.resume()
        }
    }

    override fun hide() {
        dispose()
    }

    override fun dispose() {
        valid = false
        input.resetMouseVisibility()

        overlay.dispose()
    }

    fun setOverlayInputProcessor(processor: InputProcessor?) {
        if (valid) {
            Gdx.input.inputProcessor = processor
        }
    }

    /**
     * Removes all Overlays and resets to an EmptyOverlay
     */
    fun resetToEmptyOverlay() {
        if (overlay !== emptyOverlay) {
            setOverlay(emptyOverlay)
        }
    }

    override fun updateMusic(play: Boolean) {
        overlay.updateMusic(play)
    }

    /**
     * Sets a new overlay as active<br></br>
     * disposes the old overlay and resets InputProcessors
     *
     * @param o [Overlay] to show
     */
    fun setOverlay(o: Overlay) {
        overlay.dispose()
        resetInputManager()
        overlay = o
        overlay.show()
        if (!overlay.canHideCursor()) {
            input.resetMouseVisibility()
        }
    }

    /**
     * Callback when player died
     * Shows GameOver Overlay and registers highscore
     */
    fun onGameOver() {
        setOverlay(GamePausedOverlay(this, true))
        game.userData.updateHighscore(gameMode, world.player.score)
    }

    /**
     * restarts the game
     */
    fun restart() {
        isInitialized = false
        resetToEmptyOverlay()
        world.restart()
    }

    fun showGamePausedOverlay() {
        this.setOverlay(GamePausedOverlay(this, false))
    }

    fun showMainMenu() {
        game.showMainMenu()
    }

    fun resetInputManager() {
        if (valid) {
            Gdx.input.inputProcessor = inputMultiplexer
            input.refreshButtonState()
            stage.inputProcessor.mouseMoved(Gdx.input.x, Gdx.input.y)
        }
    }
}