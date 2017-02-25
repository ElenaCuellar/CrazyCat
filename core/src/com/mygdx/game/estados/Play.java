package com.mygdx.game.estados;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.entidades.HUD;
import com.mygdx.game.entidades.Mostacho;
import com.mygdx.game.entidades.Player;
import com.mygdx.game.entidades.Spike;
import com.mygdx.game.manejadores.B2DVars;
import com.mygdx.game.manejadores.Background;
import com.mygdx.game.manejadores.BoundedCamera;
import com.mygdx.game.manejadores.GameStateManager;
import com.mygdx.game.manejadores.MyContactListener;
import com.mygdx.game.manejadores.MyInput;

import static com.mygdx.game.manejadores.B2DVars.PPM;

public class Play extends GameState{

    private boolean debug = false;

    private World world;
    private Box2DDebugRenderer b2dRenderer; //renderiza los bodies
    private BoundedCamera b2dCam;
    private MyContactListener cl;

    private TiledMap tileMap;
    private int tileMapWidth;
    private int tileMapHeight;
    private float tileSize;
    private OrthogonalTiledMapRenderer tmRenderer;

    private Player player;
    private Array<Mostacho> mostachos;
    private Array<Spike> spikes;

    private Background[] backgrounds;
    private HUD hud;

    public static int level;

    public Play(GameStateManager gsm) {
        super(gsm);

        //Configuracion de box2c
        world = new World(new Vector2(0,-7f),true);
        cl = new MyContactListener();
        world.setContactListener(cl);
        b2dRenderer = new Box2DDebugRenderer();

        //Crear player
        createPlayer();

        //Crear muros
        createWalls();
        cam.setBounds(0, tileMapWidth * tileSize, 0, tileMapHeight * tileSize);

        //Crear mostachos
        crearMostachos();
        player.setTotalMostachos(mostachos.size);

        //Crear obstaculos
        createSpikes();

        //Crear fondos
        Texture bgs = MyGdxGame.res.getTexture("bgs");
        TextureRegion sky = new TextureRegion(bgs, 0, 0, 320, 240);
        TextureRegion clouds = new TextureRegion(bgs, 0, 240, 320, 240);
        TextureRegion mountains = new TextureRegion(bgs, 0, 480, 320, 240);
        backgrounds = new Background[3];
        backgrounds[0] = new Background(sky, cam, 0f);
        backgrounds[1] = new Background(clouds, cam, 0.1f);
        backgrounds[2] = new Background(mountains, cam, 0.2f);

        //Crear HUD
        hud = new HUD(player);

        //Configurar b2dCam
        b2dCam = new BoundedCamera();
        b2dCam.setToOrtho(false, MyGdxGame.V_WIDTH/PPM,MyGdxGame.V_HEIGHT/PPM);
        b2dCam.setBounds(0, (tileMapWidth * tileSize) / PPM, 0, (tileMapHeight * tileSize) / PPM);
    }

    @Override
    public void update(float dt) {
        //manejar input
        handleInput();

        //actualizar b2d
        world.step(MyGdxGame.STEP,1,1);

        //Borrar mostachos
        Array<Body> bodies = cl.getBodies();
        for(int i=0;i<bodies.size;i++){
            Body b = bodies.get(i);
            mostachos.removeValue((Mostacho)b.getUserData(),true);
            world.destroyBody(b);
            player.cogerMostacho();
            MyGdxGame.res.getSound("coin").play();
        }
        bodies.clear();

        player.update(dt);

        //comprobar si el jugador ha ganado
        if(player.getBody().getPosition().x * PPM > tileMapWidth * tileSize) {
            MyGdxGame.res.getSound("levelselect").play();
            gsm.setState(GameStateManager.LEVEL_SELECT);
        }

        //si ha perdido
        if(player.getBody().getPosition().y < 0) {
            MyGdxGame.res.getSound("hit").play();
            gsm.setState(GameStateManager.MENU);
        }
        if(player.getBody().getLinearVelocity().x < 0.001f) {
            MyGdxGame.res.getSound("hit").play();
            gsm.setState(GameStateManager.MENU);
        }
        if(cl.isPlayerDead()) {
            MyGdxGame.res.getSound("hit").play();
            gsm.setState(GameStateManager.MENU);
        }

        //actualizar mostachos y obstaculos
        for(int i=0;i<mostachos.size;i++){
            mostachos.get(i).update(dt);
        }

        for(int i = 0; i < spikes.size; i++) {
            spikes.get(i).update(dt);
        }
    }

