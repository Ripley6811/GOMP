package com.mygdx.gomp.InputMapper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/**
 * Created by Jay on 4/28/2016.
 */
public class IM {
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
}
