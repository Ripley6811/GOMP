package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.StaticAssets.Planetoids;

/**
 * Blobs eat everything and move by jumping.
 *
 * Created by Jay on 5/8/2016.
 */
public class SpaceBlobs {
    private static final String TAG = SpaceBlobs.class.getName();
    public Array<Blob> blobs;  // Maintains world getPosition
    private final float TEXTURE_SCALE = 0.08f;
    private final int TEXTURE_WIDTH;
    private final int TEXTURE_HALF_WIDTH;
    private World world;
    private final Animation blobAnimation;
    private float timer;

    public SpaceBlobs(World world, TextureAtlas atlas) {
        this.world = world;
        this.blobs = new Array<Blob>();
        this.timer = 0f;

        blobAnimation = new Animation(
                C.BLOB_ANIMATION_RATE,
                atlas.createSprites("spaceblob"),
                Animation.PlayMode.LOOP_PINGPONG
        );
        TEXTURE_WIDTH = blobAnimation.getKeyFrame(0).getRegionWidth();
        TEXTURE_HALF_WIDTH = TEXTURE_WIDTH / 2;

        // TODO: Jump higher when jumping off another blob. Use square bodies
    }

    public void spawn(Vector2 position, Vector2 velocity) {
        blobs.add(new Blob(world, position, velocity));
    }

    public void applyGravity(Planetoids planetoids) {
        for (Blob blob: blobs) {
            if (blob.grounded) {
                Vector2 v = new Vector2(blob.getVelocity());
                v.scl(.1f);
                blob.body.setLinearVelocity(v);
            } else {
                blob.applyGravity(planetoids);
            }
        }
    }

    private void update() {
        for (int i=blobs.size-1; i>=0; i--) {
            blobs.get(i).jump();
            if (blobs.get(i).isDead()) {
                blobs.removeIndex(i).destroy(world);
            }
        }
    }

    public void render(SpriteBatch batch) {
        this.timer += Gdx.graphics.getDeltaTime();
        this.update();

        batch.begin();
        for (Blob blob: blobs) {
            Vector2 pos = blob.body.getPosition();
            if (blob.isMoving()) {
                this.draw(batch, blobAnimation.getKeyFrame(blob.moveTime),
                        pos.x - TEXTURE_HALF_WIDTH,
                        pos.y - TEXTURE_HALF_WIDTH,
                        blob.down.angle() + 90);
            } else {
                this.draw(batch, blobAnimation.getKeyFrame(0),
                        pos.x - TEXTURE_HALF_WIDTH,
                        pos.y - TEXTURE_HALF_WIDTH,
                        blob.down.angle() + 90);
            }
        }
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
