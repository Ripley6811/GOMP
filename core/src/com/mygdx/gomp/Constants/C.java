package com.mygdx.gomp.Constants;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

/**
 * Created by Jay on 4/25/2016.
 */
public class C {
    /** LOGGING LEVELS */
    // Application logging levels from lowest to highest. Choose one.
//    public static final int LOG_LEVEL = Application.LOG_NONE;
//    public static final int LOG_LEVEL = Application.LOG_ERROR;
//    public static final int LOG_LEVEL = Application.LOG_INFO;
    public static final int LOG_LEVEL = Application.LOG_DEBUG;

    public static final float ROTATE_SPEED_CAP = 2f;
    public static final float PI_4_3RDS = 4.18879f;  // pi * 4/3

    /** PLANETOIDS */
    public static final float GRAVITY = 9.8f;
    public static final float PLANETOID_DENSITY = 1f;
    public static final float LARGE_PLANETOID_RADIUS = 20f;  // Meters
    public static final float MEDIUM_PLANETOID_RADIUS = 10f;
    public static final float SMALL_PLANETOID_RADIUS = 5f;
    public static final JsonValue LEVEL_MAPS =
            new JsonReader().parse(Gdx.files.internal("json/levels.json")).get("levels");

    /** FIGHTERS */
    public static final float FIGHTER_HEIGHT = 3f;  // Meters (Box2D)
    public static final float FIGHTER_MASS = 2000f;  // kg
}
