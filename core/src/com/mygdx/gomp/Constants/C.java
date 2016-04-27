package com.mygdx.gomp.Constants;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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
    // Rotation speed cap when on or close to surface.
    public static final float ROTATE_SPEED_CAP = 6f;
    // Rotation speed slowed when free-falling between surfaces.
    public static final float ROTATE_FREEFALL_CAP = ROTATE_SPEED_CAP/20f;
    public static final float PI = 3.14159f;
    public static final float PI_4_3RDS = PI * 4f / 3f;

    /** PLANETOIDS */
    public static final float GRAVITY = 9.8f;
    public static final float PLANETOID_DENSITY = 0.15f;
    public static final float INTERPLANETOID_DISTANCE_MULTIPLIER = 2f;
    public static final float GROUNDED_THRESHOLD_MULTIPLIER = 1f;
    public static final JsonValue LEVEL_MAPS =
            new JsonReader().parse(Gdx.files.internal("json/levels.json")).get("levels");

    /** FIGHTERS */
    public static final float FIGHTER_HEIGHT = 3f;  // Meters (Box2D)
    public static final float FIGHTER_MASS = 2000f;  // kg
    public static final float FIGHTER_WALK_SPEED = 3000f;  // kg
    public static final float FIGHTER_JUMP_SPEED = 50000f;  // kg
    public static final float LASER_SPEED = 50000f;
    public static final float LASER_RADIUS = 0.1f;
    public static final float LASER_COOLDOWN = 0.1f;
    public static final float LASER_RANDOM = 1.5f;  // Degrees error
    public static final float LASER_LENGTH = 3f;
    public static final float LASER_START_OFFSET = 2f;
    // Destroy bullets that fly off into the void.
    public static final float BULLET_AGE_LIMIT = 300;  // Frames
    public static final float GRENADE_SPEED = 1000f;
    public static final float GRENADE_MASS = 100f;
    public static final float GRENADE_RADIUS = 0.3f;
    public static final float GRENADE_COOLDOWN = 0.4f;
    public static final float GRENADE_RANDOM = 1.5f;  // Degrees error
    public static final int GROUP_PLAYER = -1;
    public static final int GROUP_BANDIT = -2;

    /** KEYBOARD MAPPING */
    public static final int MOVE_LEFT = Input.Keys.A;
    public static final int MOVE_RIGHT = Input.Keys.D;
    public static final int MOVE_JUMP = Input.Keys.W;
}
