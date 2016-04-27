package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/25/2016.
 */
public class GameScreen extends InputAdapter implements Screen {
    private static final String TAG = GameScreen.class.getName();

    private MainGomp game;
    private ExtendViewport viewport;
    private OrthographicCamera camera;

    private World world = new World(new Vector2(0, 0), true);
    private Box2DDebugRenderer debugRenderer;

    private Planetoids planetoids;
    private Fighter player;
    private Fighter bandit;
    private int level;
    private float rotation;
    private boolean onePlayer;

    public GameScreen(MainGomp game) {
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        this.level = game.level;
        this.onePlayer = game.onePlayer;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(100, 100, camera);
        viewport.apply(true);
        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");
        Gdx.input.setInputProcessor(this);

        // Init level planetoids
        planetoids = new Planetoids(world, level);

        // Init player and opponent
        JsonValue p1Base = C.LEVEL_MAPS.get(level).get(0);
        JsonValue p2Base = C.LEVEL_MAPS.get(level).get(1);

        player = new Fighter( world,
                p1Base.getFloat("x"),
                p1Base.getFloat("y") + p1Base.getFloat("radius")
        );

        bandit = new Fighter( world,
                p2Base.getFloat("x"),
                p2Base.getFloat("y") - p2Base.getFloat("radius")
        );

        rotation = 0f;
    }

    @Override
    public void render(float delta) {
        player.applyGravity(planetoids);
        player.applyLandFriction(planetoids);
        player.queryPlayerInput();
        bandit.applyGravity(planetoids);
        bandit.applyLandFriction(planetoids);

        // Center camera on player.
        viewport.getCamera().position.set(player.body.getPosition(), 0f);

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
            viewport.getCamera().rotate(rotateBit, 0, 0, 1);
            rotation += rotateBit;  // Update current rotation reference
            rotation = (rotation + 360f) % 360f;  // Normalize degree value
        }
        viewport.getCamera().update();


        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        debugRenderer.render(world, viewport.getCamera().combined);

        world.step(delta, 6, 2);
    }

    public static float shortestRotation(float originDeg, float targetDeg) {
        // Ensure degrees are between 0 and 360.
        originDeg = (originDeg + 360f) % 360f;
        targetDeg = (targetDeg + 360f) % 360f;

        float rotateDeg = targetDeg - originDeg;
        if (rotateDeg > 180) rotateDeg -= 360f;
        else if (rotateDeg < -180) rotateDeg += 360f;
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
}
