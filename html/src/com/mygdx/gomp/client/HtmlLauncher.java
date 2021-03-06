package com.mygdx.gomp.client;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.backends.gwt.GwtApplication;
import com.badlogic.gdx.backends.gwt.GwtApplicationConfiguration;
import com.mygdx.gomp.MainGomp;

public class HtmlLauncher extends GwtApplication {

        @Override
        public GwtApplicationConfiguration getConfig () {
                return new GwtApplicationConfiguration(1200, 800);
        }

        @Override
        public ApplicationListener createApplicationListener () {
                return new MainGomp();
        }
}