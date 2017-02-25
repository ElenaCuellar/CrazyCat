package com.mygdx.game.estados;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.manejadores.BoundedCamera;
import com.mygdx.game.manejadores.GameStateManager;

public abstract class GameState {
    public GameStateManager gsm;
    public MyGdxGame game;
    protected SpriteBatch sb;
    protected BoundedCamera cam;
    protected OrthographicCamera hudCam;

    protected GameState (GameStateManager gsm){
        this.gsm=gsm;
        game= gsm.game();
        sb = game.getSpriteBatch();
        cam = game.getCamera();
        hudCam=game.getHUDCamera();
    }

    public abstract void handleInput();
    public abstract void update(float dt);
    public abstract void render();
    public abstract void dispose();
}
