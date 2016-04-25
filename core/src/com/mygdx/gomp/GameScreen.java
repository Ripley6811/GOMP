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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/25/2016.
 */
public class GameScreen extends InputAdapter implements Screen {
    private static final String TAG = GameScreen.class.getName();

    MainGomp game;
    ExtendViewport viewport;
    OrthographicCamera camera;

    World world = new World(new Vector2(0, 0), true);
    Box2DDebugRenderer debugRenderer;

    Planetoids planetoids;
    Fighters fighters;
    int level;
    float rotation;

    public GameScreen(MainGomp game) {
        Gdx.input.setCatchBackKey(true);
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(100, 100, camera);
        viewport.apply(true);
        debugRenderer = new Box2DDebugRenderer();

        level = 1;
    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");
        Gdx.input.setInputProcessor(this);

        planetoids = new Planetoids(world, level);
        fighters = new Fighters(world, level);
        rotation = 0f;
    }

    public void queryInput() {
        Body player = fighters.fighters.get(0);
        Vector2 position = player.getPosition();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.applyLinearImpulse(-10f, 0f, position.x, position.y, true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.applyLinearImpulse(10f, 0f, position.x, position.y, true);
        }
    }

    @Override
    public void render(float delta) {
        queryInput();

        Body player = fighters.fighters.get(0);
        Vector2 gravity = planetoids.getGravityVector(player.getPosition());
        player.applyLinearImpulse(gravity, player.getPosition(), true);

        viewport.getCamera().position.set(player.getPosition(), 0f);
//        Gdx.app.log(TAG, "direction: " + viewport.getCamera().position);
        float rotateBit = gravity.angle() + 90 - rotation;
        rotateBit = Math.min(rotateBit, C.ROTATE_SPEED_CAP);
        rotateBit = Math.max(rotateBit, -C.ROTATE_SPEED_CAP);
        viewport.getCamera().rotate(rotateBit, 0, 0, 1);
        rotation += rotateBit;
        viewport.getCamera().update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);



        debugRenderer.render(world, viewport.getCamera().combined);


        world.step(1 / 60f, 6, 2);
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
