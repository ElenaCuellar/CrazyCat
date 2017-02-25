package com.mygdx.game.entidades;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.game.MyGdxGame;

public class HUD {
    private Player player;

    private TextureRegion container;
    private TextureRegion[] font;

    public HUD(Player player) {

        this.player = player;

        Texture tex = MyGdxGame.res.getTexture("hud");

        container = new TextureRegion(tex, 0, 0, 32, 32);

        font = new TextureRegion[11];
        for(int i = 0; i < 6; i++) {
            font[i] = new TextureRegion(tex, 32 + i * 9, 16, 9, 9);
        }
        for(int i = 0; i < 5; i++) {
            font[i + 6] = new TextureRegion(tex, 32 + i * 9, 25, 9, 9);
        }

    }

    public void render(SpriteBatch sb) {

        sb.begin();

        //dibujar contenedor
        sb.draw(container, 32, 200);

        //dibujar cantidad de mostachos
        drawString(sb, player.getNumMostachos() + " / " + player.getTotalMostachos(), 132, 211);

        sb.end();

    }

    private void drawString(SpriteBatch sb, String s, float x, float y) {
        for(int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if(c == '/') c = 10;
            else if(c >= '0' && c <= '9') c -= '0';
            else continue;
            sb.draw(font[c], x + i * 9, y);
        }
    }
}
