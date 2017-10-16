package net.brenig.pixelescape

import com.badlogic.gdx.Application
import com.badlogic.gdx.Game
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.NinePatch
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import net.brenig.pixelescape.game.data.*
import net.brenig.pixelescape.game.data.constants.Reference
import net.brenig.pixelescape.lib.*
import net.brenig.pixelescape.render.GameRenderManager
import net.brenig.pixelescape.screen.MainMenuScreen
import net.brenig.pixelescape.screen.PixelScreen
import java.util.*

/**
 * Main Game class
 */
class PixelEscape @JvmOverloads constructor(
        /**
         * Platform dependent GameConfiguration
         */
        val gameConfig: GameConfiguration = GameConfiguration()) : Game() {

    /**
     * true if assets are loaded. Used for unloading when paused and in background
     */
    private var assetsLoaded = false

    /**
     * @return Game Renderer
     */
    lateinit var renderManager: GameRenderManager private set
    lateinit var gameAssets: GameAssets private set
    lateinit var gameMusic: GameMusic private set
    lateinit var gameSettings: GameSettings private set
    lateinit var gameDebugSettings: GameDebugSettings private set
    lateinit var userData: UserData private set

    var gameSizeX = Reference.TARGET_RESOLUTION_X; private set
    var gameSizeY = Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE; private set

    val scaledMouseX: Float
        get() {
            val scale = gameSizeX.toFloat() / Gdx.graphics.width
            return Gdx.input.x * scale
        }

    val scaledMouseY: Float
        get() {
            val scale = gameSizeY.toFloat() / Gdx.graphics.height
            return Gdx.input.y * scale
        }

    /**
     * Main BitMap Font
     */
    val font: BitmapFont
        get() = gameAssets.defaultFont

    /**
     * Default button ninepatch
     */
    val buttonNinePatch: NinePatch
        get() = gameAssets.buttonNinePatch

    /**
     * Main Gui Skin
     */
    val skin: Skin
        get() = gameAssets.mainUiSkin

    /**
     * Main Sprite Batch
     */
    val batch: SpriteBatch
        get() = renderManager.batch

    override fun create() {
        if (gameConfig.loggingEnabled) {
            setGDXLogLevel(Application.LOG_DEBUG)
        } else {
            setGDXLogLevel(Application.LOG_NONE)
        }
        info("Main", "Starting up...")
        if (pixelEscape != null) {
            if (pixelEscape!!.assetsLoaded) {
                pixelEscape!!.dispose() //needed?
            }
            warn("Critical Error! Game already initialized!")
        }
        pixelEscape = this

        //initialize renderer
        renderManager = GameRenderManager()

        //load everything needed
        initializeRendering()

        gameMusic = GameMusic(this)

        //load settings
        gameSettings = GameSettings()
        gameDebugSettings = GameDebugSettings()

        //load userdata
        //currently only highscore
        userData = UserData()
        //convert legacy savedata
        userData.updateSaveGames()

        //open main menu
        showMainMenu()

        log("Main", "Finished loading!")
    }

    override fun setScreen(screen: Screen) {
        //reset font
        renderManager.resetFontSize()
        super.setScreen(screen)
    }

    override fun render() {
        // update game music
        gameMusic.update(Gdx.graphics.deltaTime)

        // render frame
        renderManager.prepareRender()
        super.render()
        if (GameDebugSettings.get("SHOW_FPS")) {
            renderManager.begin()
            font.color = Color.RED
            renderManager.resetFontSize()
            font.draw(batch, "FPS " + Gdx.graphics.framesPerSecond, 10f, (gameSizeY - 10).toFloat())
        }
        renderManager.end()
    }

    override fun dispose() {
        saveUserData()
        unloadAssets()
        super.dispose()
    }

    fun saveUserData() {
        gameSettings.saveToDisk()
        userData.saveToDisk()
    }

    fun unloadAssets() {
        renderManager.dispose()
        gameAssets.disposeAll()
        assetsLoaded = false
    }

    override fun resume() {
        if (!assetsLoaded) {
            initializeRendering()
        }
        super.resume()
    }

    /**
     * loads game assets and initializes rendering
     */
    private fun initializeRendering() {

        renderManager.initializeRendering()

        gameAssets = GameAssets()

        gameAssets.initAll()

        renderManager.setGameAssets(gameAssets)

        assetsLoaded = true
    }

    override fun resize(width: Int, height: Int) {
        log("Main", "Resizing...")

        val targetHeight = (Reference.GAME_RESOLUTION_Y + Reference.GAME_UI_Y_SIZE).toFloat()
        val targetWidth = Reference.TARGET_RESOLUTION_X.toFloat()
        val targetRatio = targetHeight / targetWidth
        val sourceRatio = height.toFloat() / width.toFloat()
        val scale = if (sourceRatio > targetRatio) targetWidth / width else targetHeight / height
        gameSizeX = Math.ceil((width * scale).toDouble()).toInt()
        gameSizeY = Math.ceil((height * scale).toDouble()).toInt()

        renderManager.onResize(gameSizeX.toFloat(), gameSizeY.toFloat())

        log("Main", "new width: $gameSizeX, new height: $gameSizeY")

        super.resize(width, height)
    }

    fun showMainMenu() {
        setScreen(MainMenuScreen(this))
    }

    /**
     * stops or starts music if settings have changed
     */
    fun updateMusicPlaying() {
        if (!gameSettings.isMusicEnabled) {
            gameMusic.fadeOutToStop(0.5f)
        }
        if (screen is PixelScreen) {
            (screen as PixelScreen).updateMusic(gameSettings.isMusicEnabled)
        }
    }

    /**
     * goes or leaves fullscreen
     */
    fun updateFullscreen() {
        if (gameConfig.canGoFullScreen) {
            if (gameSettings.fullscreen) {
                val oldMode = Gdx.graphics.displayMode
                Gdx.graphics.setFullscreenMode(oldMode)
            } else {
                Gdx.graphics.setWindowedMode(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y)
            }
        }
    }

    companion object {
        /**
         * general Random instance
         */
        val rand = Random()

        /**
         * singleton static instance
         */
        private var pixelEscape: PixelEscape? = null

        val INSTANCE: PixelEscape
            get() = pixelEscape ?: throw IllegalStateException("Game not initialized!")
    }
}//set default config
