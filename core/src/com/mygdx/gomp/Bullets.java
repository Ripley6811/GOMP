package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
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

    public Bullets(World world) {
        this.world = world;
        lasers = new Array<LaserFire>();
    }

    public void addLaser(Vector2 position, Vector2 heading) {
        lasers.add(new LaserFire(position, heading));
    }

    public class LaserFire {
        public Body body;  // Maintains world position
        Vector2 heading;

        public LaserFire(Vector2 position, Vector2 heading) {
            this.heading = heading;

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.bullet = true;
            bodyDef.position.set(new Vector2(position));

            this.body = world.createBody(bodyDef);
            CircleShape circle = new CircleShape();
            circle.setRadius(0.1f);

            // Create a fixture definition to apply our shape to
            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = circle;
            fixtureDef.density = 0f;
            fixtureDef.friction = 0f;

            // Create fixture and attach reference to this Object
            this.body.createFixture(fixtureDef).setUserData(this);

            circle.dispose();

            body.setLinearVelocity(heading.setLength2(C.LASER_SPEED));
        }
    }

    public class MissileFire {
        public Body body;  // Maintains world position
        Vector2 heading;

        public MissileFire(Vector2 position, Vector2 heading) {
        }

    }
}
