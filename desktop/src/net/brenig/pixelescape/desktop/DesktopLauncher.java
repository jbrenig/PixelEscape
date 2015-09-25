package net.brenig.pixelescape.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.lib.Reference;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "PixelEscape";
		config.width = Reference.TARGET_RESOLUTION_X;
		config.height = Reference.TARGET_RESOLUTION_Y;
		new LwjglApplication(new PixelEscape(), config);
	}
}
