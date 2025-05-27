package com.pixelway.models.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PlayerBullet extends Actor {
    private World world;
    private Body body;
    private Vector2 position;
    private float width;
    private float height;
    private Texture bulletTexture;
    private int damage;
    private boolean markedForRemoval;


    private static final float DEFAULT_BULLET_WIDTH_PIXELS = 10f;
    private static final float DEFAULT_BULLET_HEIGHT_PIXELS = 20f;


    private static final float BULLET_SPEED = 32000000f; // что это за число??? :(

    private static final float SCREEN_HEIGHT = 720;

    public PlayerBullet(World world, Vector2 startPosition, int damage) {
        this.world = world;
        this.damage = damage;
        this.markedForRemoval = false;
        this.width = DEFAULT_BULLET_WIDTH_PIXELS;
        this.height = DEFAULT_BULLET_HEIGHT_PIXELS;
        this.bulletTexture = new Texture("texture/boss/bullet.png");

        this.position = new Vector2(startPosition);

        createBody(startPosition);
    }

    private void createBody(Vector2 initialPosition) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(initialPosition.x, initialPosition.y);
        bd.fixedRotation = true;
        bd.gravityScale = 0;
        body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        Fixture f = body.createFixture(shape, 0.1f);
        f.setSensor(true);
        shape.dispose();

        body.setUserData(this);

        body.setBullet(true);

        body.setLinearVelocity(0, BULLET_SPEED);
    }

    public void update(float delta) {
        position.set(body.getPosition().x - width / 2f, body.getPosition().y - height / 2f);

        if (position.y > SCREEN_HEIGHT) {
            markedForRemoval = true;

        }
    }

    public void render(Batch batch) {
        batch.draw(bulletTexture,
            position.x,
            position.y,
            width, height);
    }

    public int getDamage() {
        return damage;
    }

    public Body getBody() {
        return body;
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    public boolean isMarkedForRemoval(){
        return this.markedForRemoval;
    }

    public void dispose() {
        bulletTexture.dispose();
    }
}
