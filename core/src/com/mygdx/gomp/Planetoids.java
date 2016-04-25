package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.MassData;
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
    Array<Float> planetoidMasses;

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
            MassData massData = new MassData();
            massData.mass = calculateMass(radius);

            planetoidBodyDef.position.set(
                    planetoid.getFloat("x"),
                    planetoid.getFloat("y")
            );
            planetoidBody = world.createBody(planetoidBodyDef);
            planetoidShape.setRadius(radius);
            planetoidBody.createFixture(planetoidShape, 1f);
            planetoidShape.dispose();
            planetoidBody.setUserData(massData);

            planetoidBodies.add(planetoidBody);
        }
    }

    private float calculateMass(float radius) {
        float volume = C.PI_4_3RDS * radius * radius * radius;
        return C.PLANETOID_DENSITY * volume;
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
            float dist2 = toPlanet.len2() * 100000f;
//            Gdx.app.log(TAG, "dist2: " + dist2);
//            Gdx.app.log(TAG, "mass: " + ((MassData) planetoid.getUserData()).mass);
            float force = C.GRAVITY * ((MassData) planetoid.getUserData()).mass * C.FIGHTER_MASS / dist2;

//            Gdx.app.log(TAG, "force: " + force);
            toPlanet.setLength(force);
            totalForce.add(toPlanet);
        }

        return totalForce;
    }

}
