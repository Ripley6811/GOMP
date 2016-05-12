package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Jay on 5/12/2016.
 */
public abstract class ActorBase {
    protected Body body;  // Maintains world getPosition
    protected Vector2 down;  // Gravity pull vector
    private float health;
    private float maxHealth;

    protected abstract void initWorldBody(World world);

    protected void initHealth(float amount) {
        health = amount;
        maxHealth = amount;
    }

    public Vector2 getPosition() {
        return body.getPosition();
    }

    public Vector2 getVelocity() {
        return body.getLinearVelocity();
    }

    public float getDownAngle() {
        return down.angle();
    }

    public float getDownMagnitude() {
        return down.len();
    }

    public void damage(float amount) {
        health -= amount;
    }

    protected void heal(float amount) {
        health += amount;
        if (maxHealth > 0f) {
            health = Math.min(health, maxHealth);
        }
    }

    public float getHealth() {
        return health;
    }

    public boolean isDead() {
        return health <= 0;
    }

    protected void destroy(World world) {
        world.destroyBody(this.body);
    }
}
