package com.mygdx.gomp.StaticAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/25/2016.
 */
public class Planetoids {
    private static final String TAG = Planetoids.class.getName();

    private Array<Planetoid> planetoids;
    private Texture planetImage = new Texture(Gdx.files.internal("planet2.png"));

    /**
     * Reads in level map data and builds planetoid system as an array.
     * @param world Box2D world object
     * @param level For loading a level map from file.
     */
    public Planetoids(World world, int level) {
        JsonValue planetoidsData = C.LEVEL_MAPS.get(level);
        planetoids = new Array<Planetoid>(planetoidsData.size);

        for (JsonValue planetoid: planetoidsData) {
            Circle circle = new Circle(
                    planetoid.getFloat("x"),
                    planetoid.getFloat("y"),
                    planetoid.getFloat("radius")
            );
            planetoids.add(new Planetoid(world, circle));
        }
    }

    /**
     *
     * @param position Fighter position.
     * @param mass Fighter mass.
     * @return Gravitational force vector.
     */
    public Vector2 getGravityVector(Vector2 position, float mass) {
        Vector2 totalForce = new Vector2(0, 0);

        for (Planetoid planetoid: planetoids) {
            Vector2 toPlanet = new Vector2(planetoid.getPosition()).sub(position);
            float dist2 = toPlanet.len2() * C.INTERPLANETOID_DISTANCE_MULTIPLIER;
//            Gdx.app.log(TAG, "dist2: " + dist2);
//            Gdx.app.log(TAG, "mass: " + ((UserData) planetoid.getUserData()).mass);
            float force = C.GRAVITY * planetoid.getMass() * mass / dist2;

//            Gdx.app.log(TAG, "force: " + force);
            toPlanet.setLength(force);

            if (isOnSurface(planetoid, position)) {
                return toPlanet;
            } else {
                totalForce.add(toPlanet);
            }
        }

        return totalForce;
    }

    /**
     * Iterates through all planetoid bodies and returns true is position is on a surface.
     * @param position
     * @return
     */
    public boolean isOnAnySurface(Vector2 position) {
        for (Planetoid planetoid: planetoids) {
            if (isOnSurface(planetoid, position)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if vector position is considered on a particular planetoid body.
     * @param planetoid
     * @param position
     * @return
     */
    public boolean isOnSurface(Planetoid planetoid, Vector2 position) {
        float threshold = planetoid.getCircle().radius + C.FIGHTER_HEIGHT;

        if (position.dst(planetoid.getPosition()) < threshold * C.GROUNDED_THRESHOLD_MULTIPLIER) {
            return true;
        }
        return false;
    }

    public Planetoid get(int index) {
        return planetoids.get(index);
    }

    public Circle getCircle(int bodyIndex) {
        if (bodyIndex >= planetoids.size) return null;

        return planetoids.get(bodyIndex).getCircle();
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        for (Planetoid rock: planetoids) {
            Circle circle = rock.getCircle();
            batch.draw(planetImage,
                    circle.x - circle.radius,
                    circle.y - circle.radius,
                    2*circle.radius, 2*circle.radius);
        }
        batch.end();
    }
}
