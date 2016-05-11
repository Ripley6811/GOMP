package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.PointLight;

/**
 * Created by Jay on 4/27/2016.
 */
public class Bullet {
    protected String type;
    protected Body body;
    protected PointLight light;
    protected boolean collided;
    protected int age;
    protected int damage;
    protected boolean canBounce;  // On planetoids
    protected boolean ignoreGravity;

    public Bullet(String type) {
        this.type = type;
        this.collided = false;
        this.age = 0;
    }

    public String getType() {
        return type;
    }

    public boolean hasCollided() {
        return collided;
    }

    public void hasCollided(boolean newVal) {
        collided = newVal;
    }

    public void updateLightPos() {
        light.setPosition(body.getPosition());
    }

    public void destroy(World world) {
        world.destroyBody(this.body);
        light.remove();
    }
}
