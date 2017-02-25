package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

public class Player extends B2DSprite{
    private int numMostachos;
    private int totalMostachos;

    public Player(Body body){
        super(body);

        Texture tex = MyGdxGame.res.getTexture("catsir");
        TextureRegion[] sprites = new TextureRegion[4];
        for(int i = 0; i < sprites.length; i++) {
            sprites[i] = new TextureRegion(tex, i * 32, 0, 32, 32);
        }

        setAnimation(sprites,1/12f);
        width = sprites[0].getRegionWidth();
        height = sprites[0].getRegionHeight();
    }

    public void cogerMostacho(){numMostachos++;}

    public int getNumMostachos(){return numMostachos;}

    public void setTotalMostachos(int t){totalMostachos=t;}

    public int getTotalMostachos(){return totalMostachos;}

}
