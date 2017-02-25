package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.game.MyGdxGame;

public class Mostacho extends B2DSprite{

    public Mostacho(Body body){
        super(body);
        Texture tex = MyGdxGame.res.getTexture("mostacho");
        TextureRegion[] sprites = TextureRegion.split(tex,32,32)[0];

        setAnimation(sprites,1/12f);
        width = sprites[0].getRegionWidth();
        height = sprites[0].getRegionHeight();
    }
}
