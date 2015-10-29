package net.brenig.pixelescape.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import net.brenig.pixelescape.PixelEscape;
import net.brenig.pixelescape.game.GameConfiguration;
import net.brenig.pixelescape.lib.Reference;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(Reference.TARGET_RESOLUTION_X, Reference.TARGET_RESOLUTION_Y);
        }

        @Override
        public ApplicationListener getApplicationListener () {
                return new PixelEscape(new HtmlConfig());
        }

        public static class HtmlConfig extends GameConfiguration {
	        @Override
	        public boolean canQuitGame() {
		        return false;
	        }
        }
}