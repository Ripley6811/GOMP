package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/26/2016.
 */
public class Fighter {
    private static final String TAG = Fighter.class.getName();
    private boolean flyMode;
    private boolean grounded;
    private boolean jumping;
    private boolean faceRight;
    private float laserCooldown = 0f;
    private float grenadeCooldown = 0f;
    public Body body;  // Maintains world position
    public Vector2 down;

    public Fighter(World world, float x, float y) {
        this(world, x, y, true);
    }
    public Fighter(World world, float x, float y, boolean isPlayer) {
        flyMode = false;
        grounded = true;
        jumping = false;
        faceRight = true;
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
        if (isPlayer) {
            fixtureDef.filter.groupIndex = C.GROUP_PLAYER;
        } else {
            fixtureDef.filter.groupIndex = C.GROUP_BANDIT;
        }
//        fixtureDef.restitution = 0f; // Make it bounce a little bit

        body.createFixture(fixtureDef);

        circle.dispose();
//        square.dispose();
    }

    public boolean laserReady() {
        if (laserCooldown <= 0f) {
            laserCooldown = C.LASER_COOLDOWN;
            return true;
        }
        return false;
    }

    public boolean grenadeReady() {
        if (grenadeCooldown <= 0f) {
            grenadeCooldown = C.GRENADE_COOLDOWN;
            return true;
        }
        return false;
    }

    public boolean isGrounded() {
        return grounded;
    }

    public boolean isFlying() {
        return flyMode;
    }

    public void applyGravity(Planetoids planetoids) {
        Vector2 pos = this.body.getPosition();
        this.down.set(planetoids.getGravityVector(pos, C.FIGHTER_MASS));
        this.body.applyForceToCenter(this.down, true);
    }

    public void applyLandFriction(Planetoids planetoids) {
        grounded = planetoids.isOnAnySurface(this.body.getPosition());

        if (grounded) {
            Vector2 v = new Vector2(this.body.getLinearVelocity());
            v.scl(0.8f);
            this.body.setLinearVelocity(v);
        }
    }


    public void queryPlayerMovementInput() {
        if (Gdx.input.isKeyPressed(C.MOVE_LEFT)) {
            faceRight = false;
            if (!flyMode && grounded) {
                Vector2 vLeft = new Vector2(this.down).rotate90(-1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vLeft, true);
            }
        }
        if (Gdx.input.isKeyPressed(C.MOVE_RIGHT)) {
            faceRight = true;
            if (!flyMode && grounded) {
                Vector2 vRight = new Vector2(this.down).rotate90(1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vRight, true);
            }
        }
        if (Gdx.input.isKeyPressed(C.MOVE_JUMP)) {
            if (grounded && !jumping) {
                Vector2 vUp = new Vector2(this.down).rotate(180).setLength(C.FIGHTER_JUMP_SPEED);
//                this.body.applyLinearImpulse(vUp, position, true);
                this.body.applyForceToCenter(vUp, true);
//                jumping = true;
            }
//            if (jumping)
        }
    }

    public void render(float delta, ShapeRenderer renderer) {
        laserCooldown -= delta;
        grenadeCooldown -= delta;

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(.8f, .8f, .9f, 1f);
        Vector2 pos = body.getPosition();
        renderer.translate(pos.x, pos.y, 0f);
        renderer.rotate(0, 0, 1, down.angle() + 90);
        // Draw character at center
        renderer.ellipse(- C.FIGHTER_HEIGHT / 4, - C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT);

        renderer.setColor(.7f, .5f, .5f, 1f);
        renderer.arc(0f, - C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT / 2.5f, 0f, 180f, 10);


        renderer.rotate(0, 0, -1, down.angle()+90);
        renderer.translate(-pos.x, -pos.y, 0f);
        renderer.end();
    }
}
