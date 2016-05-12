package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.StaticAssets.Planetoids;

/**
 * Created by Jay on 5/8/2016.
 */
public class BlobShip extends ActorBase {
    private boolean hasLanded;
    private Vector2 fixedPosition;
    private final float TEXTURE_SCALE = 0.08f;
    private final int TEXTURE_WIDTH;
    private final int TEXTURE_HALF_WIDTH;
    public SpaceBlobs spaceBlobs;
    private float timeSinceSpawn;
    private Animation texture;
    private float animateTimer;

    public BlobShip(World world, TextureAtlas atlas, SpaceBlobs spaceBlobs, Vector2 startPosition) {
        this.initWorldBody(world);
        this.initHealth(C.BLOB_SHIP_HEALTH);
        this.body.setTransform(startPosition, 0);
        this.setMass(C.FIGHTER_MASS);
        this.spaceBlobs = spaceBlobs;
        this.timeSinceSpawn = 0;
        this.down = new Vector2();
        this.hasLanded = false;

        texture = new Animation(0.2f, atlas.createSprites("BlobShip"), Animation.PlayMode.LOOP_PINGPONG);

        TEXTURE_WIDTH = texture.getKeyFrame(0).getRegionWidth();
        TEXTURE_HALF_WIDTH = TEXTURE_WIDTH / 2;
    }

    @Override
    protected void initWorldBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(C.BLOB_SHIP_HEIGHT / 2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 2f;
        fixtureDef.friction = 0f;
        fixtureDef.filter.groupIndex = C.GROUP_BLOB;
        fixtureDef.filter.categoryBits = C.CAT_BLOB;

        body.createFixture(fixtureDef).setUserData(this);
        circle.dispose();
    }

    public void setAsLanded() {
        hasLanded = true;
        fixedPosition = new Vector2(getPosition());
    }

    @Override
    public void applyGravity(Planetoids planetoids) {
        if (hasLanded) return;

        super.applyGravity(planetoids);
    }

    public void update(float delta) {
        animateTimer += delta;
        if (hasLanded) {
            this.body.setTransform(fixedPosition, 0);
            timeSinceSpawn += delta;
            if (timeSinceSpawn > C.BLOB_SHIP_SPAWN_RATE) {
                Vector2 vel = new Vector2(this.down).rotate(180f);
                spaceBlobs.spawn(new Vector2(this.getPosition()).add(vel.nor().setLength(C.BLOB_SHIP_HEIGHT / 2f)),
                        vel.setLength(C.FIGHTER_JUMP_SPEED));
                timeSinceSpawn = 0f;
            }
        }
    }

    public void render(SpriteBatch batch) {
        Vector2 pos = this.getPosition();
        batch.begin();
        this.draw(batch, texture.getKeyFrame(animateTimer),
                pos.x - TEXTURE_HALF_WIDTH,
                pos.y - TEXTURE_HALF_WIDTH,
                this.down.angle() + 90);
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
