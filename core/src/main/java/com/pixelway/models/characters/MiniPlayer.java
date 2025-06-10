package com.pixelway.models.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixelway.MainClass;
import com.pixelway.models.projectiles.PlayerBullet;
import com.pixelway.screens.SadGameEnd;
import com.pixelway.utils.SoundController;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MiniPlayer extends Actor {
    private Vector2 position;
    private Body body;
    private float width, height;
    private Texture miniPlayerTexture, bulletTexture;
    private List<PlayerBullet> bullets = new ArrayList<>();
    private Vector2 direction = new Vector2();
    private int playerSpeed = 10000;
    private float stateTime;
    private int health;
    private World world;
    private int shield;
    private MainClass game;
    private SoundController soundController;
    private Sound pluhSound;


    public MiniPlayer(Vector2 position, float width, float height, World world, MainClass game) {
        this.position = new Vector2(position.x, position.y);
        this.width = width;
        this.height = height;
        this.world = world;
        this.health = game.getPlayerData().hp;
        this.shield = game.getPlayerData().shield;
        this.game = game;
        this.soundController = new SoundController("sounds/shield.mp3");
        pluhSound = Gdx.audio.newSound(Gdx.files.internal("sounds/pluh.mp3"));


        miniPlayerTexture = new Texture("texture/boss/miniplayer.png");
        bulletTexture = new Texture("texture/boss/bullet.png");

        createBody(world);
        body.setUserData(this);
        stateTime = 0f;
    }

    public List<PlayerBullet> getBullets() {
        return bullets;
    }

    private void createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
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

    public int getHealth() {
        return health;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    public void update(float delta, Vector2 dir) {
        this.direction.set(dir);
        if (direction.len() > 0) {
            direction.nor();
        }

        Vector2 vel = new Vector2(direction).scl(playerSpeed);
        body.setLinearVelocity(vel);

        position.set(body.getPosition().x - width / 2f,
            body.getPosition().y - height / 2f);

        stateTime += delta;
//        System.out.println("x: " + body.getPosition().x + " y: " + body.getPosition().y);

        Iterator<PlayerBullet> iter = bullets.iterator();
        while (iter.hasNext()) {
            PlayerBullet bullet = iter.next();
            bullet.update(delta);
            if (bullet.isMarkedForRemoval()) {
                world.destroyBody(bullet.getBody());
                iter.remove();
            }
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(miniPlayerTexture, position.x, position.y, width, height);
        for (PlayerBullet bullet : bullets) {
            bullet.render(batch);
        }
    }

    public void shoot() {
        bullets.add(new PlayerBullet(world, new Vector2(position.x + width / 2f, position.y + height), game.getPlayerData().strength));
    }

    public void takeDamage(int amount) {
        if (shield >= amount) {
            shield -= amount;

            soundController.playWalk();
        } else {

            int remainingDamage = amount - shield;
            shield = 0;
            health -= remainingDamage;
            game.getPlayerData().substractHP(remainingDamage);
            soundController.setWalkSound("sounds/hit.mp3");
            soundController.playWalk();
            if (health <= 0) {
                pluhSound.play(0.3f);
                game.setScreen(new SadGameEnd(game));
            }
        }
        System.out.println(shield);
    }


    public void dispose() {
        miniPlayerTexture.dispose();
        bulletTexture.dispose();
         world.destroyBody(body);
    }

    public Body getBody() {
        return body;
    }
}
