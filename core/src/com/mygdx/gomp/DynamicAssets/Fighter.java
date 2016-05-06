package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.InputMapper.IM;
import com.mygdx.gomp.StaticAssets.Planetoid;
import com.mygdx.gomp.StaticAssets.Planetoids;

/**
 * Created by Jay on 4/26/2016.
 */
public class Fighter {
    private static final String TAG = Fighter.class.getName();
    private boolean flyMode;
    private boolean grounded;
    private boolean jumping;
    private boolean faceRight;
    private boolean recharging;
    private float timeWalking;
    private float laserCooldown = 0f;
    private float grenadeCooldown = 0f;
    public Body body;  // Maintains world position
    public Vector2 down;
    public Vector2 cursorPos;
    private int health;
    private int energy;
    private Planetoid base;
    private float energyBoost;

    // Textures
    private final Animation pav_walk;
    private final Animation pav_trans;  // Jump is first frame, jet is last
    private final TextureRegion pav_gun;
    private final TextureRegion pav_fly;
    private final TextureRegion pav_rwing;
    private final TextureRegion pav_lwing;


    public Fighter(World world, TextureAtlas atlas, Planetoid base) {
        this(world, atlas, base, true);
    }

    public Fighter(World world, TextureAtlas atlas, Planetoid base, boolean isPlayer) {
        flyMode = false;
        grounded = true;
        jumping = false;
        faceRight = true;
        recharging = false;
        this.base = base;
        down = new Vector2();
        cursorPos = new Vector2();
        timeWalking = 0f;
        health = C.FIGHTER_MAX_HEALTH;
        energy = C.FIGHTER_MAX_ENERGY;
        energyBoost = 0f;

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(base.getCircle().x,
                base.getCircle().y + base.getCircle().radius));

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

        body.createFixture(fixtureDef).setUserData(this);

        circle.dispose();

