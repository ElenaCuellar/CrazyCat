package com.mygdx.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.mygdx.game.MyGdxGame;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = MyGdxGame.TITULO;
		config.width = MyGdxGame.V_WIDTH*MyGdxGame.ESCALADO;
		config.height = MyGdxGame.V_HEIGHT*MyGdxGame.ESCALADO;

		//cfg.useGL20 = false; --> no hace falta
		new LwjglApplication(new MyGdxGame(), config);
	}
}
