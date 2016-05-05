package com.mygdx.gomp.StaticAssets;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 5/5/2016.
 */
public class Planetoid {
    private static final String TAG = Planetoid.class.getName();
    private Body body;
    private Circle circle;

    public Planetoid(World world, Circle circle) {
        this.circle = circle;

        BodyDef planetoidBodyDef = new BodyDef();
        CircleShape planetoidShape = new CircleShape();

        planetoidBodyDef.position.set(circle.x, circle.y);
        this.body = world.createBody(planetoidBodyDef);

        planetoidShape.setRadius(circle.radius);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = planetoidShape;
        fixtureDef.density = 1f;
        fixtureDef.filter.categoryBits = C.CAT_STATIC;

        this.body.createFixture(fixtureDef).setUserData(this);

        planetoidShape.dispose();
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    /**
     * Uses "area" for mass to reduce the weight difference between different size bodies.
     * @param planetoid Body object representing planetoid
     * @return Area times planetoid density
     */
    public float getMass() {
        return C.PLANETOID_DENSITY * circle.area();
    }

    public Circle getCircle() {
        return circle;
    }
}
