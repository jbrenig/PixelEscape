package net.brenig.pixelescape.render.ui.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.game.entity.impl.EntityPlayer;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.screen.GameScreen;

/**
 * Gui Button that triggers use of the current player Ability
 */
public class AbilityWidget extends Button {


	private final GameScreen gameScreen;
	private EntityPlayer player;

	private float animCounter = 0;
	private final static float ANIM_DURATION = 0.5F;

	private final static float item_frame_border = 0.21875F;

	public AbilityWidget(Skin skin, String style, EntityPlayer player, GameScreen gameScreen) {
		this(skin.get(style, AbilityButtonStyle.class), player, gameScreen);
	}

	public AbilityWidget(Skin skin, EntityPlayer player, GameScreen gameScreen) {
		this(skin.get(AbilityButtonStyle.class), player, gameScreen);
	}

	public AbilityWidget(AbilityButtonStyle style, EntityPlayer player, GameScreen gameScreen) {
		super(style);
		this.gameScreen = gameScreen;
		setPlayer(player);
		initialize();
	}

	public AbilityWidget(EntityPlayer player, GameScreen gameScreen) {
		super();
		this.gameScreen = gameScreen;
		setPlayer(player);
		initialize();
	}

	/**
	 * initialized click listener etc.
	 */
	private void initialize() {
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!gameScreen.isGamePaused() && player.hasAbility()) {
					player.useAbility();
				}
			}
		});
	}

	/**
	 * sets the player that has the ability
	 */
	public void setPlayer(EntityPlayer player) {
		this.player = player;
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (player.hasAbility()) {
			final float itemFrame = getWidth() * item_frame_border;
			final Drawable abilityIcon = player.getCurrentAbility().getDrawable(gameScreen.game.getGameAssets());
			if (abilityIcon != null) {
				abilityIcon.draw(batch, getX() + itemFrame, getY() + itemFrame, getWidth() - itemFrame * 2, getHeight() - itemFrame * 2);
			}
			if (player.getCooldownRemaining() != 0) {
				animCounter = ANIM_DURATION;
				batch.end();
				gameScreen.game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
				Gdx.gl.glEnable(GL20.GL_BLEND);
				gameScreen.game.getShapeRenderer().setColor(0.7F, 0.7F, 1, 0.4F);
				gameScreen.game.getShapeRenderer().rect(getX() + itemFrame, getY() + itemFrame, getWidth() - itemFrame * 2, (getHeight() - itemFrame * 2) * player.getCooldownRemainingScaled());
				gameScreen.game.getShapeRenderer().end();
				batch.begin();
			} else if (animCounter > 0) {
				animCounter -= Gdx.graphics.getDeltaTime();
				final float alpha = Utils.easeInAndOut(animCounter, ANIM_DURATION) * 0.7F;
				batch.end();
				gameScreen.game.getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
				Gdx.gl.glEnable(GL20.GL_BLEND);
				gameScreen.game.getShapeRenderer().setColor(1, 1, 1, alpha);
				gameScreen.game.getShapeRenderer().rect(getX() + itemFrame, getY() + itemFrame, getWidth() - itemFrame * 2, getHeight() - itemFrame * 2);
				gameScreen.game.getShapeRenderer().end();
				batch.begin();
			}
		}
	}

	public static class AbilityButtonStyle extends Button.ButtonStyle {

		public AbilityButtonStyle() {

		}

		public AbilityButtonStyle(Drawable up, Drawable down, Drawable checked) {
			super(up, down, checked);
		}

		public AbilityButtonStyle(AbilityButtonStyle style) {
			super(style);
		}
	}
}
