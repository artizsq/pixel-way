package com.pixelway.models.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixelway.MainClass;
import com.pixelway.screens.GameEndScreen;
import com.pixelway.utils.HPChangeListener;

import java.util.ArrayList;
import java.util.List;

public class Boss extends Actor {
    protected Vector2 position;
    protected Body body;
    protected float width, height;
    protected Texture bossTexture;
    protected int health;
    protected MainClass game;
    protected transient List<HPChangeListener> hpChangeListeners = new ArrayList<>();

    public Boss(Vector2 position, float width, float height, World world, MainClass game, int health, String texturePath) {
        this.position = new Vector2(position.x, position.y);
        this.width = width;
        this.height = height;
        this.game = game;
        this.health = health;

        bossTexture = new Texture(texturePath);

        createBody(world);
        body.setUserData(this);
    }

    public int getHealth() {
        return health;
    }

    private void createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.StaticBody;
        bd.position.set(position.x + width / 2f, position.y + height / 2f);
        bd.fixedRotation = true;
        body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2f, height / 2f);

        Fixture f = body.createFixture(shape, 1f);
        f.setFriction(0.5f);
        f.setRestitution(0f);
        shape.dispose();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void update(float delta) {
    }

    public void render(SpriteBatch batch) {
        batch.draw(bossTexture, position.x, position.y, width, height);
    }

    public void takeDamage(int amount) {
        this.health -= amount;
        notifyHPListener();
        if (health <= 0) {
            onBossDefeated();
        }
    }

    // Метод, который будет переопределяться в наследниках
    protected void onBossDefeated() {
        dispose();
        game.setScreen(new GameEndScreen(game));
    }

    public void dispose() {
        bossTexture.dispose();
    }

    public Body getBody() {
        return body;
    }

    public void addHPListener(HPChangeListener listener) {
        hpChangeListeners.add(listener);
    }

    public void removeHPListener(HPChangeListener listener) {
        hpChangeListeners.remove(listener);
    }

    public void notifyHPListener() {
        for (HPChangeListener listener : hpChangeListeners) {
            listener.onHPchanged(this.health);
        }
    }
}
