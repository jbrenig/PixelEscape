package net.brenig.pixelescape.render;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Disposable;

import net.brenig.pixelescape.game.data.GameAssets;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Reference;


/**
 * Manages the games sprite batch and shaperenderer
 */
public class GameRenderManager implements Disposable {

	private State state = State.INVALID;

	private OrthographicCamera camera;

	private ShapeRenderer shapeRenderer;
	private SpriteBatch batch;

	private GameAssets gameAssets;

	public GameRenderManager() {
		//initialize viewport
		camera = new OrthographicCamera();
		camera.setToOrtho(false);
	}

	public void setGameAssets(GameAssets gameAssets) {
		this.gameAssets = gameAssets;
	}

	/**
	 * initializes SpirteBatch and shape renderer
	 */
	public void initializeRendering() {
		if(state != State.INVALID) {
			throw new IllegalStateException("Error intializing Rendering!! Already initialized state: " + state);
		}
		//initialize drawing area
		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		shapeRenderer = new ShapeRenderer();
		shapeRenderer.setProjectionMatrix(camera.combined);

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
		shapeRenderer.setProjectionMatrix(camera.combined);
		camera.update();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public SpriteBatch getBatch() {
		return batch;
	}

	public ShapeRenderer getShapeRenderer() {
		return shapeRenderer;
	}

	public BitmapFont getFont() {
		return gameAssets.getFont();
	}

	@Override
	public void dispose() {
		state = State.INVALID;
		shapeRenderer.dispose();
		batch.dispose();
	}

	/**
	 * prepares batch for drawing
	 */
	public void begin() {
		if(state != State.BATCH) {
			end();
			batch.begin();
			state = State.BATCH;
		}
	}

	/**
	 * initialized shaperenderer for drawing filled shapes
	 */
	public void beginFilledShape() {
		if(state != State.SHAPE_FILLED) {
			end();
			shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
			state = State.SHAPE_FILLED;
		}
	}

	/**
	 * initialized shaperenderer for drawing
	 * @param type shapetype
	 */
	public void beginShape(ShapeRenderer.ShapeType type) {
		if(state != State.SHAPE_OTHER) {
			end();
			shapeRenderer.begin(type);
			state = State.SHAPE_OTHER;
		} else if(shapeRenderer.getCurrentType() != type) {
			shapeRenderer.end();
			shapeRenderer.begin(type);
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
			case SHAPE_FILLED:
			case SHAPE_OTHER:
				shapeRenderer.end();
				break;
			case INVALID:
				LogHelper.warn("RenderManager in invalid state. Initializing...");
				prepareRender();
				return;
			default:
				LogHelper.error("RenderManager in unknown state!");
				if(batch.isDrawing()) {
					batch.end();
					break;
				} else if(shapeRenderer.isDrawing()) {
					shapeRenderer.end();
					break;
				} else {
					LogHelper.error("Unable to reconstruct state!!");
				}
				break;
		}
		state = State.READY;
	}

	/**
	 * draws the drawable at the given position and size
	 *
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(Drawable drawable, float x, float y, float width, float height) {
		drawable.draw(batch, x, y, width, height);
	}

	/**
	 * draws the TextureRegion at the given position and size
	 *
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(TextureRegion drawable, float x, float y, float width, float height) {
		batch.draw(drawable, x, y, width, height);
	}

	/**
	 * draws the TextureRegion at the given position
	 *
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(TextureRegion drawable, float x, float y) {
		batch.draw(drawable, x, y);
	}

	/**
	 * draws the Sprite at its position and size
	 *
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void draw(Sprite sprite) {
		sprite.draw(batch);
	}

	/**
	 * draws the given String
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void draw(String text, float x, float y) {
		getFont().draw(batch, text, x, y);
	}

	/**
	 * draws the given String
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void draw(String text, Color color, float x, float y) {
		getFont().setColor(color);
		getFont().draw(batch, text, x, y);
	}

	/**
	 * draws the given String
	 *
	 * note: Renderer has to be initialized and in the right state
	 */
	public void draw(String text, Color color, float x, float y, float size) {
		setFontScale(size);
		getFont().setColor(color);
		getFont().draw(batch, text, x, y);
	}

	/**
	 * draws a Rectangle at the given position and size
	 *
	 * note: the renderer has to be initialized and in the correct state
	 */
	public void rect(float x, float y, float width, float height) {
		shapeRenderer.rect(x, y, width, height);
	}

	public enum State {
		READY, BATCH, SHAPE_FILLED, SHAPE_OTHER, UNKNOWN, INVALID
	}
}
