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
    public static final int LOG_LEVEL = Application.LOG_NONE;
//    public static final int LOG_LEVEL = Application.LOG_ERROR;
//    public static final int LOG_LEVEL = Application.LOG_INFO;
//    public static final int LOG_LEVEL = Application.LOG_DEBUG;



    /** PLANETOIDS */
    public static final float LARGE_PLANETOID_RADIUS = 20f;
    public static final float MEDIUM_PLANETOID_RADIUS = 10f;
    public static final float SMALL_PLANETOID_RADIUS = 5f;
    public static final JsonValue LEVEL_MAPS =
            new JsonReader().parse(Gdx.files.internal("json/levels.json")).get("levels");
}
