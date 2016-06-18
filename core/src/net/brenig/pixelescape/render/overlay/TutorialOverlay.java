package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Overlay that is displayed at the first start of any gamemode explaining controls and features to the player
 */
public class TutorialOverlay extends OverlayWithUi {

	public TutorialOverlay(GameScreen screen) {
		super(screen);
		Table headLayout = stage.createHeadUiLayoutTable();
		Table table = stage.createContentUiLayoutTable();
		table.defaults().expand().fill();

		SwipeTabbedStack stack = new SwipeTabbedStack();
		stack.add(createButton("Test 1"));
		stack.add(createButton("Test 2"));
		stack.add(createButton("Test 3"));
		stack.add(createButton("Test 4"));
		stack.add(createButton("Test 5"));
		stack.add(createButton("Test 6"));


		table.add(stack).center().fill().expand();

	}

	private Table createButton(String text) {
		Table out = new Table();
		out.add(new TextButton(text, getSkin())).center();
		return out;
	}


	@Override
	public void renderFirst(float delta) {
		//noinspection PointlessBooleanExpression,ConstantConditions
		if(Reference.SCREEN_TINT_STRENGTH > 0) {
			renderScreenTint(Reference.SCREEN_TINT_STRENGTH);
		}
	}

	@Override
	public void render(float delta) {
		super.render(delta);
	}

	@Override
	public boolean doesPauseGame() {
		return true;
	}
}
