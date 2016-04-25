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

    // Create world body from the definition.
    Array<Body> planetoidBodies = new Array<Body>();

    /**
     *
     * @param world Box2D world object
     * @param level For loading a level map from file.
     */
    public Planetoids(World world, int level) {


        for (JsonValue planetoid: C.LEVEL_MAPS.get(level)) {
            BodyDef planetoidBodyDef = new BodyDef();
            Body planetoidBody;
            CircleShape planetoidShape = new CircleShape();

            planetoidBodyDef.position.set(
                    new Vector2(
                            planetoid.getFloat("x"),
                            planetoid.getFloat("y")
                    )
            );
            planetoidBody = world.createBody(planetoidBodyDef);
            planetoidShape.setRadius(planetoid.getFloat("radius"));
            planetoidBody.createFixture(planetoidShape, 0f);
            planetoidShape.dispose();

            planetoidBodies.add(planetoidBody);
        }
    }
}
