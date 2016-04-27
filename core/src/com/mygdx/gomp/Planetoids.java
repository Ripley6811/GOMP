package com.mygdx.gomp;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/25/2016.
 */
public class Planetoids {
    private static final String TAG = Planetoids.class.getName();

    Array<Body> planetoidBodies;

    /**
     *
     * @param world Box2D world object
     * @param level For loading a level map from file.
     */
    public Planetoids(World world, int level) {
        JsonValue planetoids = C.LEVEL_MAPS.get(level);
        planetoidBodies = new Array<Body>(planetoids.size);

        for (JsonValue planetoid: planetoids) {
            BodyDef planetoidBodyDef = new BodyDef();
            Body planetoidBody;
            CircleShape planetoidShape = new CircleShape();
            float radius = planetoid.getFloat("radius");

            planetoidBodyDef.position.set(
                    planetoid.getFloat("x"),
                    planetoid.getFloat("y")
            );
            planetoidBody = world.createBody(planetoidBodyDef);
            planetoidShape.setRadius(radius);
            planetoidBody.createFixture(planetoidShape, 1f);
            planetoidShape.dispose();
            planetoidBody.setUserData(new UserData(radius));

            planetoidBodies.add(planetoidBody);
        }
    }

    /**
     *
     * @param position Fighter position.
     * @return Gravitational force vector.
     */
    public Vector2 getGravityVector(Vector2 position) {
        Vector2 totalForce = new Vector2(0, 0);

        for (Body planetoid: planetoidBodies) {
            Vector2 toPlanet = new Vector2(planetoid.getPosition()).sub(position);
            float dist2 = toPlanet.len2() * C.INTERPLANETOID_DISTANCE_MULTIPLIER;
//            Gdx.app.log(TAG, "dist2: " + dist2);
//            Gdx.app.log(TAG, "mass: " + ((UserData) planetoid.getUserData()).mass);
            float force = C.GRAVITY * ((UserData) planetoid.getUserData()).mass * C.FIGHTER_MASS / dist2;

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

    public boolean isOnAnySurface(Vector2 position) {
        for (Body planetoid: planetoidBodies) {
            if (isOnSurface(planetoid, position)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnSurface(Body planetoid, Vector2 position) {
        float threshold = ((UserData) planetoid.getUserData()).radius;
        threshold += C.FIGHTER_HEIGHT;

        if (position.dst(planetoid.getPosition()) < threshold * C.GROUNDED_THRESHOLD_MULTIPLIER) {
            return true;
        }
        return false;
    }

    /**
     * Data object for planetoid Body instance using the
     * setUserData method.
     */
    private class UserData {
        public float radius;
        public float mass;

        public UserData(float radius) {
            this.radius = radius;
            this.mass = calculateMass(radius);
        }
    }

    private float calculateMass(float radius) {
        // Volume based
//        float volume = C.PI_4_3RDS * radius * radius * radius;
        // Area based
        float volume = C.PI * radius * radius;
        return C.PLANETOID_DENSITY * volume;
    }
}
