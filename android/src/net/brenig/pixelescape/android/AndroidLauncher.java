package net.brenig.pixelescape.android;

import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.data.GameConfiguration;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		config.useAccelerometer = false;
		config.useCompass = false;
		initialize(new PixelEscape(new AndroidConfiguration()), config);
	}

	public static class AndroidConfiguration extends GameConfiguration {
		@Override
		public boolean useBiggerButtons() {
			return true;
		}
	}
}
