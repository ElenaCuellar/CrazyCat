package com.mygdx.game.estados;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.manejadores.Animation;
import com.mygdx.game.manejadores.B2DVars;
import com.mygdx.game.manejadores.Background;
import com.mygdx.game.manejadores.GameButton;
import com.mygdx.game.manejadores.GameStateManager;

import static com.mygdx.game.manejadores.B2DVars.PPM;

public class Menu extends GameState {
    private boolean debug = false;

    private Background bg;
    private Animation animation;
    private GameButton playButton;

    private World world;
    private Box2DDebugRenderer b2dRenderer;

    public Menu(GameStateManager gsm) {

        super(gsm);

        Texture tex = MyGdxGame.res.getTexture("menu");
        bg = new Background(new TextureRegion(tex), cam, 1f);
        bg.setVector(-20, 0);

        tex = MyGdxGame.res.getTexture("catsir");
        TextureRegion[] reg = new TextureRegion[4];
        for(int i = 0; i < reg.length; i++) {
            reg[i] = new TextureRegion(tex, i * 32, 0, 32, 32);
        }
        animation = new Animation(reg, 1 / 12f);

        tex = MyGdxGame.res.getTexture("hud");
        playButton = new GameButton(new TextureRegion(tex, 0, 34, 58, 27), 160, 100, cam);

        cam.setToOrtho(false, MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT);

        world = new World(new Vector2(0, -9.8f * 5), true);
        b2dRenderer = new Box2DDebugRenderer();

        createTitleBodies();

    }

    private void createTitleBodies() {

        // top platform
        BodyDef tpbdef = new BodyDef();
        tpbdef.type = BodyDef.BodyType.StaticBody;
        tpbdef.position.set(160 / PPM, 180 / PPM);
        Body tpbody = world.createBody(tpbdef);
        PolygonShape tpshape = new PolygonShape();
        tpshape.setAsBox(120 / PPM, 1 / PPM);
        FixtureDef tpfdef = new FixtureDef();
        tpfdef.shape = tpshape;
        tpfdef.filter.categoryBits = B2DVars.BIT_TOP_PLATFORM;
        tpfdef.filter.maskBits = B2DVars.BIT_TOP_BLOCK;
        tpbody.createFixture(tpfdef);
        tpshape.dispose();

        // bottom platform
        BodyDef bpbdef = new BodyDef();
        bpbdef.type = BodyDef.BodyType.StaticBody;
        bpbdef.position.set(160 / PPM, 130 / PPM);
        Body bpbody = world.createBody(bpbdef);
        PolygonShape bpshape = new PolygonShape();
        bpshape.setAsBox(120 / PPM, 1 / PPM);
        FixtureDef bpfdef = new FixtureDef();
        bpfdef.shape = bpshape;
        bpfdef.filter.categoryBits = B2DVars.BIT_BOTTOM_PLATFORM;
        bpfdef.filter.maskBits = B2DVars.BIT_BOTTOM_BLOCK;
        bpbody.createFixture(bpfdef);
        bpshape.dispose();
    }

    public void handleInput() {

        // mouse/touch input
        if(playButton.isClicked()) {
            MyGdxGame.res.getSound("levelselect").play();
            gsm.setState(GameStateManager.LEVEL_SELECT);
        }

    }

    public void update(float dt) {

        handleInput();

        world.step(dt / 5, 8, 3);

        bg.update(dt);
        animation.update(dt);

        playButton.update(dt);

    }

    public void render() {

        sb.setProjectionMatrix(cam.combined);

        // dibujar fondo
        bg.render(sb);

        //dibujar boton
        playButton.render(sb);

        //dibujar gato
        sb.begin();
        sb.draw(animation.getFrame(), 146, 31);
        sb.end();

        // debug dibujar box2d
        if(debug) {
            cam.setToOrtho(false, MyGdxGame.V_WIDTH / PPM, MyGdxGame.V_HEIGHT / PPM);
            b2dRenderer.render(world, cam.combined);
            cam.setToOrtho(false, MyGdxGame.V_WIDTH, MyGdxGame.V_HEIGHT);
        }
    }

    public void dispose() {
    }
}
