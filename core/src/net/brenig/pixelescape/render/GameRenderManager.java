package net.brenig.pixelescape.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;
import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.lib.LogHelperKt;
import net.brenig.pixelescape.lib.Reference;


/**
 * Manages the games sprite batch and shaperenderer
 */
public class GameRenderManager implements Disposable {

	private State state = State.INVALID;

	private OrthographicCamera camera;

	private SpriteBatch batch;

	private GameAssets gameAssets;


	/**
	 * basic square texture for easy access
	 */
	@SuppressWarnings("FieldCanBeLocal")
	private TextureRegion square;


	/**
	 * current color (for drawing rectangles, etc.)
	 */
	private Color color = Color.BLACK;

	/**
	 * current colored square texture
	 */
	private Sprite squareDrawable;

	public GameRenderManager() {
		//initialize viewport
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
	}

	public void setGameAssets(GameAssets gameAssets) {
		this.gameAssets = gameAssets;
		this.square = gameAssets.getSquare();
		this.squareDrawable = new Sprite(square);
		this.squareDrawable.setColor(color);
	}

	/**
	 * initializes SpriteBatch and shape renderer
	 */
	public void initializeRendering() {
		if (state != State.INVALID) {
			throw new IllegalStateException("Error initializing Rendering!! Already initialized state: " + state);
		}
		//initialize drawing area
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		state = State.READY;
	}

	/**
	 * Prepares the screen for rendering
	 */
	public void prepareRender() {
		Gdx.gl.glClearColor(1, 1, 1, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// tell the camera to update its matrices.
		camera.update();
	}

	/**
	 * updates camera and spritebatch/shaperenderer to new size
	 */
	public void onResize(float gameSizeX, float gameSizeY) {
		camera.setToOrtho(false, gameSizeX, gameSizeY);
		batch.setProjectionMatrix(camera.combined);
		camera.update();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public BitmapFont getFont() {
		return gameAssets.getDefaultFont();
	}

	@Override
	public void dispose() {
		state = State.INVALID;
		batch.dispose();
	}

	/**
	 * prepares batch for drawing
	 */
	public void begin() {
		if (state != State.BATCH) {
			end();
			batch.begin();
			state = State.BATCH;
		}
	}

	/**
	 * sets font scale
	 */
	public void setFontScale(float scale) {
		getFont().getData().setScale(scale);
	}

	/**
	 * resets font to default size for ui
	 */
	public void resetFontSizeToDefaultGuiSize() {
		setFontScale(Reference.GAME_UI_MAIN_MENU_FONT_SIZE);
	}

	/**
	 * resets font to default(*1.0) size
	 */
	public void resetFontSize() {
		setFontScale(1);
	}

	/**
	 * ends any drawing that is in progress and flushes batch/shaperenderer
	 */
	public void end() {
		switch (state) {
			case READY:
				return;
			case BATCH:
				batch.end();
				break;
			case INVALID:
				LogHelperKt.warn("RenderManager in invalid state. Initializing...");
				if(batch != null) {
					batch.dispose();
				}
				initializeRendering();
				return;
			default:
				LogHelperKt.error("RenderManager in unknown state!");
				if (batch.isDrawing()) {
					batch.end();
					break;
				} else {
					LogHelperKt.error("Unable to reconstruct state!!");
				}
				break;
		}
		state = State.READY;
	}

	/**
	 * draws the drawable at the given position and size
	 * <p>
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(Drawable drawable, float x, float y, float width, float height) {
		drawable.draw(batch, x, y, width, height);
	}

	/**
	 * draws the TextureRegion at the given position and size
	 * <p>
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(TextureRegion drawable, float x, float y, float width, float height) {
		batch.draw(drawable, x, y, width, height);
	}

	/**
	 * draws the TextureRegion at the given position
	 * <p>
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(TextureRegion drawable, float x, float y) {
		batch.draw(drawable, x, y);
	}

	/**
	 * draws the Sprite at its position and size
	 * <p>
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(Sprite sprite) {
		sprite.draw(batch);
	}

	/**
	 * draws the given String
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void draw(String text, float x, float y) {
		getFont().draw(batch, text, x, y);
	}

	/**
	 * draws the given String
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void draw(String text, Color color, float x, float y) {
		getFont().setColor(color);
		getFont().draw(batch, text, x, y);
	}

	/**
	 * draws the given String
	 * <p>
	 * note: Renderer has to be initialized and in the right state
	 */
	public void draw(String text, Color color, float x, float y, float size) {
		setFontScale(size);
		getFont().setColor(color);
		getFont().draw(batch, text, x, y);
	}


	/**
	 * draws a filled Rectangle at the given position and size using the specified batch
	 * <p>
	 * note: the batch has to be initialized and in the correct state
	 */
	public void rect(Batch batch, float x, float y, float width, float height) {
		squareDrawable.setBounds(x, y, width, height);
		squareDrawable.draw(batch);
	}

	/**
	 * draws a filled Rectangle at the given position and size
	 * <p>
	 * note: the renderer has to be initialized and in the correct state {@link State#BATCH}
	 */
	public void rect(float x, float y, float width, float height, Color color) {
		setColor(color);
		rect(x, y, width, height);
	}

	/**
	 * draws a filled Rectangle at the given position and size
	 * <p>
	 * note: the renderer has to be initialized and in the correct state {@link State#BATCH}
	 */
	public void rect(float x, float y, float width, float height) {
		squareDrawable.setBounds(x, y, width, height);
		squareDrawable.draw(batch);
	}


	/**
	 * draws a filled line at the given position and size
	 * <p>
	 * note: the renderer has to be initialized and in the correct state {@link State#BATCH}
	 */
	public void line(float x1, float y1, float x2, float y2, int thickness, Color color) {
		setColor(color);
		line(x1, y1, x2, y2, thickness);
	}

	/**
	 * draws a filled line at the given position and size
	 * <p>
	 * note: the renderer has to be initialized and in the correct state {@link State#BATCH}
	 */
	public void line(float x1, float y1, float x2, float y2, int thickness) {
		float dx = x2 - x1;
		float dy = y2 - y1;
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		float rad = (float) Math.atan2(dy, dx);
		squareDrawable.setBounds(x1, y1, dist, thickness);
		squareDrawable.setRotation(rad * MathUtils.radiansToDegrees);
		squareDrawable.draw(batch);
		squareDrawable.setRotation(0);
	}

	/**
	 * sets the current color to draw basic shapes in (when not using {@link ShapeRenderer}
	 */
	public void setColor(Color color) {
		if (this.color != color) {
			squareDrawable.setColor(color);
			this.color = color;
		}
	}

	/**
	 * sets the current color to draw basic shapes in (when not using {@link ShapeRenderer}
	 */
	public void setColor(float r, float g, float b, float a) {
		setColor(new Color(r, g, b, a));
	}

	public void enableBlending() {
		batch.enableBlending();
	}

	public void disableBlending() {
		batch.disableBlending();
	}

	public enum State {
		READY, BATCH, INVALID
	}
}
