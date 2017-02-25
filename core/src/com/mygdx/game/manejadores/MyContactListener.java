package com.mygdx.game.manejadores;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.utils.Array;

public class MyContactListener implements ContactListener {

    private int numFootContacts;
    private Array<Body> bodiesToRemove;
    private boolean playerDead;

    public MyContactListener(){
        super();
        bodiesToRemove = new Array<Body>();
    }

    //Llamamos a este metodo cuando dos fixtures colisionan
    @Override
    public void beginContact(Contact contact) {
        //Obtenemos los dos fixtures que colisionan
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            numFootContacts++;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            numFootContacts++;
        }

        if(fa.getUserData() != null && fa.getUserData().equals("mostacho")){
            bodiesToRemove.add(fa.getBody());
        }
        if(fb.getUserData() != null && fb.getUserData().equals("mostacho")){
            bodiesToRemove.add(fb.getBody());
        }

        if(fa.getUserData() != null && fa.getUserData().equals("spike")) {
            playerDead = true;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("spike")) {
            playerDead = true;
        }

    }

    //Llamamos a este metodo cuando dos fixtures han dejado de colisionar
    @Override
    public void endContact(Contact contact) {
        Fixture fa = contact.getFixtureA();
        Fixture fb = contact.getFixtureB();

        if(fa == null || fb == null) return;

        if(fa.getUserData() != null && fa.getUserData().equals("foot")){
            numFootContacts--;
        }
        if(fb.getUserData() != null && fb.getUserData().equals("foot")){
            numFootContacts--;
        }
    }

    public boolean playerCanJump() { return numFootContacts > 0; }
    public Array<Body> getBodies() { return bodiesToRemove; }
    public boolean isPlayerDead() { return playerDead; }

    //Lo que se hace tras detectar la colision
    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {}
    //Lo que se hace tras haber manejado la colision
    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {}
}
