package com.mygdx.gomp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.gomp.Constants.C;

public class MainGomp extends Game {

    GameScreen gameScreen;
    TitleScreen titleScreen;
    AssetManager assets;

    boolean onePlayer;
    int level;
	
	@Override
	public void create () {
        Gdx.app.setLogLevel(C.LOG_LEVEL);
        assets = new AssetManager();
        assets.load(C.MAIN_ATLAS, TextureAtlas.class);
        assets.finishLoading();

        onePlayer = true;
        level = 1;

        titleScreen = new TitleScreen(this);
        gameScreen = new GameScreen(this);
        setScreen(titleScreen);
	}

}
