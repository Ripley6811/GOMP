package com.mygdx.gomp;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.gomp.DynamicAssets.Fighter;

/**
 * Created by Jay on 5/10/2016.
 */
public class MyDebugger {
    private final Color FONT_COLOR = Color.YELLOW;
    private final float FONT_SCALE = 1f;
    private FPSLogger fpsLogger = new FPSLogger();

    BitmapFont font;
    SpriteBatch batch;
    GameScreen gameScreen;

    public MyDebugger(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.font = new BitmapFont();
        font.setColor(FONT_COLOR);
        font.getData().setScale(FONT_SCALE);
        font.getRegion().getTexture().setFilter(
                Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        this.batch = new SpriteBatch();
    }

    public void render() {
        fpsLogger.log();  // Requires log level of Application.LOG_INFO

        batch.begin();
        Fighter player = gameScreen.player;
        font.draw(batch, "Fighter vel: " + player.getVelocity().len(), 10, 180);
        font.draw(batch, "# Blobs: " + gameScreen.spaceBlobs.blobs.size, 10, 160);
        font.draw(batch, "Fighter grounded: " + player.isGrounded(), 10, 140);
        font.draw(batch, "Fighter gravity: " + player.getDownMagnitude(), 10, 120);
        font.draw(batch, "World Fixtures: " + gameScreen.world.getFixtureCount(), 10, 100);
        batch.end();
    }
}
