package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.StaticAssets.StarField;

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
    private ExtendViewport hudViewport;
    private SpriteBatch hudBatch;
    private OrthographicCamera hudCamera;

    private StarField starField;
    private Vector2 cursorPos;

    int level;
    boolean onePlayer;
    TextureAtlas atlas;

    float starFieldSpread;
    float startDelay;
    float cameraSlant;
    float timer;
    float xOffset;

    TextureRegion planetoidImage;
    TextureRegion pavJump;
    TextureRegion pavGun;
    TextureRegion textStart;
    NinePatch paneLightBlue;
    NinePatch paneDarkBlue;
    float xOffsetAlpha;
    float paneOpenAlpha;

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

        planetoidImage = atlas.createSprite("PLANETsm");
        pavJump = atlas.createSprite("PAVmd_JUMP");
        pavGun = atlas.createSprite("PAVmd_GUN");

        camera = new OrthographicCamera();
        camera.position.set(new Vector2(0, 0), 0f);
        viewport = new ExtendViewport(100, 100, camera);
        viewport.apply(true);

        renderer = new ShapeRenderer();
        renderer.setAutoShapeType(true);
        renderer.setProjectionMatrix(camera.combined);
        batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);

        hudCamera = new OrthographicCamera();
        hudViewport = new ExtendViewport(800, 800, hudCamera);
        hudBatch = new SpriteBatch();

        paneLightBlue = new NinePatch(atlas.createSprite("bluepane"), 15, 15, 15, 15);
        paneDarkBlue = new NinePatch(atlas.createSprite("bluepanedark"), 15, 15, 15, 15);
        textStart = atlas.createSprite("text_start");

        // TODO: Replace this with values based on level
        starField = new StarField(C.STARFIELD_CENTER, C.STARFIELD_STD);
        cursorPos = new Vector2(-100, 0);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        game.setScreen(game.gameScreen);
        return super.touchUp(screenX, screenY, pointer, button);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        hudViewport.update(width, height);
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        camera.rotate(-cameraSlant);
        cameraSlant = 0f;
    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {
        Gdx.app.log(TAG, "show()");
        Gdx.input.setInputProcessor(this);

        starFieldSpread = 0f;
        startDelay = 1.2f;
        cameraSlant = 0f;
        xOffsetAlpha = 0f;
        timer = 0f;
        xOffset = 100;
        paneOpenAlpha = 0f;
    }

    @Override
    public void render(float delta) {
        if (delta > C.DELTA_THRESHOLD) return;  // Avoids spikes in delta value.
        int halfWidth = pavJump.getRegionWidth()/2;

        timer += delta;
        startDelay -= delta;
        cursorPos.set(viewport.unproject(
                new Vector2(Gdx.input.getX(), Gdx.input.getY())
        )).sub(20, 0);

        camera.update();
        renderer.setProjectionMatrix(camera.combined);
        batch.setProjectionMatrix(camera.combined);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);


        /** STAR FIELD ANIMATION **/
        if (startDelay > 0) {
            // Two white bars collapse to center
            float jitter = MathUtils.random(0.005f);
            float separation = Interpolation.pow3Out.apply(0, 80f, startDelay);
            starField.render(renderer, new Vector2(0,separation), jitter);
            starField.render(renderer, new Vector2(0,-separation), jitter);
        } else {
            // White bar expands into star field
            starFieldSpread = Math.min(starFieldSpread + delta*0.5f, 1f);
            if (cameraSlant < 8) {
                camera.rotate(-cameraSlant);
                cameraSlant = Interpolation.pow3Out.apply(0, 8f, starFieldSpread);
                camera.rotate(cameraSlant);
            }
            starField.render(renderer, new Vector2(xOffset,0), Interpolation.pow3Out.apply(0, 0.6f, starFieldSpread));
        }
        if (starFieldSpread == 1f) {
            // Planetoid and character move into view
            float yHover = MathUtils.sinDeg(100 * timer);
            xOffsetAlpha = Math.min(xOffsetAlpha +delta/2f, 1f);
            xOffset = Interpolation.pow4.apply(100, 0, xOffsetAlpha);
            batch.begin();
            batch.draw(planetoidImage, -30+xOffset, -140, 100, 100);
            batch.draw(pavJump, 20-halfWidth+xOffset, yHover-halfWidth, halfWidth, halfWidth, halfWidth*2, halfWidth*2, .2f, .2f, 0);
            batch.draw(pavGun, 20-halfWidth+xOffset, yHover-halfWidth, halfWidth, halfWidth, halfWidth*2, halfWidth*2, .2f, .2f, MathUtils.clamp(cursorPos.angle()-90, 45, 135));
            batch.end();
        }
        if (xOffsetAlpha == 1f) {
            // Button appear
            paneOpenAlpha = MathUtils.clamp(paneOpenAlpha+delta*2, 0f, 1f);
            float paneOpen = Interpolation.linear.apply(16f, 100f, paneOpenAlpha);
            hudBatch.setProjectionMatrix(hudCamera.combined);
            hudBatch.begin();
            paneDarkBlue.draw(hudBatch, -200f-paneOpen, 0f, 2*paneOpen, 80f);
            if (paneOpenAlpha == 1f) hudBatch.draw(textStart, -250f, 25);
//            paneDarkBlue.draw(hudBatch, -300f, -100f, 200f, 80f);
            hudBatch.end();
        }
    }
}
