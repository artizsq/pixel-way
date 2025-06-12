package com.pixelway.models.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixelway.utils.ImportantZone;
import com.pixelway.utils.SoundController;

public class Player extends Actor {
    private Vector2 position;
    private Body body;
    private int playerSpeed = 80000000;
    private float width, height;
    private Vector2 lastDirection = new Vector2(0, -1);
    private int activeZones = 0;
    private Vector2 direction = new Vector2();

    private Animation<TextureRegion> walkRightAnimation, walkLeftAnimation, walkDownAnimation, walkUpAnimation;
    private Texture idleTexture;
    private float stateTime;

    private SoundController soundController;
    private float walkSoundInterval = 0.4f; // интервал между шагами
    private float walkSoundTimer = 0f;
    private boolean inZone;
    private ImportantZone.ZoneType zoneType;


    public Player(Vector2 position, float width, float height, World world) {
        this.position = position;
        this.width = width;
        this.height = height;

        soundController = new SoundController("sounds/grass.mp3");

        Texture walkRightSheet = new Texture("anims/walk_right.png");
        Texture walkLeftSheet  = new Texture("anims/walk_left.png");
        Texture walkDownSheet  = new Texture("anims/walk_down.png");
        Texture walkUpSheet = new Texture("anims/walk_up.png");
        idleTexture            = new Texture("player.png");

        walkRightAnimation = createAnimation(walkRightSheet, 4, 0.2f);
        walkLeftAnimation  = createAnimation(walkLeftSheet, 4, 0.2f);
        walkDownAnimation  = createAnimation(walkDownSheet, 4, 0.2f);
        walkUpAnimation = createAnimation(walkUpSheet, 4, 0.2f);

        createBody(world);
        stateTime = 0f;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
    }

    private Animation<TextureRegion> createAnimation(Texture sheet, int frameCount, float frameDuration) {
        TextureRegion[][] tmp = TextureRegion.split(sheet, sheet.getWidth() / frameCount, sheet.getHeight());
        TextureRegion[] frames = new TextureRegion[frameCount];
        for (int i = 0; i < frameCount; i++) {
            frames[i] = tmp[0][i];
        }
        return new Animation<>(frameDuration, frames);
    }

    public void incrementZoneContact() {
        activeZones++;
        setInZone(true);
    }

    public void decrementZoneContact() {
        activeZones = Math.max(0, activeZones - 1);
        if (activeZones == 0) {
            setInZone(false);
        }
    }

    public int getActiveZoneCount() {
        return activeZones;
    }

    private void createBody(World world) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(position);
        bd.fixedRotation = true;
        body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 10,
            new Vector2(0, -height / 4 - 15), 0);

        Fixture f = body.createFixture(shape, 1f);
        f.setFriction(0.5f);
        f.setRestitution(0f);
        shape.dispose();
    }

    public void update(float delta, Vector2 dir) {
        this.direction.set(dir);
        if (direction.len() > 0) {
            direction.nor();
            lastDirection.set(direction);
        }

        Vector2 vel = new Vector2(direction).scl(playerSpeed);
        body.setLinearVelocity(vel);

        position.set(body.getPosition().x - width / 2,
            body.getPosition().y - height / 2);

        stateTime += delta;
//        System.out.println("X: " + body.getPosition().x + "Y: " + body.getPosition().y);

        if (dir.len() > 0) {
            walkSoundTimer += delta;
            if (walkSoundTimer >= walkSoundInterval) {
                soundController.playWalk();
                walkSoundTimer = 0f;
            }
        } else {
            walkSoundTimer = walkSoundInterval;
        }
    }


    public void render(SpriteBatch batch) {
        TextureRegion frame = getCurrentFrame();
        batch.draw(frame,
            body.getPosition().x - width / 2,
            body.getPosition().y - height / 2,
            width, height);
    }

    private TextureRegion getCurrentFrame() {
        if (direction.len() > 0) {
            if (direction.x > 0 && Math.abs(direction.x) >= Math.abs(direction.y)) {
                return walkRightAnimation.getKeyFrame(stateTime, true);
            } else if (direction.x < 0 && Math.abs(direction.x) >= Math.abs(direction.y)) {
                return walkLeftAnimation.getKeyFrame(stateTime, true);
            } else if (direction.y < 0) {
                return walkDownAnimation.getKeyFrame(stateTime, true);
            } else if (direction.y > 0) {
                return walkUpAnimation.getKeyFrame(stateTime, true);
            }
        } else {
            if (lastDirection.x > 0 && Math.abs(lastDirection.x) >= Math.abs(lastDirection.y)) {
                return walkRightAnimation.getKeyFrame(0);
            } else if (lastDirection.x < 0 && Math.abs(lastDirection.x) >= Math.abs(lastDirection.y)) {
                return walkLeftAnimation.getKeyFrame(0);
            } else if (lastDirection.y < 0) {
                return walkDownAnimation.getKeyFrame(0);
            } else if (lastDirection.y > 0) {
                return walkUpAnimation.getKeyFrame(0);
            }
        }

        return new TextureRegion(idleTexture);
    }


    public Vector2 getPosition() { return position; }
    public float getWidth()    { return width; }
    public float getHeight()   { return height; }
    public Body getBody()      { return body; }

    public SoundController getSoundController(){
        return soundController;
    }

    public void dispose() {
        idleTexture.dispose();
        walkRightAnimation.getKeyFrame(0).getTexture().dispose();
        walkLeftAnimation.getKeyFrame(0).getTexture().dispose();
        walkDownAnimation.getKeyFrame(0).getTexture().dispose();
        walkUpAnimation.getKeyFrame(0).getTexture().dispose();
        soundController.dispose();
    }

    public void setInZone(boolean inZone){
        this.inZone = inZone;
    }

    public boolean getInZone(){
        return inZone;
    }
    public void setZone(ImportantZone.ZoneType zoneType){
        this.zoneType = zoneType;
    }

    public ImportantZone.ZoneType getZoneType() {
        return zoneType;
    }
}
