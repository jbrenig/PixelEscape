package net.brenig.pixelescape.render.overlay;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import net.brenig.pixelescape.game.data.GameMode;
import net.brenig.pixelescape.lib.Reference;
import net.brenig.pixelescape.render.ui.general.HorizontalSpacer;
import net.brenig.pixelescape.render.ui.general.SwipeTabbedStack;
import net.brenig.pixelescape.render.ui.general.VerticalSpacer;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Overlay that is displayed at the first start of any gamemode explaining controls and features to the player
 */
public class TutorialOverlay extends OverlayWithUi {

	public TutorialOverlay(final GameScreen screen) {
		super(screen);

		final GameMode gameMode = screen.getGameMode();

		Table headLayout = stage.createHeadUiLayoutTable();
		Table table = stage.createContentUiLayoutTable();
		table.defaults().expand().fill();

		final SwipeTabbedStack stack = new SwipeTabbedStack();
		stack.setCycle(false);

		stack.add(screen.world.getPlayer().getMovementController().createTutorialWindow(getSkin()));
		stack.add(createButton("Test 1"));
		stack.add(createButton("Test 2"));
		stack.add(createButton("Test 3"));
		stack.add(createButton("Test 4"));
		stack.add(createButton("Test 5"));
		stack.add(createButton("Test 6"));


		table.add(stack).center().fill().expand();

		Table controls = new Table();

		Button buttonLeft =  new Button(getSkin(), "arrow_left");
		buttonLeft.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(stack.hasLastElement()) {
					stack.last();
				}
			}
		});
		buttonLeft.padLeft(40).padRight(40);

		Button buttonRight =  new Button(getSkin(), "arrow_right");
		buttonRight.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if(stack.hasNextElement()) {
					stack.next();
				}
			}
		});
		buttonRight.padLeft(40).padRight(40);

		controls.add(buttonLeft).padLeft(40);
		controls.add(new HorizontalSpacer());
		controls.add(buttonRight).padRight(40);

		table.add(new VerticalSpacer());
		table.row();
		table.add(controls);

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
