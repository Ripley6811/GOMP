package com.mygdx.gomp;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.mygdx.gomp.Constants.C;
import com.mygdx.gomp.DynamicAssets.Blob;
import com.mygdx.gomp.DynamicAssets.Fighter;
import com.mygdx.gomp.StaticAssets.Planetoid;

/**
 * Created by Jay on 5/8/2016.
 */
public class ListenerClass implements ContactListener {
    private static final String TAG = ListenerClass.class.getName();
    private GameScreen gameScreen;

    public ListenerClass(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }

    @Override
    public void endContact(Contact contact) {
        if (contact.getFixtureB().getUserData() instanceof Fighter) {
            Fighter fighter = (Fighter) contact.getFixtureB().getUserData();
            if (contact.getFixtureA().getUserData() instanceof Planetoid) {
                Planetoid p = (Planetoid) contact.getFixtureA().getUserData();

                if (p == fighter.getBase()) {
                    fighter.setRecharging(false);
                }
            }
        }
        if (contact.getFixtureA().getUserData() instanceof Blob
                && contact.getFixtureB().getUserData() instanceof Fighter) {
            Fighter fighter = (Fighter) contact.getFixtureB().getUserData();
            fighter.blobContacts -= 1;
        }
    }

    @Override
    public void beginContact(Contact contact) {
        /**
         * Check from bullet/weapon perspective instead.
         * Resolve bullet/weapon collisions.
         *
         * TODO: Send event to respective class(es) for processing
         */
//        if (contact.getFixtureA().getBody() == player.body) {
//            Gdx.app.debug(TAG, "Contact Fixture A is Player: " + contact.getFixtureA().toString() );
//        }
        if (contact.getFixtureB().getUserData() instanceof Fighter) {
            Gdx.app.debug(TAG, "Contact Fixture B is Fighter: " + contact.getFixtureB().toString() );
            Fighter fighter = (Fighter) contact.getFixtureB().getUserData();
            if (contact.getFixtureA().getUserData() instanceof Planetoid) {
//                    Planetoid p = (Planetoid) contact.getFixtureA().getUserData();

                if (contact.getFixtureA().getUserData() == fighter.getBase()) {
                    fighter.setRecharging(true);
                }
            }
        }
        // Bullet should always be fixtureB. World object ordering.
        if (contact.getFixtureB().getBody().isBullet()) {
            int damage = gameScreen.bullets.resolveContact(contact);

            Object fixtureRef = contact.getFixtureA().getUserData();
            if (fixtureRef instanceof Fighter) {
                float remainder = ((Fighter) fixtureRef).takeDamage(damage);
                Gdx.app.log(TAG, "Fighter health: " + remainder);
            } else if (fixtureRef instanceof Blob) {
                ((Blob) fixtureRef).takeDamage(damage);
            }
        } else if (contact.getFixtureA().getUserData() instanceof Blob
                && contact.getFixtureB().getUserData() instanceof Fighter) {
            Fighter fighter = (Fighter) contact.getFixtureB().getUserData();
            fighter.blobContacts += 1;
        }

//        if (gameScreen.onePlayer) {
//            if (contact.getFixtureA().getBody() == gameScreen.bandit.body) {
//                Gdx.app.debug(TAG, "Contact Fixture A is Bandit");
//            }
//            if (contact.getFixtureB().getBody() == gameScreen.bandit.body) {
//                Gdx.app.debug(TAG, "Contact Fixture B is Bandit");
//            }
//        }
    }
}
