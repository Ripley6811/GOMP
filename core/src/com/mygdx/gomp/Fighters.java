package com.mygdx.gomp;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/25/2016.
 */
public class Fighters {
    private static final String TAG = Fighters.class.getName();

    Array<Body> fighters = new Array<Body>();

    /**
     *
     * @param world Box2D world object
     * @param level For loading a level map from file.
     */
    public Fighters(World world, int level) {
        JsonValue p1Base = C.LEVEL_MAPS.get(level).get(0);
        JsonValue p2Base = C.LEVEL_MAPS.get(level).get(1);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                p1Base.getFloat("x"),
                p1Base.getFloat("y") + p1Base.getFloat("radius")
        );

        Body body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(C.FIGHTER_HEIGHT / 2f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f; // Make it bounce a little bit

        body.createFixture(fixtureDef);

        circle.dispose();

        fighters.add(body);
    }
}
