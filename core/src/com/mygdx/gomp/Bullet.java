package com.mygdx.gomp;

import com.badlogic.gdx.physics.box2d.Body;

/**
 * Created by Jay on 4/27/2016.
 */
public abstract class Bullet {
    String type;
    Body body;
    boolean hasCollided;
    int age;

    public Bullet(String type) {
        this.type = type;
        this.hasCollided = false;
        this.age = 0;
    }

    public abstract void destroy();
}
