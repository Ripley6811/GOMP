package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/26/2016.
 */
public class Fighter {
    private static final String TAG = Fighter.class.getName();
    private boolean flyMode;
    private boolean grounded;
    private boolean jumping;
    public Body body;  // Maintains world position
    public Vector2 down;

    public Fighter(World world, float x, float y) {
        flyMode = false;
        grounded = true;
        jumping = false;
        down = new Vector2();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));

        body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(C.FIGHTER_HEIGHT / 2f);
//        PolygonShape square = new PolygonShape();
//        square.setAsBox(C.FIGHTER_HEIGHT / 2f, C.FIGHTER_HEIGHT / 4f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
//        fixtureDef.shape = square;
        fixtureDef.density = 0.8f;
        fixtureDef.friction = 0.1f;
//        fixtureDef.restitution = 0f; // Make it bounce a little bit

        body.createFixture(fixtureDef);

        circle.dispose();
//        square.dispose();
    }

    public boolean isGrounded() {
        return grounded;
    }

    public boolean isFlying() {
        return flyMode;
    }

    public void applyGravity(Planetoids planetoids) {
        Vector2 pos = this.body.getPosition();
        this.down.set(planetoids.getGravityVector(pos));
        this.body.applyForceToCenter(this.down, true);
    }

    public void applyLandFriction(Planetoids planetoids) {
        grounded = planetoids.isOnAnySurface(this.body.getPosition());
        Gdx.app.log(TAG, "grounded: " + grounded);
        if (grounded) {
            Vector2 v = new Vector2(this.body.getLinearVelocity());
            v.scl(0.8f);
            this.body.setLinearVelocity(v);
        }
    }


    public void queryPlayerInput() {
        Vector2 position = this.body.getPosition();
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            if (!flyMode && grounded) {
                Vector2 vLeft = new Vector2(this.down).rotate90(-1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vLeft, true);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            if (!flyMode && grounded) {
                Vector2 vRight = new Vector2(this.down).rotate90(1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vRight, true);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            if (grounded && !jumping) {
                Vector2 vUp = new Vector2(this.down).rotate(180).setLength(C.FIGHTER_JUMP_SPEED);
//                this.body.applyLinearImpulse(vUp, position, true);
                this.body.applyForceToCenter(vUp, true);
//                jumping = true;
            }
//            if (jumping)
        }
    }
}
