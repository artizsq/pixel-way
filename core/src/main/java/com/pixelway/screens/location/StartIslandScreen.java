package com.pixelway.screens.location;

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
import com.pixelway.screens.minigames.FishCatchGame;
import com.pixelway.map.TiledObjectsConverter;
import com.pixelway.map.WorldContactListener;
import com.pixelway.map.WorldManager;
import com.pixelway.models.dialogs.GameDialogs;
import com.pixelway.models.characters.Player;
import com.pixelway.utils.BaseUIManager;
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
    private final WorldManager worldManager;
    private Array<Fixture> fixtures;

    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private ImageButton mainButton;
    private Texture buttonTexture;
    private final PlayerData playerData;
    private Stage gameStage;
    private Stage uiStage;
    private GameDialogs gameDialogs;

    private boolean isteleport = false;
    private BaseUIManager baseUIManager;
    private boolean isGame;

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

        gameDialogs = new GameDialogs(game);

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

        System.out.println(game.getPlayerData().fishCount);

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
            player = new Player(new Vector2(playerData.x, playerData.y), 52f, 100f, worldManager.getWorld());
        }


        worldManager.getWorld().setContactListener(new WorldContactListener(game, player));
        fixtures = TiledObjectsConverter.importObjects(tiledMap, worldManager, 1);
        debugRenderer = new Box2DDebugRenderer();

        new ImportantZone(worldManager.getWorld(), new Vector2(1060, 563), 100, 20, ImportantZone.ZoneType.SHOP);
        new ImportantZone(worldManager.getWorld(), new Vector2(1915, 555), 10, 300, ImportantZone.ZoneType.TELEPORT).setNextZone("shipMap", playerData);
        new ImportantZone((worldManager.getWorld()), new Vector2(1465, 565), 115, 20, ImportantZone.ZoneType.DIALOGUE);
        new ImportantZone(worldManager.getWorld(), new Vector2(527, 540), 40, 400, ImportantZone.ZoneType.SUPER_DIALOGUE).setStageAndDialog(uiStage, 0);
        new ImportantZone(worldManager.getWorld(), new Vector2(1100, 300), 40, 600, ImportantZone.ZoneType.SUPER_DIALOGUE).setStageAndDialog(uiStage, 1);
        new ImportantZone(worldManager.getWorld(), new Vector2(1237, 566), 40, 20, ImportantZone.ZoneType.CHEST);
        new ImportantZone(worldManager.getWorld(), new Vector2(924, 580), 50, 20, ImportantZone.ZoneType.SAVE);
        new ImportantZone(worldManager.getWorld(), new Vector2(940, 110), 48, 48, ImportantZone.ZoneType.FISH_GAME);


        new ImportantZone(worldManager.getWorld(), new Vector2(1237, 140), 70, 70, ImportantZone.ZoneType.FISHMAN_DIALOG);

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
                if (player.getInZone()) {
                    switch (player.getZoneType()) {
                        case SAVE:
                            playerData.x = player.getPosition().x + 26;
                            playerData.y = player.getPosition().y + 50;
                            playerData.currentMap = "start";
                            game.saveData();
                            new AlertWindow(uiStage, "Успешно сохранил данные!");
                            break;


                        case SHOP:
                            new ShopScreen(uiStage, game);
                            break;


                        case DIALOGUE:
                            new DialogueWindow(uiStage, game, gameDialogs.starikDialog());
                            break;


                        case CHEST:
                            List<PlayerData.InventorySlot> chestItems = new ArrayList<>();
                            if (!playerData.chestItems.contains("Медаль Рыцаря")) {
                                chestItems.add(new PlayerData.InventorySlot("Медаль Рыцаря", PlayerData.ItemType.POWER,
                                    2, 1,
                                    "Медаль XVI века, судя по всему\nиспользовалась рыцарями.", "imgs/items/strmedal.png"));
                            }
                            new ChestWindow(uiStage, game, chestItems);
                            break;


                        case FISH_GAME:

                            if (playerData.activeMissions.contains("fishing")){
                                playerData.x = player.getPosition().x + 26;
                                playerData.y = player.getPosition().y + 50;
                                game.setScreen(new FishCatchGame(game, player));
                            }
                            break;

                        case FISHMAN_DIALOG:
                            if(playerData.activeMissions.contains("fishing")){
                                if(playerData.fishCount > 0){
                                    new DialogueWindow(uiStage, game, gameDialogs.successFishmanDialog());
                                } else {
                                    new DialogueWindow(uiStage, game, gameDialogs.failFishmanDialog());
                                }



                            } else {
                                new DialogueWindow(uiStage, game, gameDialogs.fishmanDialog());
                            }
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
