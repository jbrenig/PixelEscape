package net.brenig.pixelescape.render

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.*
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.utils.Drawable
import com.badlogic.gdx.utils.Disposable
import net.brenig.pixelescape.game.data.GameAssets
import net.brenig.pixelescape.lib.error
import net.brenig.pixelescape.lib.warn


/**
 * Manages the games sprite batch and shaperenderer
 */
class GameRenderManager : Disposable {

    private var state = State.INVALID

    val camera: OrthographicCamera = OrthographicCamera()

    lateinit var batch: SpriteBatch private set

    private lateinit var gameAssets: GameAssets


    /**
     * basic square texture for easy access
     */
    private lateinit var square: TextureRegion


    /**
     * current color (for drawing rectangles, etc.)
     */
    private var color = Color.BLACK

    /**
     * current colored square texture
     */
    private lateinit var squareDrawable: Sprite

    val font: BitmapFont
        get() = gameAssets.defaultFont

    init {
        //initialize viewport
        camera.setToOrtho(false)
    }

    fun setGameAssets(gameAssets: GameAssets) {
        this.gameAssets = gameAssets
        this.square = gameAssets.square
        this.squareDrawable = Sprite(square)
        this.squareDrawable.color = color
    }

    /**
     * initializes SpriteBatch and shape renderer
     */
    fun initializeRendering() {
        if (state != State.INVALID) {
            throw IllegalStateException("Error initializing Rendering!! Already initialized state: " + state)
        }
        //initialize drawing area
        batch = SpriteBatch()
        batch.projectionMatrix = camera.combined

        state = State.READY
    }

    /**
     * Prepares the screen for rendering
     */
    fun prepareRender() {
        Gdx.gl.glClearColor(1f, 1f, 1f, 1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        // tell the camera to update its matrices.
        camera.update()
    }

    /**
     * updates camera and spritebatch/shaperenderer to new size
     */
    fun onResize(gameSizeX: Float, gameSizeY: Float) {
        camera.setToOrtho(false, gameSizeX, gameSizeY)
        batch.projectionMatrix = camera.combined
        camera.update()
    }

    override fun dispose() {
        state = State.INVALID
        batch.dispose()
    }

    /**
     * prepares batch for drawing
     */
    fun begin() {
        if (state != State.BATCH) {
            end()
            batch.begin()
            state = State.BATCH
        }
    }

    /**
     * sets font scale
     */
    fun setFontScale(scale: Float) {
        font.data.setScale(scale)
    }

    /**
     * resets font to default size for ui
     */
    fun resetFontSizeToDefaultGuiSize() {
        setFontScale(1f)
    }

    /**
     * resets font to default(*1.0) size
     */
    fun resetFontSize() {
        setFontScale(1f)
    }

    /**
     * ends any drawing that is in progress and flushes batch/shaperenderer
     */
    fun end() {
        when (state) {
            GameRenderManager.State.READY -> return
            GameRenderManager.State.BATCH -> batch.end()
            GameRenderManager.State.INVALID -> {
                warn("RenderManager in invalid state. Initializing...")
                batch.dispose()
                initializeRendering()
                return
            }
            else -> {
                error("RenderManager in unknown state!")
                if (batch.isDrawing) {
                    batch.end()
                } else {
                    error("Unable to reconstruct state!!")
                }
            }
        }
        state = State.READY
    }

    /**
     * draws the drawable at the given position and size
     *
     *
     * note: the renderer has to be initialized and in the correct state
     */
    fun draw(drawable: Drawable, x: Float, y: Float, width: Float, height: Float) {
        drawable.draw(batch, x, y, width, height)
    }

    /**
     * draws the TextureRegion at the given position and size
     *
     *
     * note: the renderer has to be initialized and in the correct state
     */
    fun draw(drawable: TextureRegion, x: Float, y: Float, width: Float, height: Float) {
        batch.draw(drawable, x, y, width, height)
    }

    /**
     * draws the TextureRegion at the given position
     *
     *
     * note: the renderer has to be initialized and in the correct state
     */
    fun draw(drawable: TextureRegion, x: Float, y: Float) {
        batch.draw(drawable, x, y)
    }

    /**
     * draws the Sprite at its position and size
     *
     *
     * note: the renderer has to be initialized and in the correct state
     */
    fun draw(sprite: Sprite) {
        sprite.draw(batch)
    }

    /**
     * draws the given String
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun draw(text: String, x: Float, y: Float) {
        font.draw(batch, text, x, y)
    }

    /**
     * draws the given String
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun draw(text: String, color: Color, x: Float, y: Float) {
        font.color = color
        font.draw(batch, text, x, y)
    }

    /**
     * draws the given String
     *
     *
     * note: Renderer has to be initialized and in the right state
     */
    fun draw(text: String, color: Color, x: Float, y: Float, size: Float) {
        setFontScale(size)
        font.color = color
        font.draw(batch, text, x, y)
    }


    /**
     * draws a filled Rectangle at the given position and size using the specified batch
     *
     *
     * note: the batch has to be initialized and in the correct state
     */
    fun rect(batch: Batch, x: Float, y: Float, width: Float, height: Float) {
        squareDrawable.setBounds(x, y, width, height)
        squareDrawable.draw(batch)
    }

    /**
     * draws a filled Rectangle at the given position and size
     *
     *
     * note: the renderer has to be initialized and in the correct state [State.BATCH]
     */
    fun rect(x: Float, y: Float, width: Float, height: Float, color: Color) {
        setColor(color)
        rect(x, y, width, height)
    }

    /**
     * draws a filled Rectangle at the given position and size
     *
     *
     * note: the renderer has to be initialized and in the correct state [State.BATCH]
     */
    fun rect(x: Float, y: Float, width: Float, height: Float) {
        squareDrawable.setBounds(x, y, width, height)
        squareDrawable.draw(batch)
    }


    /**
     * draws a filled line at the given position and size
     *
     *
     * note: the renderer has to be initialized and in the correct state [State.BATCH]
     */
    fun line(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Int, color: Color) {
        setColor(color)
        line(x1, y1, x2, y2, thickness)
    }

    /**
     * draws a filled line at the given position and size
     *
     *
     * note: the renderer has to be initialized and in the correct state [State.BATCH]
     */
    fun line(x1: Float, y1: Float, x2: Float, y2: Float, thickness: Int) {
        val dx = x2 - x1
        val dy = y2 - y1
        val dist = Math.sqrt((dx * dx + dy * dy).toDouble()).toFloat()
        val rad = Math.atan2(dy.toDouble(), dx.toDouble()).toFloat()
        squareDrawable.setBounds(x1, y1, dist, thickness.toFloat())
        squareDrawable.rotation = rad * MathUtils.radiansToDegrees
        squareDrawable.draw(batch)
        squareDrawable.rotation = 0f
    }

    /**
     * sets the current color to draw basic shapes in (when not using [ShapeRenderer]
     */
    fun setColor(color: Color) {
        if (this.color !== color) {
            squareDrawable.color = color
            this.color = color
        }
    }

    /**
     * sets the current color to draw basic shapes in (when not using [ShapeRenderer]
     */
    fun setColor(r: Float, g: Float, b: Float, a: Float) {
        setColor(Color(r, g, b, a))
    }

    fun enableBlending() {
        batch.enableBlending()
    }

    fun disableBlending() {
        batch.disableBlending()
    }

    enum class State {
        READY, BATCH, INVALID
    }
}
