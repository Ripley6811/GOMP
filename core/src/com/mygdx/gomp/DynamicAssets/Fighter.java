package com.mygdx.gomp.DynamicAssets;

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
public class Fighter extends ActorBase {
    private static final String TAG = Fighter.class.getName();
    private boolean flyMode;
    private boolean grounded;
    private boolean jumping;
    private boolean faceRight;
    private boolean recharging;
    private boolean isPlayer;
    private float timeWalking;
    private float laserCooldown = 0f;
    private float grenadeCooldown = 0f;
    public Vector2 cursorPos;
    private float energy;
    private Planetoid base;
    public int blobContacts;

    // Textures
    private final Animation pav_walk;
    private final Animation pav_trans;  // Jump is first frame, jet is last
    private final TextureRegion pav_gun;
    private final TextureRegion pav_fly;
    private final TextureRegion pav_rwing;
    private final TextureRegion pav_lwing;
    private final float TEXTURE_SCALE = 0.08f;
    private final int TEXTURE_WIDTH;
    private final int TEXTURE_HALF_WIDTH;


    public Fighter(World world, TextureAtlas atlas, Planetoid base) {
        this(world, atlas, base, true);
    }

    public Fighter(World world, TextureAtlas atlas, Planetoid base, boolean isPlayer) {
        flyMode = false;
        grounded = true;
        jumping = false;
        faceRight = true;
        recharging = false;
        this.isPlayer = isPlayer;
        this.base = base;
        down = new Vector2();
        cursorPos = new Vector2();
        timeWalking = 0f;
        this.initHealth(C.FIGHTER_MAX_HEALTH);
        energy = C.FIGHTER_MAX_ENERGY;
        blobContacts = 0;

        this.initWorldBody(world);

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
        TEXTURE_WIDTH = pav_gun.getRegionWidth();
        TEXTURE_HALF_WIDTH = TEXTURE_WIDTH /2;
    }

    @Override
    protected void initWorldBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(new Vector2(base.getCircle().x,
                base.getCircle().y + base.getCircle().radius));
        body = world.createBody(bodyDef);

        // Create a fixture definition to apply our shape to
        CircleShape circle = new CircleShape();
        circle.setRadius(C.FIGHTER_HEIGHT / 2f);
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
    }

//    public float takeDamage(float amount) {
//        health = Math.max(health - amount, 0);
//        return health;
//    }

//    public float addHealth(float amount) {
//        health = Math.min(health + amount, C.FIGHTER_MAX_HEALTH);
//        return health;
//    }

    public float useEnergy(float amount) {
        energy = Math.max(energy - amount, 0);
        return energy;
    }

    public float addEnergy(float amount) {
        energy = Math.min(energy + amount, C.FIGHTER_MAX_ENERGY);
        return energy;
    }

//    public float getHealth() {
//        return health;
//    }

    public float getEnergy() {
        return energy;
    }

    public Planetoid getBase() {
        return this.base;
    }

    public boolean isRecharging() {
        return recharging;
    }

    public void setRecharging(boolean bool) {
        recharging = bool;
    }

    public boolean fireLaser() {
        if (laserCooldown <= 0f && energy >= C.LASER_DAMAGE) {
            laserCooldown = C.LASER_COOLDOWN;
            useEnergy(C.LASER_DAMAGE);
            return true;
        }
        return false;
    }

    public boolean fireGrenade() {
        if (grenadeCooldown <= 0f && energy >= C.GRENADE_DAMAGE) {
            grenadeCooldown = C.GRENADE_COOLDOWN;
            useEnergy(C.GRENADE_DAMAGE);
            return true;
        }
        return false;
    }

    public void isGrounded(boolean bool) {
        grounded = bool;
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
                Vector2 flyVector = new Vector2(this.cursorPos).setLength(C.FIGHTER_FLY_SPEED);
                this.body.applyLinearImpulse(flyVector, body.getPosition(), true);
            }
        }
    }

    private void update(float delta) {
        float energy = delta*C.FIGHTER_ENERGY_RECHARGE_RATE;
        if (recharging) {
            energy *= C.FIGHTER_ENERGY_BASE_RECHARGE_MULTIPLIER;
            this.heal(delta*C.FIGHTER_HEALTH_BASE_RECHARGE_RATE);
        }
        addEnergy(energy);

        this.damage(blobContacts * C.BLOB_HIT_DAMAGE);
    }

    public void render(float delta, SpriteBatch batch, Vector2 cursorPos) {
        this.update(delta);

        Vector2 pos = body.getPosition();
        this.cursorPos.set(cursorPos.sub(pos));
        laserCooldown -= delta;
        grenadeCooldown -= delta;
        float tempRotation = cursorPos.angle() - 90;
        float wingWaddle = 3*MathUtils.sinDeg(1600*timeWalking);
        float posX = pos.x - TEXTURE_HALF_WIDTH;
        float posY = pos.y - TEXTURE_HALF_WIDTH;

        batch.begin();
        // Draw wings/antennae
        if (!isFlying()) {
            if (faceRight) {
                this.draw(batch, pav_lwing,
                        posX, posY,
                        down.angle() + 40 + wingWaddle);
                this.draw(batch, pav_lwing,
                        posX, posY,
                        down.angle() + 50 + wingWaddle);
            } else {
                this.draw(batch, pav_rwing,
                        posX, posY,
                        down.angle() + 130 + wingWaddle);
                this.draw(batch, pav_rwing,
                        posX, posY,
                        down.angle() + 140 + wingWaddle);
            }
        }
        // Draw body
        if (isGrounded()) {
            TextureRegion texture = pav_walk.getKeyFrame(timeWalking);
            if (!faceRight && !texture.isFlipX()) texture.flip(true, false);
            if (faceRight && texture.isFlipX()) texture.flip(true, false);
            this.draw(batch, texture,
                    posX, posY,
                    down.angle() + 90);
        } else if (isFlying()) {
            this.draw(batch, pav_fly,
                    posX, posY,
                    tempRotation);
        } else { // JUMPING
            TextureRegion texture = pav_trans.getKeyFrame(0f);
            if (!faceRight && !texture.isFlipX()) texture.flip(true, false);
            if (faceRight && texture.isFlipX()) texture.flip(true, false);
            this.draw(batch, texture,
                    posX, posY,
                    down.angle() + 90);
        }
        // Draw gun (in all cases)
        this.draw(batch, pav_gun,
                posX, posY,
                tempRotation);
        batch.end();
    }

    private void draw(SpriteBatch batch, TextureRegion texture, float x, float y, float rotation) {
        batch.draw(texture,
                x, y,  // Placement
                TEXTURE_HALF_WIDTH, TEXTURE_HALF_WIDTH,  // Center
                TEXTURE_WIDTH, TEXTURE_WIDTH,  // Size
                TEXTURE_SCALE, TEXTURE_SCALE,  // Scale
                rotation);
    }
}