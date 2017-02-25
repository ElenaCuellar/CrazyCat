package com.mygdx.game.manejadores;

import com.badlogic.gdx.Input.*;
import com.badlogic.gdx.InputAdapter;

public class MyInputProcessor extends InputAdapter {

    public boolean mouseMoved(int x, int y) {
        MyInput.x = x;
        MyInput.y = y;
        return true;
    }

    public boolean touchDragged(int x, int y, int pointer) {
        MyInput.x = x;
        MyInput.y = y;
        MyInput.down = true;
        return true;
    }

    public boolean touchDown(int x, int y, int pointer, int button) {
        MyInput.x = x;
        MyInput.y = y;
        MyInput.down = true;
        return true;
    }

    public boolean touchUp(int x, int y, int pointer, int button) {
        MyInput.x = x;
        MyInput.y = y;
        MyInput.down = false;
        return true;
    }

    public boolean keyDown(int k) {
        //si pulsamos la tecla Z en el teclado,hemos pulsado el boton 1
        if(k == Keys.Z) MyInput.setKey(MyInput.BUTTON1, true);
        return true;
    }

    public boolean keyUp(int k) {
        if(k == Keys.Z) MyInput.setKey(MyInput.BUTTON1, false);
        return true;
    }
}
