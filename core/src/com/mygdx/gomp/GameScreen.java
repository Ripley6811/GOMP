package com.mygdx.gomp;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.DynamicAssets.BlobShip;
import com.mygdx.gomp.DynamicAssets.Bullets;
import com.mygdx.gomp.DynamicAssets.Fighter;
import com.mygdx.gomp.DynamicAssets.SpaceBlobs;
import com.mygdx.gomp.InputMapper.IM;
import com.mygdx.gomp.StaticAssets.Planetoids;
import com.mygdx.gomp.StaticAssets.StarField;

import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Created by Jay on 4/25/2016.
 */
public class GameScreen extends InputAdapter implements Screen {
    private static final String TAG = GameScreen.class.getName();

    private MainGomp game;
    private ExtendViewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer renderer;
    private SpriteBatch batch;
    private SpriteBatch hudBatch;
    private RayHandler rayHandler;

    protected World world;
    private Box2DDebugRenderer debugRenderer;
    private MyDebugger myDebugger;

    private TextureAtlas atlas;
    private StarField starField;
    protected Planetoids planetoids;
    protected Fighter player;
    protected Fighter bandit;
    private Array<BlobShip> blobShips;
    protected SpaceBlobs spaceBlobs;
    private float blobSpawnTimer = 0f;
    protected Bullets bullets;
    private int level;
    private float rotation;
    protected boolean onePlayer;
    private PointLight playerLOS;

    private Vector2 cursorPos;

    private NinePatch greyBarLeft;
    private NinePatch redBarLeft;
    private NinePatch blueBarLeft;
    private NinePatch greyBarRight;
    private NinePatch redBarRight;
    private NinePatch blueBarRight;

    public GameScreen(MainGomp game) {
        this.game = game;
        this.level = game.level;
        this.onePlayer = game.onePlayer;
        this.atlas = game.assets.get(C.MAIN_ATLAS);
        this.myDebugger = new MyDebugger(this);

        world = new World(new Vector2(0, 0), true);

        this.initViewports();

        // TODO: Replace this with values based on level
        starField = new StarField(C.STARFIELD_CENTER, C.STARFIELD_STD);
        cursorPos = new Vector2();

        /** Player view (LOS = line of sight) */
        rayHandler = new RayHandler(world);
        playerLOS = new PointLight(rayHandler, 500, Color.BLACK, 500, 0, 0);
        PointLight.setGlobalContactFilter(C.CAT_LIGHT, (short) 0, C.CAT_STATIC);

        /** Load HUD sprites **/
        greyBarLeft = new NinePatch(atlas.createSprite("ninepatch_grey_bar"),
                1, 30, 16, 16);
        greyBarRight = new NinePatch(atlas.createSprite("ninepatch_grey_bar_right"),
                30, 1, 16, 16);

        redBarLeft = new NinePatch(atlas.createSprite("ninepatch_red_bar"),
                1, 30, 16, 16);
        redBarRight = new NinePatch(atlas.createSprite("ninepatch_red_bar_right"),
                30, 1, 16, 16);

        blueBarLeft = new NinePatch(atlas.createSprite("ninepatch_blue_bar"),
                1, 30, 16, 16);
        blueBarRight = new NinePatch(atlas.createSprite("ninepatch_blue_bar_right"),
                30, 1, 16, 16);
    }

