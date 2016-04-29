package com.mygdx.gomp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.mygdx.gomp.Constants.C;

public class MainGomp extends Game {

    GameScreen gameScreen;
    AssetManager assets;


    boolean onePlayer;
    int level;
	
	@Override
	public void create () {
        Gdx.app.setLogLevel(C.LOG_LEVEL);
        assets = new AssetManager();
//        assets.load(Constants.MAIN_ATLAS, TextureAtlas.class);
        assets.finishLoading();

        onePlayer = true;
        level = 1;

        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
	}

}
