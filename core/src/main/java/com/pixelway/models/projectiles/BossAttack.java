package com.pixelway.models.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

public abstract class BossAttack {
    protected World world;
    protected Body body;
    protected Vector2 position;
    protected float width;
    protected float height;
    protected Texture attackTexture;
    protected int damage;
    protected boolean markedForRemoval;


    public BossAttack(World world, Vector2 startPosition, Texture texture, int damage, float width, float height) {
        this.world = world;
        this.damage = damage;
        this.markedForRemoval = false;
        this.attackTexture = texture;
        this.width = width;
        this.height = height;
        this.position = startPosition;

        createBody(startPosition);
    }

    protected void createBody(Vector2 startPosition) {
        BodyDef bDef = new BodyDef();
        bDef.type = BodyDef.BodyType.DynamicBody;
        bDef.position.set(startPosition.x + width / 2f, startPosition.y + height / 2f);
        body = world.createBody(bDef);
        body.setUserData(this);
        body.setGravityScale(0);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        FixtureDef fDef = new FixtureDef();
        fDef.shape = shape;
        fDef.density = 0.1f;
        fDef.friction = 0f;
        fDef.restitution = 0f;
        fDef.isSensor = true;

        body.createFixture(fDef);
        shape.dispose();
    }

    public int getDamage() {
        return damage;
    }

    public void update(float delta) {
        position.set(body.getPosition().x - width / 2f, body.getPosition().y - height / 2f);
        if (shouldRemove()) {
            markForRemoval();
        }
    }

    public void render(Batch batch) {
        batch.draw(attackTexture, position.x, position.y, width, height);
    }

    public Body getBody() {
        return body;
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public boolean shouldRemove() {
        final float PLAYER_ZONE_MIN_X = 540;
        final float PLAYER_ZONE_MAX_X = 760;
        final float PLAYER_ZONE_MIN_Y = 130;
        final float PLAYER_ZONE_MAX_Y = 398;

        final float ZONE_PADDING = 0;

        boolean remove = false;


        if (position.x + width < PLAYER_ZONE_MIN_X - ZONE_PADDING) {
            remove = true;
        }

        else if (position.x > PLAYER_ZONE_MAX_X + ZONE_PADDING) {

            remove = true;
        }

        else if (position.y + height < PLAYER_ZONE_MIN_Y - ZONE_PADDING) {
            remove = true;
        }

        else if (position.y > PLAYER_ZONE_MAX_Y + ZONE_PADDING) {
            remove = true;
        }

        return remove;
    }


    public void dispose() {
        attackTexture.dispose();
    }
}
