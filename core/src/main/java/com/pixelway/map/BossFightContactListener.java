package com.pixelway.map;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.pixelway.models.characters.Boss;
import com.pixelway.models.characters.MiniPlayer;
import com.pixelway.models.projectiles.BossAttack;
import com.pixelway.models.projectiles.PlayerBullet;

public class BossFightContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        Object userDataA = fixtureA.getBody().getUserData();
        Object userDataB = fixtureB.getBody().getUserData();

        if (userDataA == null || userDataB == null) return;

        if (userDataA instanceof PlayerBullet && userDataB instanceof Boss) {
            PlayerBullet bullet = (PlayerBullet) userDataA;
            Boss targetBoss = (Boss) userDataB;
            targetBoss.takeDamage(bullet.getDamage());
            bullet.markForRemoval();

        } else if (userDataB instanceof PlayerBullet && userDataA instanceof Boss) {
            PlayerBullet bullet = (PlayerBullet) userDataB;
            Boss targetBoss = (Boss) userDataA;
            targetBoss.takeDamage(bullet.getDamage());
            bullet.markForRemoval();
        }

        else if (userDataA instanceof BossAttack && userDataB instanceof MiniPlayer) {
            BossAttack attack = (BossAttack) userDataA;
            MiniPlayer targetPlayer = (MiniPlayer) userDataB;

            targetPlayer.takeDamage(attack.getDamage());

            attack.markForRemoval();

        } else if (userDataB instanceof BossAttack && userDataA instanceof MiniPlayer) {
            BossAttack attack = (BossAttack) userDataB;
            MiniPlayer targetPlayer = (MiniPlayer) userDataA;
            targetPlayer.takeDamage(attack.getDamage());

            attack.markForRemoval();
        }
    }


    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
    }
}
