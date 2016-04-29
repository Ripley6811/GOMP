package com.mygdx.gomp;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

import box2dLight.PointLight;

/**
 * Created by Jay on 4/27/2016.
 */
public abstract class Bullet {
    protected String type;
    protected Body body;
    protected PointLight light;
    protected boolean hasCollided;
    protected int age;

    public Bullet(String type) {
        this.type = type;
        this.hasCollided = false;
        this.age = 0;
    }

    public void updateLightPos() {
        light.setPosition(body.getPosition());
    }

    public void destroy(World world) {
        world.destroyBody(this.body);
        light.remove();
    }
}
