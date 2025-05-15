package com.pixelway.gameScreens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;

import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
import com.pixelway.map.TiledObjectsConverter;
import com.pixelway.map.WorldContactListener;
import com.pixelway.map.WorldManager;
import com.pixelway.models.DialogData;
import com.pixelway.models.Player;
import com.pixelway.utils.ImportantZone;
import com.pixelway.windows.AlertWindow;
import com.pixelway.windows.ChestWindow;
import com.pixelway.windows.DialogueWindow;
import com.pixelway.windows.ShopScreen;

import java.util.ArrayList;
import java.util.List;

public class StartIslandScreen implements Screen {
    private final MainClass game;
    private OrthographicCamera gameCamera; // Renamed game camera
    private OrthographicCamera uiCamera;   // New UI camera
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;
    private WorldManager worldManager;
    private Array<Fixture> fixtures;

    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private ImageButton mainButton;
    private Texture buttonTexture;
    private PlayerData playerData;
    private Stage gameStage;
    private Stage uiStage;

    private boolean isteleport;
    private BaseUIManager baseUIManager;

    public StartIslandScreen(MainClass game) {
        this.game = game;
        this.worldManager = new WorldManager();
        game.clearPlayerData();
        playerData = game.getPlayerData();
    }

    public StartIslandScreen(MainClass game, Player player, PlayerData playerData, boolean isteleport) {
        this.game = game;
        this.worldManager = new WorldManager();
        this.player = player;
        this.playerData = playerData;
        this.isteleport = isteleport;
    }

    @Override
    public void show() {
        // Load map and initialize renderers
        tiledMap = new TmxMapLoader().load("maps/startIslandMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();

        // Initialize game camera and stage
        gameCamera = new OrthographicCamera();
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float desiredWorldWidth = 640 / 1.5f;
        float desiredWorldHeight = 360 / 1.5f;
        gameCamera.setToOrtho(false, desiredWorldWidth, desiredWorldHeight);
        gameCamera.position.set(desiredWorldWidth / 2f, desiredWorldHeight / 2f, 0);
        gameCamera.update();
        gameStage = new Stage(new ExtendViewport(640, 360, gameCamera));

        // Initialize UI camera and stage
        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, screenWidth, screenHeight);
        uiCamera.update();
        uiStage = new Stage(new ScreenViewport(uiCamera));

        // Set up input multiplexer to handle input for both stages (if needed)
        InputMultiplexer multiplexer = new InputMultiplexer();
//        multiplexer.addProcessor(gameStage); // If you need input on the game stage
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);



        if (isteleport) {
            player = new Player(new Vector2(1880, player.getPosition().y + 50), 52f, 100f, worldManager.getWorld());
        } else {
            player = new Player(new Vector2(playerData.x, playerData.y + 50), 52f, 100f, worldManager.getWorld());
        }


        worldManager.getWorld().setContactListener(new WorldContactListener(game, player));
        fixtures = TiledObjectsConverter.importObjects(tiledMap, worldManager, 1 / 1f);
        debugRenderer = new Box2DDebugRenderer();

        new ImportantZone(worldManager.getWorld(), new Vector2(1060, 563), 100, 20, ImportantZone.ZoneType.SHOP);
        new ImportantZone(worldManager.getWorld(), new Vector2(1915, 555), 10, 300, ImportantZone.ZoneType.TELEPORT).setNextZone("shipMap", playerData);
        new ImportantZone((worldManager.getWorld()), new Vector2(1465, 565), 115, 20, ImportantZone.ZoneType.DIALOGUE);
//        new ImportantZone(worldManager.getWorld(), new Vector2(527, 540), 40, 400, ImportantZone.ZoneType.SUPER_DIALOGUE).setStageAndDialog(uiStage, 0);
//        new ImportantZone(worldManager.getWorld(), new Vector2(1100, 300), 40, 600, ImportantZone.ZoneType.SUPER_DIALOGUE).setStageAndDialog(uiStage, 1);
        new ImportantZone(worldManager.getWorld(), new Vector2(1237, 566), 40, 20, ImportantZone.ZoneType.CHEST);
        new ImportantZone(worldManager.getWorld(), new Vector2(924, 580), 50, 20, ImportantZone.ZoneType.SAVE);

        baseUIManager = new BaseUIManager(uiStage, playerData, game); // Pass uiStage
        baseUIManager.init();


