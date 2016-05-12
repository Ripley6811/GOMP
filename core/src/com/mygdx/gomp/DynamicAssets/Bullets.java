package com.mygdx.gomp.DynamicAssets;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.StaticAssets.Planetoid;
import com.mygdx.gomp.StaticAssets.Planetoids;

import box2dLight.PointLight;
import box2dLight.RayHandler;

/**
 * Created by Jay on 4/27/2016.
 */
public class Bullets {
    private static final String TAG = Bullets.class.getName();

    private World world;
    private RayHandler rayHandler;

    private Array<LaserFire> lasers;
    private Array<GrenadeFire> grenades;
    private Array<Vector3> explosions;  // x, y, timer

    private Animation explosionAnimation;
    private final float TEXTURE_SCALE = 0.08f;

    public Bullets(World world, RayHandler rayHandler, Animation explosionAnimation) {
        this.world = world;
        this.rayHandler = rayHandler;
        this.explosionAnimation = explosionAnimation;
        lasers = new Array<LaserFire>();
        grenades = new Array<GrenadeFire>();
        explosions = new Array<Vector3>();
    }

    public void addLaser(Vector2 position, Vector2 heading) {
        lasers.add(new LaserFire(position, heading));
    }

    public void addGrenade(Vector2 position, Vector2 playerVel, Vector2 heading) {
        grenades.add(new GrenadeFire(position, playerVel.scl(0.5f), heading));
    }

    public class LaserFire extends Bullet {
        public LaserFire(Vector2 position, Vector2 heading) {
            super("LASER");

            this.damage = C.LASER_DAMAGE;
            this.canBounce = false;

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
            fixtureDef.filter.categoryBits = C.CAT_BULLET;

            // Create fixture and attach reference to this Object
            this.body = world.createBody(bodyDef);
            this.body.createFixture(fixtureDef).setUserData(this);

            circle.dispose();

            body.setLinearVelocity(heading.rotate(MathUtils.random(-C.LASER_RANDOM, C.LASER_RANDOM)).setLength2(C.LASER_SPEED));

            // Add light
            light = new PointLight(rayHandler, 5, new Color(1f, 1f, 0.5f, 0.5f), 4, position.x, position.y);
        }
    }

    public class MissileFire extends Bullet {
        public MissileFire(Vector2 position, Vector2 heading) {
            super("MISSILE");

            this.damage = C.MISSILE_DAMAGE;
            this.canBounce = false;
        }
    }

    public class GrenadeFire extends Bullet {
        public GrenadeFire(Vector2 position, Vector2 playerVelocity, Vector2 heading) {
            super("GRENADE");

            this.damage = C.GRENADE_DAMAGE;
            this.canBounce = true;

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
            fixtureDef.filter.categoryBits = C.CAT_BULLET;

            // Create fixture and attach reference to this Object
            this.body = world.createBody(bodyDef);
            this.body.createFixture(fixtureDef).setUserData(this);

            circle.dispose();

            body.setLinearVelocity(heading.rotate(MathUtils.random(-C.GRENADE_RANDOM, C.GRENADE_RANDOM)).setLength2(C.GRENADE_SPEED).add(playerVelocity));

            // Add light/sensor
            light = new PointLight(rayHandler, 8, Color.BLACK, 25, position.x, position.y);
        }
    }

    public void applyGravity(Planetoids planetoids) {
        for (GrenadeFire bullet: grenades) {
            Vector2 pos = bullet.body.getPosition();
            bullet.body.applyForceToCenter(planetoids.getGravityVector(pos), true);

        }
    }

    public int resolveContact(Contact contact) {
        Bullet bullet = (Bullet) contact.getFixtureB().getUserData();

        if (bullet.hasCollided()) return 0;

        if (bullet.canBounce && contact.getFixtureA().getUserData() instanceof Planetoid) {
            // Do nothing
        } else {
            bullet.hasCollided(true);
            return bullet.damage;
        }
        return 0;
    }

    private void queueExplosion(Vector2 vector) {
        explosions.add(new Vector3(vector, 0));
    }

    private void update(float delta) {
        // Remove dead lasers, grenades and explosions
        for (int i=lasers.size-1; i >= 0; i--) {
            LaserFire bullet = lasers.get(i);
            bullet.updateLightPos();
            // This line ensures lasers are all at a constant speed.
            bullet.body.setLinearVelocity(bullet.body.getLinearVelocity().setLength2(C.LASER_SPEED));
            if (bullet.age++ > C.BULLET_AGE_LIMIT) bullet.collided = true;
            if (bullet.collided) lasers.removeIndex(i).destroy(world);
        }
        for (int i=grenades.size-1; i >= 0; i--) {
            GrenadeFire bullet = grenades.get(i);
            bullet.updateLightPos();
            if (bullet.age++ > C.BULLET_AGE_LIMIT) bullet.collided = true;
            if (bullet.collided) {
                queueExplosion(bullet.body.getPosition());
                grenades.removeIndex(i).destroy(world);
            }
        }
        for (int i=explosions.size-1; i >= 0; i--) {
            explosions.get(i).z += delta;
            if (explosionAnimation.isAnimationFinished(explosions.get(i).z)) {
                explosions.removeIndex(i);
            }
        }
    }

    public void render(ShapeRenderer renderer, SpriteBatch batch) {
        this.update(Gdx.graphics.getDeltaTime());
        /** Lasers **/
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(1, 1, MathUtils.random(.5f, 1f), 1);
        for (LaserFire laser: lasers) {
            Vector2 pos = laser.body.getPosition();
            renderer.line(pos, laser.body.getLinearVelocity().nor().scl(C.LASER_LENGTH).add(pos));
        }
        renderer.end();
        /** Grenades **/
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(MathUtils.random(.5f, 1f), 1, 1, 1);
        for (GrenadeFire bullet: grenades) {
            Vector2 pos = bullet.body.getPosition();
            renderer.circle(pos.x, pos.y, C.GRENADE_RADIUS, 8);
        }
        renderer.end();
        /** Explosions from grenades **/
        batch.begin();
        for (Vector3 explosion: explosions) {
            TextureRegion tr = explosionAnimation.getKeyFrame(explosion.z);
            batch.draw(tr, explosion.x - 24, explosion.y - 24,
                    24, 24, 48, 48, TEXTURE_SCALE, TEXTURE_SCALE, 0);
        }
        batch.end();
    }
}
