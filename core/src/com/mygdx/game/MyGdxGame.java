package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.manejadores.BoundedCamera;
import com.mygdx.game.manejadores.Content;
import com.mygdx.game.manejadores.GameStateManager;
import com.mygdx.game.manejadores.MyInput;
import com.mygdx.game.manejadores.MyInputProcessor;

public class MyGdxGame extends ApplicationAdapter {

	public static final String TITULO = "Crazy Cat";
	public static final int V_WIDTH = 320;
	public static final int V_HEIGHT = 240;
	public static final int ESCALADO = 2;
	public static final float STEP = 1/60f;

	private SpriteBatch sb;
	private BoundedCamera cam;
	private OrthographicCamera hudCam;

	private GameStateManager gsm;

	public static Content res;

	@Override
	public void create () {
		Gdx.input.setInputProcessor(new MyInputProcessor());

		res = new Content();
		//cargar una textura
		res.loadTexture("images/menu.png");
		res.loadTexture("images/bgs.png");
		res.loadTexture("images/catsir.png");
		res.loadTexture("images/mostacho.png");
		res.loadTexture("images/hud.png");
		res.loadTexture("images/spikes.png");

		res.loadSound("audio/sfx/jump.wav");
		res.loadSound("audio/sfx/coin.mp3");
		res.loadSound("audio/sfx/levelselect.wav");
		res.loadSound("audio/sfx/hit.wav");

		res.loadMusic("audio/music/song.mp3");
		res.getMusic("song").setLooping(true);
		res.getMusic("song").setVolume(0.5f);
		res.getMusic("song").play();

		cam = new BoundedCamera();
		cam.setToOrtho(false, V_WIDTH, V_HEIGHT);
		hudCam = new OrthographicCamera();
		hudCam.setToOrtho(false, V_WIDTH, V_HEIGHT);

		sb = new SpriteBatch();

		gsm = new GameStateManager(this);

	}

	@Override
	public void render () {
		Gdx.graphics.setTitle(TITULO + " -- FPS: " + Gdx.graphics.getFramesPerSecond());

		gsm.update(Gdx.graphics.getDeltaTime());
		gsm.render();
		MyInput.update();
	}
	
	@Override
	public void dispose () {
		res.removeAll();
	}

	public SpriteBatch getSpriteBatch(){return sb;}
	public BoundedCamera getCamera(){return cam;}
	public OrthographicCamera getHUDCamera(){return hudCam;}
}
