package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.StaticAssets.StarField;

import java.util.Random;

/**
 * Created by Jay on 5/3/2016.
 */
public class TitleScreen extends InputAdapter implements Screen {
    private static final String TAG = TitleScreen.class.getName();

    private MainGomp game;
    private ExtendViewport viewport;
    private OrthographicCamera camera;
    private ShapeRenderer renderer;
    private SpriteBatch batch;

    private StarField starField;
    private Vector2 cursorPos;
    private Interpolation interpolation;

    int level;
    boolean onePlayer;
    TextureAtlas atlas;

    float starFieldSpread;
    float startDelay;
    float cameraSlant;

    /**
     * TODO: Add swipe panel for options.
     * @param game
     */
    public TitleScreen(MainGomp game) {
        Gdx.input.setCatchBackKey(true);
        this.game = game;
        this.level = game.level;
        this.onePlayer = game.onePlayer;
        this.atlas = game.assets.get(C.MAIN_ATLAS);

        camera = new OrthographicCamera();
        camera.position.set(new Vector2(0, 0), 0f);
        viewport = new ExtendViewport(100, 100, camera);
        viewport.apply(true);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(camera.combined);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        // TODO: Replace this with values based on level
        starField = new StarField(C.STARFIELD_CENTER, C.STARFIELD_STD);
        cursorPos = new Vector2();
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        game.setScreen(game.gameScreen);
        return super.touchUp(screenX, screenY, pointer, button);
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

    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");
        Gdx.input.setInputProcessor(this);

        starFieldSpread = 0f;
        startDelay = 0.8f;
        cameraSlant = 0;
    }

    @Override
    public void render(float delta) {
        startDelay -= delta;

        camera.update();
        renderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        /** STAR FIELD ANIMATION **/
        if (startDelay > 0) {
            float jitter = new Random().nextFloat()*0.005f;
            float separation = Interpolation.pow3Out.apply(0, 80f, startDelay);
            starField.render(renderer, new Vector2(0,separation), jitter);
            starField.render(renderer, new Vector2(0,-separation), jitter);
        } else {
            starFieldSpread = Math.min(starFieldSpread + delta*0.5f, 1f);
            if (cameraSlant < 8) {
                camera.rotate(-cameraSlant);
                cameraSlant = Interpolation.pow3Out.apply(0, 8f, starFieldSpread);
                camera.rotate(cameraSlant);
            }
            starField.render(renderer, new Vector2(0,0), Interpolation.pow3Out.apply(0, 0.6f, starFieldSpread));
        }


    }
}
