package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 5/8/2016.
 */
public class Blob {
    protected Body body;  // Maintains world position
    protected Vector2 down;
    private int health;
    protected boolean moving;
    protected float moveTime;

    public Blob(World world, Vector2 position, Vector2 velocity) {
        this.initWorldBody(world, position);
        this.down = new Vector2(velocity);
        body.setLinearVelocity(velocity);
        this.health = C.BLOB_HEALTH;
        this.moving = false;
        this.moveTime = 0f;
    }

    private void initWorldBody(World world, Vector2 position) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position);
        body = world.createBody(bodyDef);

        PolygonShape square = new PolygonShape();
        square.setAsBox(C.BLOB_HEIGHT, C.BLOB_HEIGHT);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = square;
        fixtureDef.density = C.BLOB_DENSITY;
        fixtureDef.friction = C.BLOB_FRICTION;

        body.createFixture(fixtureDef).setUserData(this);
        square.dispose();
    }

    public void takeDamage(int amount) {
        health -= amount;
    }

    public boolean isMoving() {
        moving = body.getLinearVelocity().len2() > C.BLOB_IS_MOVING_THRESHOLD;
        if (moving) {
            moveTime += Gdx.graphics.getDeltaTime();
        } else {
            moveTime = 0f;
        }
        return moving;
    }

    public boolean isDead() {
        return health <= 0;
    }

    public void destroy(World world) {
        world.destroyBody(this.body);
    }
}
