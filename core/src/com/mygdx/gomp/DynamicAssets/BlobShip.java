package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 5/8/2016.
 */
public class BlobShip {
    public Body body;  // Maintains world position
    private boolean hasLanded;
    public Vector2 down;
    private int health;
    private final float TEXTURE_SCALE = 0.08f;
    private final int TEXTURE_WIDTH;
    private final int TEXTURE_HALF_WIDTH;

    public BlobShip(World world, TextureAtlas atlas) {
        this.initBody(world);

        // TODO: TEMP
        TEXTURE_WIDTH = 20;
        TEXTURE_HALF_WIDTH = TEXTURE_WIDTH / 2;
    }

    private void initBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        CircleShape circle = new CircleShape();
        circle.setRadius(C.BLOB_HEIGHT / 2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.8f;
        fixtureDef.friction = 0.1f;

        body.createFixture(fixtureDef).setUserData(this);
        circle.dispose();
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