        buttonTexture = new Texture(Gdx.files.internal("btns/mainBtn.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        mainButton = new ImageButton(buttonDrawable);




        float buttonWidth = 240 * 2f;
        float buttonHeight = 240 * 2f;


        float buttonX = screenWidth - buttonWidth - 20;
        float buttonY = 20;

        mainButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.log("BUTTON", "Clicked!");
                if (player.getInZone()) {
                    switch (player.getZoneType()) {
                        case SAVE:
                            try {
                                playerData.x = player.getPosition().x;
                                playerData.y = player.getPosition().y;
                                playerData.currentMap = "start";
                                game.saveData();
                                new AlertWindow(uiStage, "Успешно сохранил данные!");
                            } catch (Exception e){
                                new AlertWindow(uiStage, "" + e);
                            }
                            break;


                        case SHOP:
                            new ShopScreen(uiStage, game);
                            break;


                        case DIALOGUE:
                            DialogData forthNode = new DialogData();
                            forthNode.name = "Старик";
                            forthNode.text = "Клан Тонель разрушил ВСЁ! Мы сумели сохранить лишь главный дом, зайди туда как-нибудь.";
                            forthNode.imagePath = "starik.png";
                            forthNode.option1 = "Хорошо";


                            DialogData thirdNode = new DialogData();
                            thirdNode.name = "Старик";
                            thirdNode.text = "Ты находишься в деревне Лолокек! Правда, сейчас оно почти разрушено...";
                            thirdNode.imagePath = "starik.png";
                            thirdNode.option1 = "Что случилось?";
                            thirdNode.option2 = "Ладно";
                            thirdNode.newDialogData = forthNode;

                            DialogData secondNode = new DialogData();
                            secondNode.name = "Старик";
                            secondNode.text = playerData.playerName + "? Интересное имя, очень необычное в наших краях.";
                            secondNode.imagePath = "starik.png";
                            secondNode.option1 = "Что за края?";
                            secondNode.option2 = "Спс...";
                            secondNode.newDialogData = thirdNode;

                            DialogData firstNode = new DialogData();
                            firstNode.name = "Старик";
                            firstNode.text = "Здравствуй, юноша. Хм, я раньше тебя не видел, кто ты?";
                            firstNode.imagePath = "starik.png";
                            firstNode.option1 = "Меня зовут...";
                            firstNode.option2 = "Неважно.";
                            firstNode.newDialogData = secondNode;

                            new DialogueWindow(uiStage, game, firstNode);
                            break;



                        case CHEST:
                            List<PlayerData.InventorySlot> chestItems = new ArrayList<>();
                            if(!playerData.chestItems.contains("Медаль Рыцаря")) {
                                chestItems.add(new PlayerData.InventorySlot("Медаль Рыцаря", PlayerData.ItemType.POWER,
                                    2, 1,
                                    "Медаль XVI века, судя по всему\nиспользовалась рыцарями.", "imgs/items/strmedal.png"));
                            }
                            new ChestWindow(uiStage, game, chestItems);
                            break;
                    }
                }
            }
        });
        uiStage.addActor(mainButton);


    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldManager.getWorld().step(1 / 60f, 6, 2);

        updateCameraPosition();


        renderer.setView(gameCamera);
        renderer.render();

        batch.setProjectionMatrix(gameCamera.combined);
        batch.begin();
        player.render(batch);
        batch.end();

        gameStage.act(delta);
        gameStage.draw();

        uiStage.act(delta);
        uiStage.draw();

        if (baseUIManager.getJoystick() != null) {
            player.update(delta, baseUIManager.getJoystick().getDirection());
        }

        debugRenderer.render(worldManager.getWorld(), gameCamera.combined);
    }

    private void updateCameraPosition() {
        int mapWidth = tiledMap.getProperties().get("width", Integer.class) * tiledMap.getProperties().get("tilewidth", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class) * tiledMap.getProperties().get("tileheight", Integer.class);

        float cameraHalfWidth = gameCamera.viewportWidth / 2f;
        float cameraHalfHeight = gameCamera.viewportHeight / 2f;

        float playerX = player.getPosition().x + player.getWidth() / 2f;
        float playerY = player.getPosition().y + player.getHeight() / 2f;

        float cameraX = Math.max(cameraHalfWidth, Math.min(mapWidth - cameraHalfWidth, playerX));
        float cameraY = Math.max(cameraHalfHeight, Math.min(mapHeight - cameraHalfHeight, playerY));

        gameCamera.position.set(cameraX, cameraY, 0);
        gameCamera.update();
    }

    @Override
    public void resize(int width, int height) {
        gameStage.getViewport().update(width, height, true);
        uiStage.getViewport().update(width, height, true);
        gameCamera.update();
        uiCamera.update();
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
        tiledMap.dispose();
        renderer.dispose();
        worldManager.dispose();
        player.dispose();
        debugRenderer.dispose();
        buttonTexture.dispose();
        gameStage.dispose();
        uiStage.dispose();
        if (baseUIManager != null && baseUIManager.getJoystick() != null) {
            baseUIManager.getJoystick().dispose();
        }
        batch.dispose();
    }
}
