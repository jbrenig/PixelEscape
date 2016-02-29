package net.brenig.pixelescape.screen.ui.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import net.brenig.pixelescape.game.entity.player.EntityPlayer;
import net.brenig.pixelescape.game.entity.player.abliity.IAbility;
import net.brenig.pixelescape.lib.LogHelper;
import net.brenig.pixelescape.lib.Utils;
import net.brenig.pixelescape.screen.GameScreen;

public class AbilityWidget extends Button {


	private final GameScreen gameScreen;
	private EntityPlayer player;
	private IAbility currentAbility;

	private Drawable abilityIcon;

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

	private void initialize() {
		addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (!gameScreen.isGamePaused() && hasAbility() && currentAbility.cooldownRemaining() == 0) {
					currentAbility.onActivate(gameScreen, gameScreen.world, player);
				}
			}
		});
	}

	public void setPlayer(EntityPlayer player) {
		this.player = player;
		updateAbilityIcon();
	}

	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		final float itemFrame = getWidth() * item_frame_border;
		updateAbilityIcon();
		if(hasAbility()) {
			if (abilityIcon != null) {
				abilityIcon.draw(batch, getX() + itemFrame, getY() + itemFrame, getWidth() - itemFrame * 2, getHeight() - itemFrame * 2);
			}
			if(currentAbility.cooldownRemaining() != 0) {
				animCounter = ANIM_DURATION;
				batch.end();
				gameScreen.game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				Gdx.gl.glEnable(GL20.GL_BLEND);
				gameScreen.game.shapeRenderer.setColor(0.7F, 0.7F, 1, 0.4F);
				gameScreen.game.shapeRenderer.rect(getX() + itemFrame, getY() + itemFrame, getWidth() - itemFrame * 2, (getHeight() - itemFrame * 2) * currentAbility.cooldownRemaining());
				gameScreen.game.shapeRenderer.end();
				batch.begin();
			} else if(animCounter > 0) {
				animCounter -= Gdx.graphics.getDeltaTime();
				final float alpha = Utils.easeInAndOut(animCounter, ANIM_DURATION) * 0.7F;
				batch.end();
				gameScreen.game.shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
				Gdx.gl.glEnable(GL20.GL_BLEND);
				gameScreen.game.shapeRenderer.setColor(1, 1, 1, alpha);
				gameScreen.game.shapeRenderer.rect(getX() + itemFrame, getY() + itemFrame, getWidth() - itemFrame * 2, getHeight() - itemFrame * 2);
				gameScreen.game.shapeRenderer.end();
				batch.begin();
			}
		}
	}

	/**
	 * updates current ability and ability icon to the current ability of the player (if changed)
	 */
	public void updateAbilityIcon() {
		if(player.getCurrentAbility() != currentAbility) {
			currentAbility = player.getCurrentAbility();
			if(currentAbility == null) {
				abilityIcon = null;
			} else {
				abilityIcon = currentAbility.getDrawable(gameScreen.game.getGameAssets());
				if(abilityIcon == null) {
					LogHelper.warn("Ability " + currentAbility + " has no ability icon!!!");
				}
			}
		}
	}

	public boolean hasAbility() {
		return currentAbility != null;
	}

	public static class AbilityButtonStyle extends Button.ButtonStyle {

		public AbilityButtonStyle () {

		}

		public AbilityButtonStyle (Drawable up, Drawable down, Drawable checked) {
			super(up, down, checked);
		}

		public AbilityButtonStyle (AbilityButtonStyle style) {
			super(style);
		}
	}
}
