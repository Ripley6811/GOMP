package com.mygdx.gomp;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gomp.Constants.C;

import java.util.Random;

/**
 * Created by Jay on 4/28/2016.
 */
public class StarField {
    private Vector2 center;  // Center of star distribution
    private Vector2 std;  // Standard deviation multiplier
    private Array<Vector2> stars;

    public StarField(Vector2 center, Vector2 std) {
        this.center = center;
        this.std = std;
        stars = new Array<Vector2>();
        createField();
    }

    public StarField(float cx, float cy, float stdx, float stdy) {
        this(new Vector2(cx, cy), new Vector2(stdx, stdy));
    }

    private void createField() {
        final int NUMBER_OF_STARS = C.NUMBER_OF_STARS;
        float widthGaussian;
        float heightGaussian;
        Random random = new Random();

        stars.clear();
        for (int i=0; i<NUMBER_OF_STARS; i++) {
            widthGaussian = (float) random.nextGaussian();
            heightGaussian = (float) random.nextGaussian();
            Vector2 star = new Vector2(
                    widthGaussian * std.x + center.x,
                    heightGaussian * std.y + center.y
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
}