    @Override
    public void handleInput() {
        //salto del personaje si hemos pulsado el boton 1 o hemos tocado la pantalla
        if(MyInput.isPressed(MyInput.BUTTON1) || Gdx.input.justTouched()){
            playerJump();
        }
    }

    @Override
    public void render() {

        //Configurar la camara para que siga al player
        cam.position.set(player.getPosition().x * PPM + MyGdxGame.V_WIDTH / 4, MyGdxGame.V_HEIGHT / 2,0);
        cam.update();

        //dibujar fondos
        sb.setProjectionMatrix(hudCam.combined);
        for(int i = 0; i < backgrounds.length; i++) {
            backgrounds[i].render(sb);
        }

        //dibujar tile map
        tmRenderer.setView(cam);
        tmRenderer.render();

        //dibujar player
        sb.setProjectionMatrix(cam.combined);
        player.render(sb);

        //dibujar mostachos
        for(int i=0;i<mostachos.size;i++){
            mostachos.get(i).render(sb);
        }

        //dibujar obstaculos
        for(int i = 0; i < spikes.size; i++) {
            spikes.get(i).render(sb);
        }

        //dibujar HUD
        sb.setProjectionMatrix(hudCam.combined);
        hud.render(sb);

        //Crear el mundo box2d
        if(debug) {
            b2dCam.setPosition(player.getPosition().x + MyGdxGame.V_WIDTH / 4 / PPM, MyGdxGame.V_HEIGHT / 2 / PPM);
            b2dCam.update();
            b2dRenderer.render(world, b2dCam.combined);
        }

    }

    @Override
    public void dispose() {}

