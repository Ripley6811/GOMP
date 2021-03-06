package com.mygdx.gomp.Constants;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Jay on 4/25/2016.
 */
public class C {
    /** LOGGING LEVELS : CHOOSE ONE */
    public static final int LOG_LEVEL = Application.LOG_NONE;
//    public static final int LOG_LEVEL = Application.LOG_ERROR;
//    public static final int LOG_LEVEL = Application.LOG_INFO;
//    public static final int LOG_LEVEL = Application.LOG_DEBUG;

    /** WORLD CONSTANTS */
    public static final String MAIN_ATLAS = "images/gomp.pack.atlas";
    // Rotation speed cap when on or close to surface.
    public static final float ROTATE_SPEED_CAP = 6f;
    // Rotation speed slowed when free-falling between surfaces.
    public static final float ROTATE_FREEFALL_CAP = ROTATE_SPEED_CAP/20f;
    public static final float PI = MathUtils.PI;
    public static final short CAT_LIGHT = 0x001;
    public static final short CAT_PLAYER = 0x002;
    public static final short CAT_BANDIT = 0x004;
    public static final short CAT_BULLET = 0x008;
    public static final short CAT_STATIC = 0x010;
    public static final short CAT_BLOB = 0x020;
    public static final int GROUP_PLAYER = -1;
    public static final int GROUP_BANDIT = -2;
    public static final int GROUP_BLOB = -3;
    public static final int NUMBER_OF_STARS = 10000;
    public static final float STAR_RADIUS = 0.2f;
    public static final Color STAR_COLOR = new Color(.4f,.4f,.4f,1f);
    public static final float STAR_PARALLAX_SCALE = 0.7f;  // 1f = fixed to player
    public static final Vector2 STARFIELD_CENTER = new Vector2(50, 0);
    public static final Vector2 STARFIELD_STD = new Vector2(80, 32);
    public static final float SURFACE_SLOW_DOWN = 0.84f;
    public static final float PAV_WALK_FRAME_RATE = 0.05f;
    public static final float DELTA_THRESHOLD = 0.05f;
    public static final float STANDING_GRAVITY = 2000f;  // Force at surface of all planetoids

    /** PLANETOIDS */
    public static final float GRAVITY = 9.8f;
    public static final float UNIVERSAL_GRAVITATIONAL_CONSTANT = 6.673e-11f;
    public static final float PLANETOID_DENSITY = 0.15f;
    public static final float INTERPLANETOID_DISTANCE_MULTIPLIER = 2.4f;
    public static final float GROUNDED_THRESHOLD_MULTIPLIER = 1f;
    public static final JsonValue LEVEL_MAPS =
            new JsonReader().parse(Gdx.files.internal("json/levels.json")).get("levels");

    /** FIGHTERS */
    public static final float FIGHTER_HEIGHT = 4f;  // Meters (Box2D)
    public static final float FIGHTER_MASS = 1200f;  // kg
    public static final float FIGHTER_WALK_SPEED = 3000f;  // kg
    public static final float FIGHTER_JUMP_SPEED = 80000f;  // kg
    public static final float FIGHTER_FLY_SPEED = 40f;  // kg
    public static final float LASER_SPEED = 50000f;
    public static final float LASER_RADIUS = 0.1f;
    public static final float LASER_COOLDOWN = 0.1f;
    public static final float LASER_RANDOM = 1.5f;  // Degrees error
    public static final float LASER_LENGTH = 3f;
    public static final float LASER_START_OFFSET = 2f;
    // Destroy bullets that fly off into the void.
    public static final float BULLET_AGE_LIMIT = 300;  // Frames
    public static final float GRENADE_SPEED = 2000f;
    public static final float GRENADE_MASS = 100f;
    public static final float GRENADE_RADIUS = 0.3f;
    public static final float GRENADE_COOLDOWN = 0.4f;
    public static final float GRENADE_RANDOM = 1.5f;  // Degrees error
    public static final int LASER_DAMAGE = 5;
    public static final int GRENADE_DAMAGE = 25;
    public static final int MISSILE_DAMAGE = 50;
    public static final int FIGHTER_MAX_HEALTH = 100;
    public static final int FIGHTER_MAX_ENERGY = 200;
    public static final float FIGHTER_ENERGY_RECHARGE_RATE = 20f;  // * Delta
    public static final float FIGHTER_ENERGY_BASE_RECHARGE_MULTIPLIER = 2f;  // * Delta
    public static final float FIGHTER_HEALTH_BASE_RECHARGE_RATE = 8f;  // * Delta
    public static final float EXPLOSION_FRAME_RATE = 0.05f;

    /** ALIENS **/
    public static final float BLOB_HEIGHT = .8f;  // Meters (Box2D)
    public static final float BLOB_SHIP_HEIGHT = 5f;  // Meters (Box2D)
    public static final float BLOB_MASS = 10f;  // kg
    public static final float BLOB_DENSITY = .4f;
    public static final float BLOB_FRICTION = 0.3f;
    public static final float BLOB_ANIMATION_RATE = 0.05f;
    public static final int BLOB_HEALTH = 5;
    public static final int BLOB_SHIP_HEALTH = 100;
    public static final float BLOB_HIT_DAMAGE = 0.4f;
    public static final float BLOB_IS_MOVING_THRESHOLD = 1f;
    public static final float BLOB_SHIP_SPAWN_RATE = 1f;  // Interval between spawning
}
