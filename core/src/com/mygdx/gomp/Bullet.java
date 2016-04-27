package com.mygdx.gomp;

/**
 * Created by Jay on 4/27/2016.
 */
public class Bullet {
    String type;
    boolean hasCollided;
    int age;

    public Bullet(String type) {
        this.type = type;
        this.hasCollided = false;
        this.age = 0;
    }
}
