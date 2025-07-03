package com.pixelway.screens.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixelway.MainClass;
import com.pixelway.map.BossFightContactListener;
import com.pixelway.map.BossWorldManager;
import com.pixelway.map.TiledObjectsConverter;
import com.pixelway.models.characters.Boss;
import com.pixelway.models.projectiles.BossAttack;
import com.pixelway.models.projectiles.Boulder;
import com.pixelway.models.characters.MiniPlayer;
import com.pixelway.models.projectiles.PlayerBullet;
import com.pixelway.models.projectiles.Shard;
import com.pixelway.screens.GameEndScreen;
import com.pixelway.utils.HPChangeListener;
import com.pixelway.utils.SoundController;
import com.pixelway.utils.VirtualJoystick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BossBattleScreen implements Screen {

    private final MainClass game;
    private OrthographicCamera gameCamera;
    private OrthographicCamera uiCamera;

    private TiledMap tiledMap;
    private Array<Fixture> fixtures;
    private OrthogonalTiledMapRenderer renderer;
    private BossWorldManager bossWorldManager;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;

    private Boss boss;
    private MiniPlayer miniPlayer;
    private VirtualJoystick joystick;
    private Stage uiStage;
    private ImageButton mainButton;

    private List<BossAttack> bossAttacks = new ArrayList<>();
    private float attackTimer = 0f;
    private float attackInterval = 2f;
    private Texture shardTexture;
    private Texture boulderTexture;
    private Label playerHP;
    private Image hpPng;
    private Label bossHP;
    private BitmapFont font;


    private HPChangeListener playerHPListener = new HPChangeListener() {
        @Override
        public void onHPchanged(int HP) {
            playerHP.setText(HP+"");
        }
    };
    private HPChangeListener bossHPListener = new HPChangeListener() {
        @Override
        public void onHPchanged(int HP) {
            bossHP.setText(HP+"");
        }
    };

    private static final float PLAYER_AREA_MIN_X = 495;

    private static final float PLAYER_AREA_MAX_X = 783;

    private SoundController soundController;

    private static final float SHARD_SPAWN_Y = 398;

    private static final float BOULDER_SPAWN_LEFT_X = 495;

    private static final float BOULDER_SPAWN_RIGHT_X = 760;

    private static final float BOULDER_SPAWN_Y_RANGE_MIN = 150;

    private static final float BOULDER_SPAWN_Y_RANGE_MAX = 350;


    private static final int PHASE_ONE_THRESHOLD = 2000;
    private static final int PHASE_TWO_THRESHOLD = 1000;


    private List<com.badlogic.gdx.physics.box2d.Body> bodiesToDestroy = new ArrayList<>();

    public BossBattleScreen(MainClass game) {
        this.game = game;
        this.bossWorldManager = new BossWorldManager();
        this.soundController = new SoundController("sounds/hit.mp3");



    }

    @Override
    public void show() {
        tiledMap = new TmxMapLoader().load("maps/bossFight.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();

        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, 1280, 720);
        gameCamera.position.set(640, 360, 0);
        gameCamera.update();

        game.setBgMusic("songs/boss.mp3");

        boss = new Boss(new Vector2(535, 450), 200f, 200f, bossWorldManager.getWorld(), game, 3000, "texture/boss/boss.png");
        miniPlayer = new MiniPlayer(new Vector2(645, 235), 32f, 32f, bossWorldManager.getWorld(), game);

        bossWorldManager.getWorld().setContactListener(new BossFightContactListener());

        fixtures = TiledObjectsConverter.bossImportObjects(tiledMap, bossWorldManager, 1);
        debugRenderer = new Box2DDebugRenderer();

        shardTexture = new Texture(Gdx.files.internal("texture/boss/attack2.png"));
        boulderTexture = new Texture(Gdx.files.internal("texture/boss/attack1.png"));

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
        uiStage = new Stage(new ScreenViewport(uiCamera));

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);




        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("texture/boss/shoot.png"))));
        buttonDrawable.setMinSize(240, 240);
        mainButton = new ImageButton(buttonDrawable);
        mainButton.setBounds(Gdx.graphics.getWidth() - 480, 20, 240 * 2f, 240 * 2f);
        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                miniPlayer.shoot();
            }
        });
        uiStage.addActor(mainButton);

        joystick = new VirtualJoystick(242, 300, 240, 100, game);
        uiStage.addActor(joystick);

        font = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        font.getData().setScale(1.5f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        hpPng = new Image(new Texture(Gdx.files.internal("imgs/hp.png")));
        hpPng.setScale(2f);
        playerHP = new Label(game.getPlayerData().hp +"", labelStyle);
        bossHP = new Label(boss.getHealth()+"", labelStyle);


        bossHP.setPosition(900, 950);
        playerHP.setPosition(50, 950);
        hpPng.setPosition(110, 960);


        game.getPlayerData().addHPListener(playerHPListener);
        boss.addHPListener(bossHPListener);

        uiStage.addActor(bossHP);
        uiStage.addActor(playerHP);
        uiStage.addActor(hpPng);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        for (com.badlogic.gdx.physics.box2d.Body body : bodiesToDestroy) {
            if (bossWorldManager.getWorld().getBodyCount() > 0 && !bossWorldManager.getWorld().isLocked()) {
                bossWorldManager.getWorld().destroyBody(body);
            }
        }
        bodiesToDestroy.clear();

        bossWorldManager.getWorld().step(1 / 60f, 6, 2);

        gameCamera.update();
        renderer.setView(gameCamera);
        renderer.render();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        boss.render(batch);
        miniPlayer.render(batch);

        for (BossAttack attack : bossAttacks) {
            attack.render(batch);
        }
        batch.end();

        uiStage.act(delta);
        uiStage.draw();

        miniPlayer.update(delta, joystick.getDirection());
        boss.update(delta);

        Iterator<PlayerBullet> playerBulletIterator = miniPlayer.getBullets().iterator();
        while (playerBulletIterator.hasNext()) {
            PlayerBullet bullet = playerBulletIterator.next();
            bullet.update(delta);
            if (bullet.isMarkedForRemoval()) {
                bodiesToDestroy.add(bullet.getBody());
                playerBulletIterator.remove();
            }
        }

        Iterator<BossAttack> attackIterator = bossAttacks.iterator();
        while (attackIterator.hasNext()) {
            BossAttack attack = attackIterator.next();
            attack.update(delta);
            if (attack.isMarkedForRemoval()) {
                bodiesToDestroy.add(attack.getBody());
                attackIterator.remove();
            }
        }

        attackTimer += delta;
        if (attackTimer >= attackInterval) {
            spawnBossAttacksBasedOnHealth();
            attackTimer = 0f;
        }

//        debugRenderer.render(bossWorldManager.getWorld(), gameCamera.combined);
    }

    private void spawnBossAttacksBasedOnHealth() {
        int currentBossHP = boss.getHealth();

        int numberOfShards = 0;
        int numberOfBoulders = 0;
        float currentAttackInterval = 0;

        if (currentBossHP > PHASE_ONE_THRESHOLD) {
            numberOfShards = MathUtils.random(3, 4);
            numberOfBoulders = 0;
            currentAttackInterval = MathUtils.random(1.0f, 2.0f);
        } else if (currentBossHP > PHASE_TWO_THRESHOLD) {
            numberOfShards = 0;
            numberOfBoulders = MathUtils.random(2, 3);
            currentAttackInterval = MathUtils.random(0.8f, 1.5f);
        } else if (currentBossHP > 0){
            numberOfShards = MathUtils.random(2, 3);
            numberOfBoulders = MathUtils.random(1, 2);
            currentAttackInterval = MathUtils.random(0.5f, 1.0f);
        } else {
            soundController.playWalk();
            game.setScreen(new GameEndScreen(game));
        }

        this.attackInterval = currentAttackInterval;

        for (int i = 0; i < numberOfShards; i++) {
            float spawnX = MathUtils.random(PLAYER_AREA_MIN_X, PLAYER_AREA_MAX_X);
            Vector2 spawnPosition = new Vector2(spawnX, SHARD_SPAWN_Y);
            BossAttack newAttack = new Shard(bossWorldManager.getWorld(), spawnPosition, shardTexture);
            bossAttacks.add(newAttack);

        }

        for (int i = 0; i < numberOfBoulders; i++) {
            float spawnY = MathUtils.random(BOULDER_SPAWN_Y_RANGE_MIN, BOULDER_SPAWN_Y_RANGE_MAX);
            boolean spawnFromLeft = MathUtils.randomBoolean();
            float spawnX = spawnFromLeft ? BOULDER_SPAWN_LEFT_X : BOULDER_SPAWN_RIGHT_X;

            Vector2 spawnPosition = new Vector2(spawnX, spawnY);
            BossAttack newAttack = new Boulder(bossWorldManager.getWorld(), spawnPosition, boulderTexture);
            bossAttacks.add(newAttack);
        }
    }


    @Override
    public void resize(int width, int height) {
        uiStage.getViewport().update(width, height, true);
        gameCamera.update();
        uiCamera.update();
    }

    @Override
    public void dispose() {
        batch.dispose();
        debugRenderer.dispose();
        renderer.dispose();
        uiStage.dispose();
        joystick.dispose();
        shardTexture.dispose();
        boulderTexture.dispose();


        Array<com.badlogic.gdx.physics.box2d.Body> bodies = new Array<>();
        bossWorldManager.getWorld().getBodies(bodies);
        for (com.badlogic.gdx.physics.box2d.Body body : bodies) {
            bossWorldManager.getWorld().destroyBody(body);
        }
        bossWorldManager.dispose();
        boss.removeHPListener(bossHPListener);
        game.getPlayerData().removeHPListener(bossHPListener);

    }

    public void pause() {
    }

    public void resume() {
    }

    public void hide() {
    }
}
