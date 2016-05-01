package com.mygdx.gomp.InputMapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by Jay on 4/28/2016.
 */
public class IM {
    /** DESKTOP KEYBOARD CONTROL MAPPING */
    public static final int KEY_LEFT = Input.Keys.A;
    public static final int KEY_RIGHT = Input.Keys.D;
    public static final int KEY_UP = Input.Keys.W;
    public static final int KEY_DOWN = Input.Keys.S;

    public static boolean isFiringPrimary() {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT))
            return true;
        return false;
    }
    public static boolean isFiringSecondary() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)
                || Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT))
            return true;
        return false;
    }
    public static boolean isTransforming() {
        if (Gdx.input.isKeyJustPressed(KEY_DOWN))
            return true;
        return false;
    }
    public static boolean justPressedUp() {
        if (Gdx.input.isKeyJustPressed(KEY_UP))
            return true;
        return false;
    }
    public static boolean isPressingUp() {
        if (Gdx.input.isKeyPressed(KEY_UP))
            return true;
        return false;
    }
    public static boolean isPressingLeft() {
        if (Gdx.input.isKeyPressed(KEY_LEFT))
            return true;
        return false;
    }
    public static boolean isPressingRight() {
        if (Gdx.input.isKeyPressed(KEY_RIGHT))
            return true;
        return false;
    }
}
