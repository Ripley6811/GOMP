package com.mygdx.gomp;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/27/2016.
 */
public class Bullets {
    private static final String TAG = Bullets.class.getName();

    private World world;

    private Array<LaserFire> lasers;
    private Array<GrenadeFire> grenades;

    public Bullets(World world) {
        this.world = world;
        lasers = new Array<LaserFire>();
        grenades = new Array<GrenadeFire>();
    }

    public void addLaser(Vector2 position, Vector2 heading) {
        lasers.add(new LaserFire(position, heading));
    }

    public void addGrenade(Vector2 position, Vector2 playerVel, Vector2 heading) {
        grenades.add(new GrenadeFire(position, playerVel.scl(0.5f), heading));
    }

    public class LaserFire extends Bullet {
        public Body body;  // Maintains world position

        public LaserFire(Vector2 position, Vector2 heading) {
            super("LASER");

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.bullet = true;
            bodyDef.position.set(new Vector2(position));

            CircleShape circle = new CircleShape();
            circle.setRadius(C.LASER_RADIUS);

            // Create a fixture definition to apply our shape to
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circle;
            fixtureDef.density = 0f;
            fixtureDef.friction = 0f;
            fixtureDef.filter.groupIndex = C.GROUP_PLAYER;

            // Create fixture and attach reference to this Object
            this.body = world.createBody(bodyDef);
            this.body.createFixture(fixtureDef).setUserData(this);

            circle.dispose();

            body.setLinearVelocity(heading.rotate(MathUtils.random(-C.LASER_RANDOM, C.LASER_RANDOM)).setLength2(C.LASER_SPEED));
        }
    }

    public class MissileFire extends Bullet {
        public Body body;  // Maintains world position

        public MissileFire(Vector2 position, Vector2 heading) {
            super("MISSILE");
        }

    }

    public class GrenadeFire extends Bullet {
        public Body body;  // Maintains world position

        public GrenadeFire(Vector2 position, Vector2 playerVelocity, Vector2 heading) {
            super("GRENADE");

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.bullet = true;
            bodyDef.position.set(new Vector2(position));

            CircleShape circle = new CircleShape();
            circle.setRadius(C.GRENADE_RADIUS);

            // Create a fixture definition to apply our shape to
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circle;
            fixtureDef.density = 10f;
            fixtureDef.friction = 0f;
            fixtureDef.restitution = .4f;
            fixtureDef.filter.groupIndex = C.GROUP_PLAYER;

            // Create fixture and attach reference to this Object
            this.body = world.createBody(bodyDef);
            this.body.createFixture(fixtureDef).setUserData(this);

            circle.dispose();

            body.setLinearVelocity(heading.rotate(MathUtils.random(-C.GRENADE_RANDOM, C.GRENADE_RANDOM)).setLength2(C.GRENADE_SPEED).add(playerVelocity));

        }

    }

    public void applyGravity(Planetoids planetoids) {
        for (GrenadeFire bullet: grenades) {
            Vector2 pos = bullet.body.getPosition();
            bullet.body.applyForceToCenter(planetoids.getGravityVector(pos, C.GRENADE_MASS), true);
        }
    }

    public void render(ShapeRenderer renderer) {
        // Remove dead bullets
        // TODO: Render explosion.
        for (int i=lasers.size-1; i >= 0; i--) {
            LaserFire bullet = lasers.get(i);
            if (bullet.age++ > C.BULLET_AGE_LIMIT) bullet.hasCollided = true;
            if (bullet.hasCollided) world.destroyBody(lasers.removeIndex(i).body);
        }
        for (int i=grenades.size-1; i >= 0; i--) {
            GrenadeFire bullet = grenades.get(i);
            if (bullet.age++ > C.BULLET_AGE_LIMIT) bullet.hasCollided = true;
            if (bullet.hasCollided) world.destroyBody(grenades.removeIndex(i).body);
        }

        // TODO: Render non-collided bullets.
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(1, 1, MathUtils.random(.5f, 1f), 1);
        for (LaserFire laser: lasers) {
            Vector2 pos = laser.body.getPosition();
            renderer.line(pos, laser.body.getLinearVelocity().nor().scl(C.LASER_LENGTH).add(pos));
        }
        renderer.setColor(MathUtils.random(.5f,1f),1,1,1);
        for (GrenadeFire bullet: grenades) {
            Vector2 pos = bullet.body.getPosition();
            renderer.circle(pos.x, pos.y, C.GRENADE_RADIUS);
        }
        renderer.end();
    }
}
