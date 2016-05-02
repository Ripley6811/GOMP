package com.mygdx.gomp;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.InputMapper.IM;

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
    private RayHandler rayHandler;

    private World world;
    private Box2DDebugRenderer debugRenderer;

    private TextureAtlas atlas;
    private StarField starField;
    private Planetoids planetoids;
    private Fighter player;
    private Fighter bandit;
    private Bullets bullets;
    private int level;
    private float rotation;
    private boolean onePlayer;
    private PointLight playerLOS;

    private Vector2 cursorPos;

    public GameScreen(MainGomp game) {
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        this.level = game.level;
        this.onePlayer = game.onePlayer;
        this.atlas = game.assets.get(C.MAIN_ATLAS);

        world = new World(new Vector2(0, 0), true);

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(100, 100, camera);
        viewport.apply(true);
        debugRenderer = new Box2DDebugRenderer();

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(camera.combined);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        // TODO: Replace this with values based on level
        starField = new StarField(C.STARFIELD_CENTER, C.STARFIELD_STD);
        cursorPos = new Vector2();

        /** Player view (LOS = line of sight) */
        rayHandler = new RayHandler(world);
        playerLOS = new PointLight(rayHandler, 500, Color.BLACK, 500, 0, 0);
        PointLight.setGlobalContactFilter(C.CAT_LIGHT, (short) 0, C.CAT_STATIC);
    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");
        Gdx.input.setInputProcessor(this);
        world.setContactListener(new ListenerClass());

        // Init level planetoids
        planetoids = new Planetoids(world, level);

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

        // Init player and opponent
        JsonValue p1Base = C.LEVEL_MAPS.get(level).get(0);
        JsonValue p2Base = C.LEVEL_MAPS.get(level).get(1);
        player = new Fighter(world, atlas,
                p1Base.getFloat("x"),
                p1Base.getFloat("y") + p1Base.getFloat("radius")
        );
        bandit = new Fighter(world, atlas,
                p2Base.getFloat("x"),
                p2Base.getFloat("y") - p2Base.getFloat("radius"),
                false
        );

        // Init bullet manager
        bullets = new Bullets(world, rayHandler);

        // Create reference to current rotation
        rotation = 0f;
    }

    @Override
    public boolean keyTyped(char character) {
        if (character == '+') camera.zoom -= 0.05f;

        if (character == '-') camera.zoom += 0.05f;

        return super.keyTyped(character);
    }

    public void queryWeaponsInput(float delta) {

        if (IM.isFiringPrimary() && player.laserReady()) {
            Vector2 playerPos = player.body.getPosition();
            Vector2 heading = new Vector2(cursorPos).sub(playerPos).setLength(C.LASER_START_OFFSET);
            bullets.addLaser(playerPos.add(heading), heading);
        }

        if (IM.isFiringSecondary() && player.grenadeReady()) {
            Vector2 playerPos = player.body.getPosition();
            Vector2 playerVel = player.body.getLinearVelocity();
            Vector2 heading = new Vector2(cursorPos).sub(playerPos).setLength(C.LASER_START_OFFSET);
            bullets.addGrenade(playerPos.add(heading), playerVel, heading);
        }
    }

    @Override
    public void render(float delta) {
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

        // Center camera on player.
        camera.position.set(player.body.getPosition(), 0f);

        // Rotate camera so that player stands on top of planetoid.
        if (!player.isFlying()) {
            float rotateBit = shortestRotation(rotation, player.down.angle() + 90);
            if (player.isGrounded()) {
                rotateBit = Math.min(rotateBit, C.ROTATE_SPEED_CAP);
                rotateBit = Math.max(rotateBit, -C.ROTATE_SPEED_CAP);
            } else {
                rotateBit = Math.min(rotateBit, C.ROTATE_FREEFALL_CAP);
                rotateBit = Math.max(rotateBit, -C.ROTATE_FREEFALL_CAP);
            }
            camera.rotate(rotateBit, 0, 0, 1);
            rotation += rotateBit;  // Update current rotation reference
            rotation = (rotation + 360f) % 360f;  // Normalize degree value
        }
        camera.update();
        renderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);
        rayHandler.setCombinedMatrix(camera);

        playerLOS.setPosition(player.body.getPosition());


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

//        debugRenderer.render(world, camera.combined);

//        long startTime = System.nanoTime();
        starField.render(batch, player.body.getPosition());  // Desktop faster
//        long endTime = System.nanoTime();
//        Gdx.app.debug(TAG, "star render time: " + ((endTime - startTime)/1000000f));
        planetoids.render(batch);
        bullets.render(renderer);
        bandit.render(delta, batch, new Vector2());
        player.render(delta, batch, cursorPos);
        rayHandler.updateAndRender();
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

    }

    @Override
    public void dispose() {

    }

    public class ListenerClass implements ContactListener {
        @Override
        public void preSolve(Contact contact, Manifold oldManifold) {

        }

        @Override
        public void postSolve(Contact contact, ContactImpulse impulse) {

        }

        @Override
        public void endContact(Contact contact) {

        }

        @Override
        public void beginContact(Contact contact) {
            /**
             * Check from bullet/weapon perspective instead.
             * Resolve bullet/weapon collisions.
             */
            if (contact.getFixtureA().getBody() == player.body) {
                Gdx.app.debug(TAG, "Contact Fixture A is Player");
            }
            if (contact.getFixtureB().getBody() == player.body) {
                Gdx.app.debug(TAG, "Contact Fixture B is Player");

            }
            if (contact.getFixtureB().getBody().isBullet()) {
                Bullet bullet = (Bullet) contact.getFixtureB().getUserData();
                Gdx.app.debug(TAG, "Contact Fixture B is Bullet:" + bullet.type);
                if (bullet.type == "LASER") {
                    bullet.hasCollided = true;
                }
                if (bullet.type == "GRENADE") {
                    if (contact.getFixtureA().getBody().getUserData() instanceof Circle) {

                    } else {
                        bullet.hasCollided = true;
                    }
                }
            }

            if (onePlayer) {
                if (contact.getFixtureA().getBody() == bandit.body) {
                    Gdx.app.debug(TAG, "Contact Fixture A is Bandit");
                }
                if (contact.getFixtureB().getBody() == bandit.body) {
                    Gdx.app.debug(TAG, "Contact Fixture B is Bandit");
                }
            }
        }
    };
}
