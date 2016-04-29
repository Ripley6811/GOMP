package com.mygdx.gomp;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.JsonValue;
import com.mygdx.gomp.Constants.C;

/**
 * Created by Jay on 4/25/2016.
 */
public class Planetoids {
    private static final String TAG = Planetoids.class.getName();

    Array<Body> planetoidBodies;
    private Texture planetImage = new Texture(Gdx.files.internal("planet2.png"));

    /**
     *
     * @param world Box2D world object
     * @param level For loading a level map from file.
     */
    public Planetoids(World world, int level) {
        JsonValue planetoids = C.LEVEL_MAPS.get(level);
        planetoidBodies = new Array<Body>(planetoids.size);

        for (JsonValue planetoid: planetoids) {
            BodyDef planetoidBodyDef = new BodyDef();
            Body planetoidBody;
            CircleShape planetoidShape = new CircleShape();
            float radius = planetoid.getFloat("radius");

            planetoidBodyDef.position.set(
                    planetoid.getFloat("x"),
                    planetoid.getFloat("y")
            );
            planetoidBody = world.createBody(planetoidBodyDef);
            planetoidShape.setRadius(radius);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = planetoidShape;
            fixtureDef.density = 1f;
            fixtureDef.filter.categoryBits = C.CAT_STATIC;

            planetoidBody.createFixture(fixtureDef);

            planetoidShape.dispose();
//            planetoidBody.setUserData(new PlanetoidData(radius));
            planetoidBody.setUserData(
                    new Circle(
                            planetoidBody.getPosition(),
                            radius
                    )
            );

            planetoidBodies.add(planetoidBody);
        }
    }

    /**
     *
     * @param position Fighter position.
     * @return Gravitational force vector.
     */
    public Vector2 getGravityVector(Vector2 position, float mass) {
        Vector2 totalForce = new Vector2(0, 0);

        for (Body planetoid: planetoidBodies) {
            Vector2 toPlanet = new Vector2(planetoid.getPosition()).sub(position);
            float dist2 = toPlanet.len2() * C.INTERPLANETOID_DISTANCE_MULTIPLIER;
//            Gdx.app.log(TAG, "dist2: " + dist2);
//            Gdx.app.log(TAG, "mass: " + ((UserData) planetoid.getUserData()).mass);
            float force = C.GRAVITY * C.PLANETOID_DENSITY * ((Circle) planetoid.getUserData()).area() * mass / dist2;

//            Gdx.app.log(TAG, "force: " + force);
            toPlanet.setLength(force);

            if (isOnSurface(planetoid, position)) {
                return toPlanet;
            } else {
                totalForce.add(toPlanet);
            }
        }

        return totalForce;
    }

    public boolean isOnAnySurface(Vector2 position) {
        for (Body planetoid: planetoidBodies) {
            if (isOnSurface(planetoid, position)) {
                return true;
            }
        }
        return false;
    }

    public boolean isOnSurface(Body planetoid, Vector2 position) {
        float threshold = ((Circle) planetoid.getUserData()).radius;
        threshold += C.FIGHTER_HEIGHT;

        if (position.dst(planetoid.getPosition()) < threshold * C.GROUNDED_THRESHOLD_MULTIPLIER) {
            return true;
        }
        return false;
    }

    /**
     * Data object for planetoid Body instance using the
     * setUserData method.
     */
    public class PlanetoidData {
        public String type;
        public float radius;
        public float mass;

        public PlanetoidData(float radius) {
            this.type = "PLANETOID";
            this.radius = radius;
            this.mass = calculateMass(radius);
        }
    }

    private float calculateMass(float radius) {
        // Volume based
//        float volume = C.PI_4_3RDS * radius * radius * radius;
        // Area based
        float volume = C.PI * radius * radius;
        return C.PLANETOID_DENSITY * volume;
    }

    public Circle getCircle(int bodyIndex) {
        if (bodyIndex >= planetoidBodies.size) return null;

//        Body body = planetoidBodies.get(bodyIndex);
        return (Circle) planetoidBodies.get(bodyIndex).getUserData();
    }

    public void render(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(.5f, .3f, .4f, 1f);
        for (Body rock: planetoidBodies) {
            Vector2 pos = rock.getPosition();
            renderer.circle(pos.x, pos.y, ((PlanetoidData) rock.getUserData()).radius, 40);
        }
        renderer.end();
    }

    public void render(SpriteBatch batch) {
//        renderer.begin(ShapeRenderer.ShapeType.Filled);
//        renderer.setColor(.5f, .3f, .4f, 1f);
//        for (Body rock: planetoidBodies) {
//            Vector2 pos = rock.getPosition();
//            renderer.circle(pos.x, pos.y, ((PlanetoidData) rock.getUserData()).radius, 40);
//        }
//        renderer.end();


        batch.begin();
        for (Body rock: planetoidBodies) {
            Circle circle = (Circle)rock.getUserData();
            batch.draw(planetImage,
                    circle.x - circle.radius,
                    circle.y - circle.radius,
                    2*circle.radius, 2*circle.radius);
        }
        batch.end();
    }
}
