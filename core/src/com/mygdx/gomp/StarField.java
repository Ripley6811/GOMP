package com.mygdx.gomp;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gomp.Constants.C;

import java.util.Random;

/**
 * Created by Jay on 4/28/2016.
 */
public class StarField {
    private final Vector2 center;  // Center of star distribution
    private final Vector2 std;  // Standard deviation multiplier
    private Array<Vector2> stars;
    private final float MILKY_WAY_STD = 5f;
    private Texture starfieldImage;

    public StarField(Vector2 center, Vector2 std) {
        this.center = center;
        this.std = std;
        stars = new Array<Vector2>();
        createField();
        starfieldImage = createTexture();
    }

    public StarField(float cx, float cy, float stdx, float stdy) {
        this(new Vector2(cx, cy), new Vector2(stdx, stdy));
    }

    private void createField() {
        final int HALF_STARS = C.NUMBER_OF_STARS / 2;
        float widthGaussian;
        float heightGaussian;
        Random random = new Random();

        stars.clear();
        for (int i=0; i<HALF_STARS; i++) {
            widthGaussian = (float) random.nextGaussian();
            heightGaussian = (float) random.nextGaussian();
            Vector2 star = new Vector2(
                    widthGaussian * std.x,
                    heightGaussian * std.y
            );
            stars.add(star);
        }
        for (int i=0; i<HALF_STARS; i++) {
            widthGaussian = (float) random.nextGaussian();
            heightGaussian = (float) random.nextGaussian();
            Vector2 star = new Vector2(
                    widthGaussian * std.x,
                    heightGaussian * MILKY_WAY_STD
            );
            stars.add(star);
        }
    }

    public void render(ShapeRenderer renderer, Vector2 offset) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(C.STAR_COLOR);
        for (Vector2 star: stars) {
            renderer.circle(
                    star.x + offset.x * C.STAR_PARALLAX_SCALE,
                    star.y + offset.y * C.STAR_PARALLAX_SCALE,
                    C.STAR_RADIUS
            );
        }
        renderer.end();
    }

    public void render(SpriteBatch batch, Vector2 offset) {
        batch.begin();
        batch.draw(
                starfieldImage,
                -3*std.x + center.x + offset.x * C.STAR_PARALLAX_SCALE,
                -3*std.y + center.y + offset.y * C.STAR_PARALLAX_SCALE,
                6*std.x, 6*std.y
        );
        batch.end();
    }

    private Texture createTexture() {
        int spread = 5;
        Pixmap pixmap = new Pixmap((int)std.x*6*spread, (int)std.y*6*spread, Pixmap.Format.RGBA8888);
        // NOTE: fillCircle blending is a known bug. Turn off temporarily.
        Pixmap.setBlending(Pixmap.Blending.None);
        // Star outer glow
        pixmap.setColor(C.STAR_COLOR);
        for (Vector2 star: stars) {
            pixmap.drawPixel(
                    (int) (spread*star.x + std.x * 3*spread),
                    (int) (spread*star.y + std.y * 3*spread)
            );
        }

        Pixmap.setBlending(Pixmap.Blending.SourceOver);
        return new Texture(pixmap);
    }
}

