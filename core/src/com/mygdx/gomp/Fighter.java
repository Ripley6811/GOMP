package com.mygdx.gomp;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.InputMapper.IM;

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
    public Vector2 cursorPos;

    public Fighter(World world, float x, float y) {
        this(world, x, y, true);
    }
    public Fighter(World world, float x, float y, boolean isPlayer) {
        flyMode = false;
        grounded = true;
        jumping = false;
        faceRight = true;
        down = new Vector2();
        cursorPos = new Vector2();

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(x, y));

        body = world.createBody(bodyDef);
        CircleShape circle = new CircleShape();
        circle.setRadius(C.FIGHTER_HEIGHT / 2f);

        // Create a fixture definition to apply our shape to
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.8f;
        fixtureDef.friction = 0.1f;
        if (isPlayer) {
            fixtureDef.filter.groupIndex = C.GROUP_PLAYER;
            fixtureDef.filter.categoryBits = C.CAT_PLAYER;
        } else {
            fixtureDef.filter.groupIndex = C.GROUP_BANDIT;
            fixtureDef.filter.categoryBits = C.CAT_BANDIT;
        }

        body.createFixture(fixtureDef);

        circle.dispose();
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
        // Set as grounded if very near a planetoid surface
        grounded = planetoids.isOnAnySurface(this.body.getPosition());

        if (grounded) {
            flyMode = false;
            Vector2 v = new Vector2(this.body.getLinearVelocity());
            v.scl(C.SURFACE_SLOW_DOWN);
            this.body.setLinearVelocity(v);
        }
    }


    public void queryPlayerMovementInput() {
        if (IM.isPressingLeft()) {
            faceRight = false;
            if (!flyMode && grounded) {
                Vector2 vLeft = new Vector2(this.down).rotate90(-1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vLeft, true);
            }
            if (flyMode){
                Vector2 vLeft = new Vector2(this.cursorPos).rotate90(1).setLength(20);
                this.body.applyLinearImpulse(vLeft, body.getPosition(), true);
            }
        }
        if (IM.isPressingRight()) {
            faceRight = true;
            if (!flyMode && grounded) {
                Vector2 vRight = new Vector2(this.down).rotate90(1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vRight, true);
            }
            if (flyMode){
                Vector2 vRight = new Vector2(this.cursorPos).rotate90(-1).setLength(20);
                this.body.applyLinearImpulse(vRight, body.getPosition(), true);
            }
        }
        if (!flyMode && IM.justPressedUp()) {
            if (grounded) {
                Vector2 vUp = new Vector2(this.down).rotate(180).setLength(C.FIGHTER_JUMP_SPEED);
                this.body.applyForceToCenter(vUp, true);
            } else {
                flyMode = true;
            }
        }
        if (flyMode && IM.isTransforming() && !grounded) {
            flyMode = false;
        }
        if (flyMode && IM.isPressingUp()) {
            Vector2 flyVector = new Vector2(this.cursorPos).setLength(20);
            this.body.applyLinearImpulse(flyVector, body.getPosition(), true);
        }
    }

    public void render(float delta, ShapeRenderer renderer, Vector2 cursorPos) {
        Vector2 pos = body.getPosition();
        this.cursorPos.set(cursorPos.sub(pos));
        laserCooldown -= delta;
        grenadeCooldown -= delta;

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.translate(pos.x, pos.y, 0f);
        // Draw character at center
        if (isFlying()) {
            float tempRotation = cursorPos.angle() - 90;
            renderer.rotate(0, 0, 1, tempRotation);

            renderer.setColor(.7f, .5f, .5f, 1f);
            renderer.arc(0f, -.8f, C.FIGHTER_HEIGHT / 1.6f, 15f, 150f, 10);

            renderer.setColor(.8f, .8f, .9f, 1f);
            renderer.ellipse(-C.FIGHTER_HEIGHT / 4, -C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT);

            renderer.setColor(.7f, .5f, .5f, 1f);
            renderer.arc(0f, -C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT / 2.5f, 0f, 180f, 10);

            renderer.rotate(0, 0, -1, tempRotation);
        } else {
            renderer.rotate(0, 0, 1, down.angle() + 90);

            renderer.setColor(.8f, .8f, .9f, 1f);
            renderer.ellipse(-C.FIGHTER_HEIGHT / 4, -C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT);

            renderer.setColor(.7f, .5f, .5f, 1f);
            renderer.arc(0f, -C.FIGHTER_HEIGHT / 2, C.FIGHTER_HEIGHT / 2.5f, 0f, 180f, 10);

            renderer.rotate(0, 0, -1, down.angle() + 90);
        }
        renderer.translate(-pos.x, -pos.y, 0f);
        renderer.end();
    }
}