    private void initViewports() {
        debugRenderer = new Box2DDebugRenderer();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(100, 100, camera);
        viewport.apply(true);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(camera.combined);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
        hudBatch = new SpriteBatch();
    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");
        Gdx.input.setCatchBackKey(true);
        Gdx.input.setInputProcessor(this);
        world.setContactListener(new ListenerClass(this));

        /**
         * IMPORTANT: Do not change order that objects are added to world.
         * Contact resolution expects the following order:
         *      Planetoids (surfaces)
         *      Characters (vehicles)
         *      Bullets are last
         */
        // Init level planetoids
        planetoids = new Planetoids(world, level);

        // Init player and opponent
        player = new Fighter(world, atlas, planetoids.get(0));
        bandit = new Fighter(world, atlas, planetoids.get(1), false);

        spaceBlobs = new SpaceBlobs(world, atlas);
        blobShips = new Array<BlobShip>();
        blobShips.add(new BlobShip(world, atlas, spaceBlobs, new Vector2(100, 100)));
        blobShips.add(new BlobShip(world, atlas, spaceBlobs, new Vector2(120, -100)));

        // Init bullet manager
        Animation explosionAnimation = new Animation(
                C.EXPLOSION_FRAME_RATE, atlas.createSprites("explosion"));
        bullets = new Bullets(world, rayHandler, explosionAnimation);

        // Create reference to current rotation
        rotation = 0f;

        /** ADD LIGHTING */
        // NOTE: Many lights slows down HTML/WebGL version
        // Base planet view
        Circle circle = planetoids.getCircle(0);
        if (Gdx.app.getType() == Application.ApplicationType.WebGL) {
            // Single light in base planet for HTML version
            Vector2 offset = new Vector2(0, 0);
            new PointLight(rayHandler, 250, Color.BLACK, 400, circle.x + offset.x, circle.y + offset.y);
        } else {
            Vector2 offset = new Vector2(0, circle.radius * 1.1f);
            for (int i = 0; i < 360; i += 30) {
                offset.setAngle(i);
                new PointLight(rayHandler, 240, Color.BLACK, 400, circle.x + offset.x, circle.y + offset.y);
            }
        }

    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '+') camera.zoom -= 0.05f;

        if (character == '-') camera.zoom += 0.05f;

