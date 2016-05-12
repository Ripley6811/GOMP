package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 5/8/2016.
 */
public class Blob extends ActorBase {
    protected boolean moving;
    protected float moveTime;
    protected boolean grounded;
    private float timeToLeap;

    public Blob(World world, Vector2 position, Vector2 velocity) {
        this.initWorldBody(world);
        this.down = new Vector2(velocity);
        this.body.setTransform(position, velocity.angle());
        this.body.setLinearVelocity(velocity);
        this.initHealth(C.BLOB_HEALTH);
        this.moving = false;
        this.moveTime = 0f;
        this.grounded = false;
        this.timeToLeap = MathUtils.random(1f, 5f);
    }

    @Override
    protected void initWorldBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
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

    public void isGrounded(boolean bool) {
        grounded = bool;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public void jump() {
        timeToLeap -= Gdx.graphics.getDeltaTime();
        if (timeToLeap < 0) {
            timeToLeap = MathUtils.random(1f, 5f);
            Vector2 vUp = new Vector2(this.down).rotate(180).setLength(C.FIGHTER_JUMP_SPEED);
            this.body.applyForceToCenter(vUp, true);
        }
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
}