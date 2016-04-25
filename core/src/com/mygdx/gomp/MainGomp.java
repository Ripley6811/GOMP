package com.mygdx.gomp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.mygdx.gomp.Constants.C;

public class MainGomp extends Game {

    GameScreen gameScreen;



	
	@Override
	public void create () {
        Gdx.app.setLogLevel(C.LOG_LEVEL);
//        assets = new AssetManager();
//        assets.load(Constants.MAIN_ATLAS, TextureAtlas.class);
//        assets.finishLoading();

        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
	}

}
