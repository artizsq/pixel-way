//package com.pixelway.gameScreens.minigames;
//
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.InputMultiplexer;
//import com.badlogic.gdx.Screen;
//import com.badlogic.gdx.graphics.Color;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.Texture;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.graphics.g2d.TextureRegion;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
//import com.badlogic.gdx.scenes.scene2d.InputEvent;
//import com.badlogic.gdx.scenes.scene2d.Stage;
//import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
//import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
//import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
//import com.badlogic.gdx.utils.Array;
//import com.badlogic.gdx.utils.viewport.ScreenViewport;
//import com.badlogic.gdx.physics.box2d.Body;
//import com.badlogic.gdx.physics.box2d.BodyDef;
//import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
//import com.badlogic.gdx.physics.box2d.Contact;
//import com.badlogic.gdx.physics.box2d.ContactImpulse;
//import com.badlogic.gdx.physics.box2d.ContactListener;
//import com.badlogic.gdx.physics.box2d.Fixture;
//import com.badlogic.gdx.physics.box2d.FixtureDef;
//import com.badlogic.gdx.physics.box2d.Manifold;
//import com.badlogic.gdx.physics.box2d.PolygonShape;
//import com.badlogic.gdx.physics.box2d.World;
//import com.badlogic.gdx.math.Vector2;
//
//import com.pixelway.MainClass;
//import com.pixelway.database.PlayerData;
//import com.pixelway.map.WorldManagerBossFight;
//import com.pixelway.models.characters.Boss;
//import com.pixelway.models.characters.MiniPlayer;
//import com.pixelway.models.projectiles.PlayerBullet;
//import com.pixelway.models.bossattacks.BossAttack;
//import com.pixelway.models.bossattacks.Shard;
//import com.pixelway.models.bossattacks.Boulder;
//import com.pixelway.utils.VirtualJoystick;
//
//public class BossFightScreen implements Screen {
//
//    private final MainClass game;
//    private OrthographicCamera gameCamera;
//    private SpriteBatch batch;
//    private WorldManagerBossFight worldManager;
//    private Box2DDebugRenderer debugRenderer;
//    private ShapeRenderer shapeRenderer;
//
//    private MiniPlayer miniPlayer;
//    private Boss boss;
//    private PlayerData playerData;
//
//    private Stage uiStage;
//    private VirtualJoystick virtualJoystick;
//    private ImageButton attackButton;
//    private Texture attackButtonTexture;
//
//    private Array<PlayerBullet> activePlayerBullets;
//    private Array<Body> bodiesToDestroy;
//    public BossFightScreen(MainClass game, PlayerData playerData) {
//        this.game = game;
//        this.playerData = playerData;
//        this.activePlayerBullets = new Array<>();
//        this.bodiesToDestroy = new Array<>();
//    }
//
//    @Override
//    public void show() {
//        gameCamera = new OrthographicCamera();
//        gameCamera.viewportWidth = GAME_WIDTH_PIXELS;
//        gameCamera.viewportHeight = GAME_HEIGHT_PIXELS;
//        gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0);
//        gameCamera.update();
//
//        batch = new SpriteBatch();
//        shapeRenderer = new ShapeRenderer();
//
//        worldManager = new WorldManagerBossFight();
//        debugRenderer = new Box2DDebugRenderer();
//
//        createPlayAreaBoundaries();
//
//        // Спавн игрока: в центре белого квадрата
//        Vector2 miniPlayerStartPosition = new Vector2(
//            PLAY_AREA_X + PLAY_AREA_WIDTH / 2,
//            PLAY_AREA_Y + PLAY_AREA_HEIGHT / 2
//        );
//        miniPlayer = new MiniPlayer(miniPlayerStartPosition, worldManager.getWorld(), playerData); // Без размеров, они в классе MiniPlayer
//
//        // Спавн босса: сверху, по центру экрана
//        Vector2 bossStartPosition = new Vector2(GAME_WIDTH_PIXELS / 2f, GAME_HEIGHT_PIXELS * 0.8f);
//        boss = new Boss(worldManager.getWorld(), bossStartPosition); // Без размеров, они в классе Boss
//
//        // Настройка UI (джойстик, кнопка атаки)
//        uiStage = new Stage(new ScreenViewport());
//        float joystickSize = Gdx.graphics.getHeight() * 0.2f;
//        float joystickPadding = Gdx.graphics.getWidth() * 0.05f;
//        float joystickX = joystickPadding + joystickSize / 2;
//        float joystickY = joystickPadding + joystickSize / 2;
//        virtualJoystick = new VirtualJoystick(joystickX, joystickY, joystickSize / 2, joystickSize * 0.3f, game);
//        uiStage.addActor(virtualJoystick);
//
//        // Путь к текстуре кнопки атаки остаётся без изменений
//        attackButtonTexture = new Texture(Gdx.files.internal("btns/mainBtn.png"));
//        TextureRegionDrawable attackButtonDrawable = new TextureRegionDrawable(new TextureRegion(attackButtonTexture));
//        attackButton = new ImageButton(attackButtonDrawable);
//
//        float buttonSize = Gdx.graphics.getHeight() * 0.15f;
//        float buttonPadding = Gdx.graphics.getWidth() * 0.05f;
//        attackButton.setSize(buttonSize, buttonSize);
//        attackButton.setPosition(Gdx.graphics.getWidth() - buttonSize - buttonPadding, buttonPadding);
//        attackButton.addListener(new ClickListener() {
//            @Override
//            public void clicked(InputEvent event, float x, float y) {
//                PlayerBullet bullet = new PlayerBullet(worldManager.getWorld(), miniPlayer.getPosition(), playerData.strength);
//                activePlayerBullets.add(bullet);
//                miniPlayer.attack();
//            }
//        });
//        uiStage.addActor(attackButton);
//
//        InputMultiplexer multiplexer = new InputMultiplexer();
//        multiplexer.addProcessor(uiStage);
//        Gdx.input.setInputProcessor(multiplexer);
//
//        worldManager.getWorld().setContactListener(new BossFightContactListener());
//    }
//
//    private void createPlayAreaBoundaries() {
//        World world = worldManager.getWorld();
//        float ppm = WorldManagerBossFight.PIXELS_PER_METER;
//
//        BodyDef bodyDef = new BodyDef();
//        bodyDef.type = BodyDef.BodyType.StaticBody;
//
//        FixtureDef fixtureDef = new FixtureDef();
//        PolygonShape shape = new PolygonShape();
//        fixtureDef.shape = shape;
//        fixtureDef.friction = 0.0f;
//        fixtureDef.restitution = 0.0f;
//
//        // Нижняя стена
//        bodyDef.position.set((PLAY_AREA_X + PLAY_AREA_WIDTH / 2) / ppm, (PLAY_AREA_Y - 1) / ppm);
//        Body groundBody = world.createBody(bodyDef);
//        shape.setAsBox(PLAY_AREA_WIDTH / 2 / ppm, 1 / ppm);
//        groundBody.createFixture(fixtureDef).setUserData("ground");
//        bodiesToDestroy.add(groundBody);
//
//        // Верхняя стена
//        bodyDef.position.set((PLAY_AREA_X + PLAY_AREA_WIDTH / 2) / ppm, (PLAY_AREA_Y + PLAY_AREA_HEIGHT + 1) / ppm);
//        Body ceilingBody = world.createBody(bodyDef);
//        shape.setAsBox(PLAY_AREA_WIDTH / 2 / ppm, 1 / ppm);
//        ceilingBody.createFixture(fixtureDef).setUserData("ceiling");
//        bodiesToDestroy.add(ceilingBody);
//
//        // Левая стена
//        bodyDef.position.set((PLAY_AREA_X - 1) / ppm, (PLAY_AREA_Y + PLAY_AREA_HEIGHT / 2) / ppm);
//        Body leftWallBody = world.createBody(bodyDef);
//        shape.setAsBox(1 / ppm, PLAY_AREA_HEIGHT / 2 / ppm);
//        leftWallBody.createFixture(fixtureDef).setUserData("wall");
//        bodiesToDestroy.add(leftWallBody);
//
//        // Правая стена
//        bodyDef.position.set((PLAY_AREA_X + PLAY_AREA_WIDTH + 1) / ppm, (PLAY_AREA_Y + PLAY_AREA_HEIGHT / 2) / ppm);
//        Body rightWallBody = world.createBody(bodyDef);
//        shape.setAsBox(1 / ppm, PLAY_AREA_HEIGHT / 2 / ppm);
//        rightWallBody.createFixture(fixtureDef).setUserData("wall");
//        bodiesToDestroy.add(rightWallBody);
//
//        shape.dispose();
//    }
//
//
//    @Override
//    public void render(float delta) {
//        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        worldManager.getWorld().step(1 / 60f, 6, 2);
//
//        Vector2 miniPlayerMovementDirection = virtualJoystick.getDirection();
//        miniPlayer.update(delta, miniPlayerMovementDirection);
//
//        boss.update(delta);
//
//        for (int i = activePlayerBullets.size - 1; i >= 0; i--) {
//            PlayerBullet bullet = activePlayerBullets.get(i);
//            bullet.update(delta);
//            if (!bullet.isAlive()) {
//                bodiesToDestroy.add(bullet.getBody());
//                bullet.dispose();
//                activePlayerBullets.removeIndex(i);
//            }
//        }
//
//        for (BossAttack attack : boss.getActiveAttacks()) {
//            if (!attack.isAlive()) {
//                bodiesToDestroy.add(attack.getBody());
//            }
//        }
//
//        for (Body body : bodiesToDestroy) {
//            if (body != null) {
//                worldManager.getWorld().destroyBody(body);
//            }
//        }
//        bodiesToDestroy.clear();
//
//        gameCamera.update();
//
//        // Отрисовка белого квадрата
//        shapeRenderer.setProjectionMatrix(gameCamera.combined);
//        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//        shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.rect(PLAY_AREA_X, PLAY_AREA_Y, PLAY_AREA_WIDTH, PLAY_AREA_HEIGHT);
//        shapeRenderer.end();
//
//        batch.setProjectionMatrix(gameCamera.combined);
//        batch.begin();
//        miniPlayer.render(batch);
//        boss.render(batch);
//        for (PlayerBullet bullet : activePlayerBullets) {
//            bullet.render(batch);
//        }
//        batch.end();
//
//        uiStage.act(delta);
//        uiStage.draw();
//
//        debugRenderer.render(worldManager.getWorld(), gameCamera.combined);
//
//        if (miniPlayer.getHealth() <= 0) {
//            System.out.println("Game Over! MiniPlayer defeated.");
//            // Переход на экран проигрыша
//        }
//        if (boss.getHealth() <= 0) {
//            System.out.println("You Win! Boss defeated.");
//            // Переход на экран победы
//        }
//    }
//
//    @Override
//    public void resize(int width, int height) {
//        uiStage.getViewport().update(width, height, true);
//
//        float joystickSize = height * 0.2f;
//        float joystickPadding = width * 0.05f;
//        virtualJoystick.setBounds(joystickPadding, joystickPadding, joystickSize, joystickSize);
//
//        float buttonSize = height * 0.15f;
//        float buttonPadding = width * 0.05f;
//        attackButton.setSize(buttonSize, buttonSize);
//        attackButton.setPosition(width - buttonSize - buttonPadding, buttonPadding);
//    }
//
//    @Override
//    public void pause() {}
//    @Override
//    public void resume() {}
//    @Override
//    public void hide() {}
//
//    @Override
//    public void dispose() {
//        batch.dispose();
//        shapeRenderer.dispose();
//        worldManager.dispose();
//        debugRenderer.dispose();
//        miniPlayer.dispose();
//        boss.dispose();
//        uiStage.dispose();
//        attackButtonTexture.dispose();
//        if (virtualJoystick != null) {
//            virtualJoystick.dispose();
//        }
//        for (PlayerBullet bullet : activePlayerBullets) {
//            bullet.dispose();
//        }
//        activePlayerBullets.clear();
//
//        for (Body body : bodiesToDestroy) {
//            if (body != null) {
//                worldManager.getWorld().destroyBody(body);
//            }
//        }
//        bodiesToDestroy.clear();
//    }
//
//    private class BossFightContactListener implements ContactListener {
//
//        @Override
//        public void beginContact(Contact contact) {
//            Fixture fixtureA = contact.getFixtureA();
//            Fixture fixtureB = contact.getFixtureB();
//
//            Object userDataA = fixtureA.getBody().getUserData();
//            Object userDataB = fixtureB.getBody().getUserData();
//
//            if (userDataA == null || userDataB == null) return;
//
//            // Коллизии: Пуля игрока + Босс
//            if (userDataA instanceof PlayerBullet && userDataB instanceof Boss) {
//                PlayerBullet bullet = (PlayerBullet) userDataA;
//                Boss boss = (Boss) userDataB;
//                boss.takeDamage(bullet.getDamage());
//                bullet.markForRemoval();
//            } else if (userDataB instanceof PlayerBullet && userDataA instanceof Boss) {
//                PlayerBullet bullet = (PlayerBullet) userDataB;
//                Boss boss = (Boss) userDataA;
//                boss.takeDamage(bullet.getDamage());
//                bullet.markForRemoval();
//            }
//            // Коллизии: Атака босса (осколок ИЛИ булыжник) + Игрок
//            else if ((userDataA instanceof Shard || userDataA instanceof Boulder) && userDataB instanceof MiniPlayer) {
//                BossAttack attack = (BossAttack) userDataA;
//                MiniPlayer miniPlayer = (MiniPlayer) userDataB;
//                miniPlayer.takeDamage(attack.getDamage());
//                bodiesToDestroy.add(attack.getBody());
//                attack.markForRemoval();
//            } else if ((userDataB instanceof Shard || userDataB instanceof Boulder) && userDataA instanceof MiniPlayer) {
//                BossAttack attack = (BossAttack) userDataB;
//                MiniPlayer miniPlayer = (MiniPlayer) userDataA;
//                miniPlayer.takeDamage(attack.getDamage());
//                bodiesToDestroy.add(attack.getBody());
//                attack.markForRemoval();
//            }
//            // Коллизии: Пуля игрока + Стены игровой зоны
//            else if (userDataA instanceof PlayerBullet && (userDataB instanceof String && (userDataB.equals("ground") || userDataB.equals("ceiling") || userDataB.equals("wall")))) {
//                ((PlayerBullet) userDataA).markForRemoval();
//            } else if (userDataB instanceof PlayerBullet && (userDataA instanceof String && (userDataA.equals("ground") || userDataA.equals("ceiling") || userDataA.equals("wall")))) {
//                ((PlayerBullet) userDataB).markForRemoval();
//            }
//            // Коллизии: Атака босса (осколок ИЛИ булыжник) + Стены игровой зоны
//            else if ((userDataA instanceof Shard || userDataA instanceof Boulder) && (userDataB instanceof String && (userDataB.equals("ground") || userDataB.equals("ceiling") || userDataB.equals("wall")))) {
//                ((BossAttack) userDataA).markForRemoval();
//                bodiesToDestroy.add(fixtureA.getBody());
//            } else if ((userDataB instanceof Shard || userDataB instanceof Boulder) && (userDataA instanceof String && (userDataA.equals("ground") || userDataA.equals("ceiling") || userDataA.equals("wall")))) {
//                ((BossAttack) userDataB).markForRemoval();
//                bodiesToDestroy.add(fixtureB.getBody());
//            }
//        }
//
//        @Override
//        public void endContact(Contact contact) {
//        }
//
//        @Override
//        public void preSolve(Contact contact, Manifold oldManifold) {}
//        @Override
//        public void postSolve(Contact contact, ContactImpulse impulse) {}
//    }
//}
