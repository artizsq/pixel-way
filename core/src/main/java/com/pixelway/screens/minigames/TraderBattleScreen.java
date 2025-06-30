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
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixelway.MainClass;
import com.pixelway.map.BossFightContactListener;
import com.pixelway.map.BossWorldManager;
import com.pixelway.map.TiledObjectsConverter;
import com.pixelway.models.characters.Boss;
import com.pixelway.models.characters.MiniPlayer;
import com.pixelway.models.characters.Player;
import com.pixelway.models.characters.TraderBoss;
import com.pixelway.models.projectiles.ArrowAttack;
import com.pixelway.models.projectiles.BossAttack;
import com.pixelway.models.projectiles.PlayerBullet;
import com.pixelway.screens.GameEndScreen;
import com.pixelway.utils.HPChangeListener;
import com.pixelway.utils.SoundController;
import com.pixelway.utils.VirtualJoystick;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TraderBattleScreen implements Screen {
    private final MainClass game;
    private OrthographicCamera gameCamera;
    private OrthographicCamera uiCamera;

    private TiledMap tiledMap;
    private Array<Fixture> fixtures;
    private OrthogonalTiledMapRenderer renderer;
    private BossWorldManager bossWorldManager;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;

    private TraderBoss boss;
    private MiniPlayer miniPlayer;
    private VirtualJoystick joystick;
    private Stage uiStage, gameStage;
    private ImageButton mainButton;

    private List<BossAttack> bossAttacks = new ArrayList<>();
    private float attackTimer = 0f;
    private float attackInterval = 2f;
    private Texture arrowTexture;
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
    private static final float ARROW_SPAWN_TOP_Y = 398;
    private SoundController soundController;
    private Player player;

    private List<com.badlogic.gdx.physics.box2d.Body> bodiesToDestroy = new ArrayList<>();


    public TraderBattleScreen(MainClass game, Player player){
        this.game = game;
        this.player = player;
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

        gameStage = new Stage(new ExtendViewport(1280, 720));

        game.setBgMusic("songs/boss.mp3");

        boss = new TraderBoss(new Vector2(600, 450), 80, 160f, bossWorldManager.getWorld(), game, 1000, "texture/boss/traderboss.png", player);
        boss.setSize(300, 300); // Размер в мировых единицах
        boss.setPosition(640 - boss.getWidth() / 2, 360 - boss.getHeight() / 2); // Центр мира
        gameStage.addActor(boss);

        miniPlayer = new MiniPlayer(new Vector2(645, 235), 32f, 32f, bossWorldManager.getWorld(), game);

        bossWorldManager.getWorld().setContactListener(new BossFightContactListener());

        fixtures = TiledObjectsConverter.bossImportObjects(tiledMap, bossWorldManager, 1);
        debugRenderer = new Box2DDebugRenderer();

        arrowTexture = new Texture(Gdx.files.internal("texture/boss/arrow.png"));

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
        uiStage = new Stage(new ScreenViewport(uiCamera));

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        gameStage.addActor(boss);


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
            spawnBossAttack();
            attackTimer = 0f;
        }

        debugRenderer.render(bossWorldManager.getWorld(), gameCamera.combined);
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    private void spawnBossAttack() {
        int currentBossHP = boss.getHealth();

        int numberOfArrows = 0;
        float currentAttackInterval = 1.5f;

        if (currentBossHP > 500) {
            numberOfArrows = 5;
        } else if (currentBossHP > 0) {
            numberOfArrows = 6;
        } else {
            soundController.playWalk();
            game.setScreen(new GameEndScreen(game));
            return;
        }

        this.attackInterval = currentAttackInterval;

        float minDistance = 35f;

        Array<Vector2> spawnPositions = new Array<>();

        for (int i = 0; i < numberOfArrows; i++) {
            boolean positionValid = false;
            Vector2 spawnPosition = null;

            int maxAttempts = 20;
            int attempts = 0;

            while (!positionValid && attempts < maxAttempts) {
                float spawnX = MathUtils.random(PLAYER_AREA_MIN_X, PLAYER_AREA_MAX_X);
                float spawnY = ARROW_SPAWN_TOP_Y;

                spawnPosition = new Vector2(spawnX, spawnY);
                positionValid = true;

                for (Vector2 existingPosition : spawnPositions) {
                    if (spawnPosition.dst(existingPosition) < minDistance) {
                        positionValid = false;
                        break;
                    }
                }

                attempts++;
            }

            if (positionValid) {
                spawnPositions.add(spawnPosition);
                ArrowAttack newAttack = new ArrowAttack(bossWorldManager.getWorld(), spawnPosition, arrowTexture);
                bossAttacks.add(newAttack);
            }
        }
    }


}
