package net.brenig.pixelescape.screen.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;

import net.brenig.pixelescape.lib.Reference;

/**
 * Manager that includes a rootTable to arrange ui Elements conveniently<br></br>
 * provides common methods to organize root layouts
 */
public class StageManager {
	
	protected Stage uiStage;
	protected Table rootTable;
	
	public StageManager(Viewport view) {
		uiStage = new Stage(view);
		uiStage.setDebugAll(Reference.DEBUG_UI);

		rootTable = new Table();
		rootTable.setFillParent(true);
		uiStage.addActor(rootTable);
	}

	public Stage getUiStage() {
		return uiStage;
	}

	/**
	 * @return the rootTable for futher modification
	 */
	public Table getRootTable() {
		return rootTable;
	}

	/**
	 * act-method
	 * @see Stage#act(float)
	 */
	public void act(float delta) {
		uiStage.act(delta);
	}

	/**
	 * draw the ui
	 * @see Stage#draw()
	 */
	public void draw() {
		uiStage.draw();
	}

	/**
	 * Special method to add actors directly to the stage<br>
	 * note: Actors do not get added to the rootTable
	 * @see Stage#addActor(Actor)
	 */
	public void addActorToStage(Actor actor) {
		uiStage.addActor(actor);
	}

	/**
	 * adds an actor to the rootTable
	 * @see Table#add(Actor)
	 */
	public void add(Actor actor) {
		rootTable.add(actor);
	}

	/**
	 * @see Stage#dispose()
	 */
	public void dispose() {
		uiStage.dispose();
	}

	public InputProcessor getInputProcessor() {
		return uiStage;
	}

	public Viewport getStageViewport() {
		return uiStage.getViewport();
	}

	/**
	 * updates viewport
	 * @see Viewport#update(int, int, boolean)
	 */
	public void updateViewport(int width, int height, boolean centerCamera) {
		getStageViewport().update(width, height, centerCamera);
	}

	/**
	 * updates Viewport to screen bounds and centers camera
	 * @see Viewport#update(int, int, boolean)
	 */
	public void updateViewportToScreen() {
		updateViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
	}
}