        return super.keyTyped(character);
    }

    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE) game.setScreen(game.titleScreen);
        return super.keyDown(keycode);
    }

    public void queryWeaponsInput(float delta) {

        if (IM.isFiringPrimary() && player.fireLaser()) {
            Vector2 playerPos = player.getPosition();
            Vector2 heading = new Vector2(cursorPos).sub(playerPos).setLength(C.LASER_START_OFFSET);
            bullets.addLaser(playerPos.add(heading), heading);
        }

        if (IM.isFiringSecondary() && player.fireGrenade()) {
            Vector2 playerPos = player.getPosition();
            Vector2 playerVel = player.getVelocity();
            Vector2 heading = new Vector2(cursorPos).sub(playerPos).setLength(C.LASER_START_OFFSET);
            bullets.addGrenade(playerPos.add(heading), playerVel, heading);
        }
    }

    @Override
    public void render(float delta) {
        if (delta > C.DELTA_THRESHOLD) return;  // Avoids spikes in delta value.
        cursorPos.set(viewport.unproject(
                new Vector2(Gdx.input.getX(), Gdx.input.getY())
        ));

        bullets.applyGravity(planetoids);
        player.applyGravity(planetoids);
        player.applyLandFriction(planetoids);
        if (onePlayer) {
            bandit.applyGravity(planetoids);
            bandit.applyLandFriction(planetoids);
        }
        player.queryPlayerMovementInput(delta);

        queryWeaponsInput(delta);

        // TODO: temporary blob spawning. Change to Blob Meteor/Blobship
        for (BlobShip blobShip: blobShips) {
            blobShip.applyGravity(planetoids);
            blobShip.update(delta);
        }
        spaceBlobs.applyGravity(planetoids);

        // Center camera on player.
        camera.position.set(player.getPosition(), 0f);

        // Rotate camera so that player stands on top of planetoid.
        if (!player.isFlying()) {
            float rotateBit = shortestRotation(rotation, player.getDownAngle() + 90);
            if (player.isGrounded()) {
                rotateBit = MathUtils.clamp(rotateBit, -C.ROTATE_SPEED_CAP, C.ROTATE_SPEED_CAP);
            } else {
                rotateBit = MathUtils.clamp(rotateBit, -C.ROTATE_FREEFALL_CAP, C.ROTATE_FREEFALL_CAP);
            }
            camera.rotate(rotateBit, 0, 0, 1);
            rotation += rotateBit;  // Update current rotation reference
            rotation = (rotation + 360f) % 360f;  // Normalize degree value
        }
        camera.update();
        renderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        rayHandler.setCombinedMatrix(camera);

        playerLOS.setPosition(player.getPosition());


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        debugRenderer.render(world, camera.combined);

//        long startTime = System.nanoTime();
        starField.render(batch, player.getPosition());  // Desktop faster
//        long endTime = System.nanoTime();
//        Gdx.app.debug(TAG, "star render time: " + ((endTime - startTime)/1000000f));
        planetoids.render(batch);
        bullets.render(renderer, batch);
        bandit.render(delta, batch, new Vector2());
        player.render(delta, batch, cursorPos);
        for (BlobShip blobShip: blobShips) blobShip.render(batch);
        spaceBlobs.render(batch);
        rayHandler.updateAndRender();


        /** DEBUG GRAVITY FIELD **/
//        renderer.begin(ShapeRenderer.ShapeType.Line);
//        renderer.setColor(Color.WHITE);
//        for (int i=0; i<Gdx.graphics.getWidth()/10; i+=5) {
//            for (int j=-100; j<Gdx.graphics.getHeight()/10; j+=5) {
//                Vector2 grav = planetoids.getGravityVector(new Vector2(i, j), 1000f).scl(0.01f);
//                renderer.line(i, j, i+grav.x, j+grav.y, Color.WHITE, Color.RED);
//            }
//        }
//        renderer.end();


        /** HUD */
        hudBatch.begin();
        greyBarLeft.draw(hudBatch, 0, 0, C.FIGHTER_MAX_HEALTH * 5, greyBarLeft.getTotalHeight());
        redBarLeft.draw(hudBatch, 0, 0, player.getHealth() * 5, greyBarLeft.getTotalHeight());
        greyBarLeft.draw(hudBatch, 0, 32, C.FIGHTER_MAX_ENERGY * 2, greyBarLeft.getTotalHeight());
        blueBarLeft.draw(hudBatch, 0, 32, player.getEnergy() * 2, greyBarLeft.getTotalHeight());
        int side = viewport.getScreenWidth();
        // TODO: Get the visible width and fit HUD properly
        greyBarRight.draw(hudBatch, 1200-(C.FIGHTER_MAX_HEALTH * 5), 0, C.FIGHTER_MAX_HEALTH * 5, greyBarRight.getTotalHeight());
        redBarRight.draw(hudBatch, 1200-(bandit.getHealth() * 5), 0, bandit.getHealth() * 5, greyBarRight.getTotalHeight());
        greyBarRight.draw(hudBatch, 1200-(C.FIGHTER_MAX_ENERGY * 2), 32, C.FIGHTER_MAX_ENERGY * 2, greyBarRight.getTotalHeight());
        blueBarRight.draw(hudBatch, 1200-(bandit.getEnergy() * 2), 32, bandit.getEnergy() * 2, greyBarRight.getTotalHeight());
        hudBatch.end();



        myDebugger.render();
        // TODO: Draw sunny side of planetoids

        world.step(delta, 6, 2);
    }

    /**
     * Calculates shortest degree change between a pair of degree values.
     * @param originDeg Starting angle
     * @param targetDeg Target angle
     * @return Degrees to reach target
     */
    public static float shortestRotation(float originDeg, float targetDeg) {
        // Calculate shortest rotation
        float rotateDeg = targetDeg - originDeg;
        while (rotateDeg < -180) rotateDeg += 360f;
        while (rotateDeg >= 180) rotateDeg -= 360f;
        return rotateDeg;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        // Undo rotation
        camera.rotate(rotation, 0, 0, -1);
        rotation = 0f;
    }

    @Override
    public void dispose() {

    }

}