    private void createPlayer(){

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(60 /PPM, 120 /PPM);
        bdef.fixedRotation = true;
        bdef.linearVelocity.set(1f, 0f);
        Body body = world.createBody(bdef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(13/PPM,13/PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1;
        fdef.friction = 0;
        fdef.filter.categoryBits= B2DVars.BIT_PLAYER;
        fdef.filter.maskBits= B2DVars.BIT_RED |B2DVars.BIT_GREEN | B2DVars.BIT_BLUE | B2DVars.BIT_MOSTACHO | B2DVars.BIT_SPIKE;
        body.createFixture(fdef);
        shape.dispose();

        //crear sensor de pisadas
        shape = new PolygonShape();
        shape.setAsBox(13/PPM,3/PPM, new Vector2(0,-13/PPM),0);
        fdef.shape=shape;
        fdef.filter.categoryBits= B2DVars.BIT_PLAYER;
        fdef.filter.maskBits= B2DVars.BIT_RED |B2DVars.BIT_GREEN | B2DVars.BIT_BLUE;
        fdef.isSensor=true;
        body.createFixture(fdef).setUserData("foot");
        shape.dispose();

        //pasamos el body al player
        player = new Player(body);
        body.setUserData(player);

        //El player va a pesar 1kg
        MassData md = body.getMassData();
        md.mass = 1;
        body.setMassData(md);
    }

    private void createWalls(){
        //Cargar el tiledMap
        try{
            tileMap = new TmxMapLoader().load("android/assets/maps/level"+level+".tmx");
        }catch(Exception e){
            System.out.println("No encuentra el archivo android/assets/maps/level" + level + ".tmx");
            Gdx.app.exit();
        }

        tileMapWidth = (Integer) tileMap.getProperties().get("width");
        tileMapHeight = (Integer) tileMap.getProperties().get("height");
        tileSize = (Integer) tileMap.getProperties().get("tilewidth");
        tmRenderer = new OrthogonalTiledMapRenderer(tileMap);

        TiledMapTileLayer layer;

        layer = (TiledMapTileLayer) tileMap.getLayers().get("red");
        createBlocks(layer,B2DVars.BIT_RED);

        layer = (TiledMapTileLayer) tileMap.getLayers().get("green");
        createBlocks(layer,B2DVars.BIT_GREEN);

        layer = (TiledMapTileLayer) tileMap.getLayers().get("blue");
        createBlocks(layer,B2DVars.BIT_BLUE);
    }

    private void createBlocks(TiledMapTileLayer layer, short bits){

        //tamaño del tile
        float ts = layer.getTileWidth();

        //recorrer todas las celdas de la capa
        for(int row = 0; row < layer.getHeight(); row++) {
            for (int col = 0; col < layer.getWidth(); col++) {
                TiledMapTileLayer.Cell cell = layer.getCell(col,row);

                //comprobar si la celda existe
                if(cell == null) continue;
                if(cell.getTile()==null) continue;

                //crear un body y un fixture de la celda
                BodyDef bdef = new BodyDef();
                bdef.type = BodyDef.BodyType.StaticBody;
                bdef.position.set((col + 0.5f) * ts / PPM , (row + 0.5f) * ts / PPM);
                ChainShape cs = new ChainShape();
                Vector2[] v = new Vector2[3];
                v[0] = new Vector2(-ts / 2 / PPM, -ts / 2 / PPM);
                v[1] = new Vector2(-ts / 2 / PPM, ts / 2 / PPM);
                v[2] = new Vector2(ts / 2 / PPM, ts / 2 / PPM);
                cs.createChain(v);
                FixtureDef fd = new FixtureDef();
                fd.friction = 0;
                fd.shape = cs;
                fd.filter.categoryBits = bits;
                fd.filter.maskBits = B2DVars.BIT_PLAYER;
                fd.isSensor=false;
                world.createBody(bdef).createFixture(fd);
                cs.dispose();
            }
        }
    }

    private void crearMostachos(){

        mostachos = new Array<Mostacho>();

        MapLayer ml = tileMap.getLayers().get("mostachos");
        if(ml==null) return;

        for(MapObject mo: ml.getObjects()){
            BodyDef mdef = new BodyDef();
            mdef.type= BodyDef.BodyType.StaticBody;
            float x = mo.getProperties().get("x",Float.class) / PPM;
            float y = mo.getProperties().get("y",Float.class) / PPM;
            mdef.position.set(x,y);
            Body body = world.createBody(mdef);

            FixtureDef mfdef = new FixtureDef();
            PolygonShape pshape = new PolygonShape();
            pshape.setAsBox(13/PPM,13/PPM);
            mfdef.shape = pshape;
            mfdef.isSensor=true; //para poder pasar a traves del mostacho
            mfdef.filter.categoryBits = B2DVars.BIT_MOSTACHO;
            mfdef.filter.maskBits = B2DVars.BIT_PLAYER;

            body.createFixture(mfdef).setUserData("mostacho");
            Mostacho m = new Mostacho(body);
            body.setUserData(m);
            mostachos.add(m);
            pshape.dispose();
        }
    }

    private void createSpikes() {

        spikes = new Array<Spike>();

        MapLayer ml = tileMap.getLayers().get("spikes");
        if(ml == null) return;

        for(MapObject mo : ml.getObjects()) {
            BodyDef cdef = new BodyDef();
            cdef.type = BodyDef.BodyType.StaticBody;
            float x = mo.getProperties().get("x",Float.class) / PPM;
            float y = mo.getProperties().get("y",Float.class) / PPM;
            cdef.position.set(x, y);
            Body body = world.createBody(cdef);
            FixtureDef cfdef = new FixtureDef();
            CircleShape cshape = new CircleShape();
            cshape.setRadius(5 / PPM);
            cfdef.shape = cshape;
            cfdef.isSensor = true;
            cfdef.filter.categoryBits = B2DVars.BIT_SPIKE;
            cfdef.filter.maskBits = B2DVars.BIT_PLAYER;
            body.createFixture(cfdef).setUserData("spike");
            Spike s = new Spike(body);
            body.setUserData(s);
            spikes.add(s);
            cshape.dispose();
        }

    }

    //Aplicar fuerza al jugador
    private void playerJump() {
        if(cl.playerCanJump()) {
            player.getBody().setLinearVelocity(player.getBody().getLinearVelocity().x, 0);
            player.getBody().applyForceToCenter(0, 200, true);
            MyGdxGame.res.getSound("jump").play();
        }
    }
}