        // Set textures
        pav_walk = new Animation(C.PAV_WALK_FRAME_RATE, atlas.findRegions("PAVsm_WALK"),
                Animation.PlayMode.LOOP);
        pav_trans = new Animation(C.PAV_WALK_FRAME_RATE, atlas.findRegions("PAVsm_TRANS"),
                Animation.PlayMode.NORMAL);
        pav_gun = atlas.createSprite("PAVsm_GUN");
        pav_fly = atlas.createSprite("PAVsm_FLY");
        pav_rwing = atlas.createSprite("PAVsm_WING_RIGHT");
        pav_lwing = atlas.createSprite("PAVsm_WING_RIGHT");
        pav_lwing.flip(true, false);
    }

    public int takeDamage(int amount) {
        health = Math.max(health - amount, 0);
        return health;
    }

    public int addHealth(int amount) {
        health = Math.min(health + amount, C.FIGHTER_MAX_HEALTH);
        return health;
    }

    public int useEnergy(int amount) {
        energy = Math.max(energy - amount, 0);
        return energy;
    }

    public int addEnergy(int amount) {
        energy = Math.min(energy + amount, C.FIGHTER_MAX_ENERGY);
        return energy;
    }

    public int getHealth() {
        return health;
    }

    public int getEnergy() {
        return energy;
    }

    public Planetoid getBase() {
        return this.base;
    }

    public boolean isRecharging() {
        return recharging;
    }

    public void setRecharging(boolean bool) {
        if (energy == C.FIGHTER_MAX_ENERGY) {
            recharging = false;
        }
        recharging = bool;
    }

    public boolean laserReady() {
        if (laserCooldown <= 0f && energy >= C.LASER_DAMAGE) {
            laserCooldown = C.LASER_COOLDOWN;
            useEnergy(C.LASER_DAMAGE);
            return true;
        }
        return false;
    }

    public boolean grenadeReady() {
        if (grenadeCooldown <= 0f && energy >= C.GRENADE_DAMAGE) {
            grenadeCooldown = C.GRENADE_COOLDOWN;
            useEnergy(C.GRENADE_DAMAGE);
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


    public void queryPlayerMovementInput(float delta) {
        if (IM.isPressingLeft()) {
            faceRight = false;
            timeWalking += delta;
            if (!flyMode && grounded) {
                Vector2 vLeft = new Vector2(this.down).rotate90(-1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vLeft, true);
            }
            if (flyMode){
                Vector2 vLeft = new Vector2(this.cursorPos).rotate90(1).setLength(20);
                this.body.applyLinearImpulse(vLeft, body.getPosition(), true);
            }
        } else if (IM.isPressingRight()) {
            faceRight = true;
            timeWalking += delta;
            if (!flyMode && grounded) {
                Vector2 vRight = new Vector2(this.down).rotate90(1).setLength(C.FIGHTER_WALK_SPEED);
                this.body.applyForceToCenter(vRight, true);
            }
            if (flyMode){
                Vector2 vRight = new Vector2(this.cursorPos).rotate90(-1).setLength(20);
                this.body.applyLinearImpulse(vRight, body.getPosition(), true);
            }
        } else {
            timeWalking = 0f;
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
            if (this.getEnergy() > 0) {
                this.useEnergy(1);
                Vector2 flyVector = new Vector2(this.cursorPos).setLength(20);
                this.body.applyLinearImpulse(flyVector, body.getPosition(), true);
            }
        }
    }

    private void update(float delta) {
        energyBoost += delta*10;
        if (recharging) energyBoost += delta*10;
        while (energyBoost > 1f) {
            addEnergy(1);
            energyBoost -= 1f;
        }
    }

    public void render(float delta, SpriteBatch batch, Vector2 cursorPos) {
        this.update(delta);

        Vector2 pos = body.getPosition();
        this.cursorPos.set(cursorPos.sub(pos));
        laserCooldown -= delta;
        grenadeCooldown -= delta;
        float tempRotation = cursorPos.angle() - 90;
        int pWidth = pav_gun.getRegionWidth();
        int pHalfWidth = pWidth/2;
        float wingWaddle = 6*MathUtils.sinDeg(1600*timeWalking);

        batch.begin();
        // Draw character at center
        if (!isFlying()) {
            if (faceRight) {
                batch.draw(pav_lwing,
                        pos.x - pHalfWidth,
                        pos.y - pHalfWidth,
                        pHalfWidth, pHalfWidth,
                        pWidth, pWidth,
                        .08f, .08f,  // Scale
                        down.angle() + 40 + wingWaddle);
                batch.draw(pav_lwing,
                        pos.x - pHalfWidth,
                        pos.y - pHalfWidth,
                        pHalfWidth, pHalfWidth,
                        pWidth, pWidth,
                        .08f, .08f,  // Scale
                        down.angle() + 50 + wingWaddle);
            } else {
                batch.draw(pav_rwing,
                        pos.x - pHalfWidth,
                        pos.y - pHalfWidth,
                        pHalfWidth, pHalfWidth,
                        pWidth, pWidth,
                        .08f, .08f,  // Scale
                        down.angle() + 130 + wingWaddle);
                batch.draw(pav_rwing,
                        pos.x - pHalfWidth,
                        pos.y - pHalfWidth,
                        pHalfWidth, pHalfWidth,
                        pWidth, pWidth,
                        .08f, .08f,  // Scale
                        down.angle() + 140 + wingWaddle);
            }
        }
        if (isGrounded()) {
            TextureRegion texture = pav_walk.getKeyFrame(timeWalking);
            if (!faceRight && !texture.isFlipX()) texture.flip(true, false);
            if (faceRight && texture.isFlipX()) texture.flip(true, false);
            batch.draw(texture,
                    pos.x - pHalfWidth,
                    pos.y - pHalfWidth,
                    pHalfWidth, pHalfWidth,
                    pWidth, pWidth,
                    .08f, .08f,  // Scale
                    down.angle() + 90);


        } else if (isFlying()) {
            batch.draw(pav_fly,
                    pos.x - pHalfWidth,
                    pos.y - pHalfWidth,
                    pHalfWidth, pHalfWidth,
                    pWidth, pWidth,
                    .08f, .08f,  // Scale
                    tempRotation);
        } else { // JUMPING
            TextureRegion texture = pav_trans.getKeyFrame(0f);
            if (!faceRight && !texture.isFlipX()) texture.flip(true, false);
            if (faceRight && texture.isFlipX()) texture.flip(true, false);
            batch.draw(texture,
                    pos.x - pHalfWidth,
                    pos.y - pHalfWidth,
                    pHalfWidth, pHalfWidth,
                    pWidth, pWidth,
                    .08f, .08f,  // Scale
                    down.angle() + 90);
        }
        // DRAW GUN IN ALL CASES
        batch.draw(pav_gun,
                pos.x - pHalfWidth,
                pos.y - pHalfWidth,
                pHalfWidth, pHalfWidth,
                pWidth, pWidth,
                .08f, .08f,  // Scale
                tempRotation);
        batch.end();
    }
}