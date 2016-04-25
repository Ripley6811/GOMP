package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.ExtendViewport;

/**
 * Created by Jay on 4/25/2016.
 */
public class GameScreen extends InputAdapter implements Screen {
    private static final String TAG = GameScreen.class.getName();

    MainGomp game;
    ExtendViewport viewport;
    Camera camera;

    World world = new World(new Vector2(0, 0), true);
    Box2DDebugRenderer debugRenderer;

    Planetoids planetoids;
    int level = 1;

    public GameScreen(MainGomp game) {
        Gdx.input.setCatchBackKey(true);
        this.game = game;

        camera = new OrthographicCamera();
        viewport = new ExtendViewport(100, 100, camera);
        viewport.apply(true);
        debugRenderer = new Box2DDebugRenderer();


    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");
        Gdx.input.setInputProcessor(this);

        planetoids = new Planetoids(world, level);
    }

    @Override
    public void render(float delta) {
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
