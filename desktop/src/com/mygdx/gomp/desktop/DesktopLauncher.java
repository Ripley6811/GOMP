package com.mygdx.gomp.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.gomp.MainGomp;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
		cfg.title = "GOMP";
		cfg.height = 800;
		cfg.width = 800;
		new LwjglApplication(new MainGomp(), cfg);
	}
}
