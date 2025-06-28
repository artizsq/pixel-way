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
import com.pixelway.database.ChestData;
import com.pixelway.database.PlayerData;
import com.pixelway.map.TiledObjectsConverter;
import com.pixelway.map.WorldContactListener;
import com.pixelway.map.WorldManager;
import com.pixelway.models.dialogs.GameDialogs;
import com.pixelway.models.characters.Player;
import com.pixelway.utils.BaseUIManager;
import com.pixelway.utils.ImportantZone;
import com.pixelway.utils.SoundController;
import com.pixelway.utils.loot.ChestLootGenerator;
import com.pixelway.windows.AlertWindow;
import com.pixelway.windows.ChestWindow;
import com.pixelway.windows.DialogueWindow;
import com.pixelway.windows.TeleportWindow;

import java.util.ArrayList;
import java.util.List;

public class TradeLocationScreen implements Screen {

    private final MainClass game;
    private OrthographicCamera gameCamera;
    private OrthographicCamera uiCamera;
    private ChestData chestData;
    private TiledMap tiledMap;
    private OrthogonalTiledMapRenderer renderer;
    private Player player;
    private WorldManager worldManager;
    private Array<Fixture> fixtures;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private ImageButton saveButton;
    private Texture buttonTexture;
    private PlayerData playerData;
    private Stage gameStage;
    private Stage uiStage;
    private String teleportType;
    private BaseUIManager baseUIManager;
    private GameDialogs gameDialogs;
    private ChestLootGenerator chestLootGenerator;

    public TradeLocationScreen(MainClass game, Player player, PlayerData playerData, String teleportType) {
        this.game = game;
        this.teleportType = teleportType;
        this.worldManager = new WorldManager();
        this.player = player;
        this.playerData = playerData;
        chestData = game.loadChestData();
        this.chestLootGenerator = game.getChestLootGenerator();
    }



    @Override
    public void show() {
        tiledMap = new TmxMapLoader().load("maps/bazar.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();
        gameDialogs = new GameDialogs(game);


        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameCamera.viewportWidth = 640 / 1.5f;
        gameCamera.viewportHeight = 360 / 1.5f;
        gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0);
        gameCamera.update();
        gameStage = new Stage(new ExtendViewport(640, 360, gameCamera));

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
        uiStage = new Stage(new ScreenViewport(uiCamera));

        InputMultiplexer multiplexer = new InputMultiplexer();
//        multiplexer.addProcessor(gameStage);
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        baseUIManager = new BaseUIManager(uiStage, playerData, game);
        baseUIManager.init();

        buttonTexture = new Texture(Gdx.files.internal("btns/mainBtn.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        saveButton = new ImageButton(buttonDrawable);

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonWidth = 240 * 2f;
        float buttonHeight = 240 * 2f;
        float buttonX = screenWidth - buttonWidth - 20;
        float buttonY = 20;
        saveButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        saveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(player.getInZone()){
                    switch (player.getZoneType()){
                        case SAVE:
                            playerData.x = player.getPosition().x + 26;
                            playerData.y = player.getPosition().y + 50;
                            playerData.currentMap = "trade";
                            game.saveData();

                            new AlertWindow(uiStage, "Успешно сохранил данные!");

                            break;

                        case TELEPORT_WINDOW:
                            new TeleportWindow(uiStage, "trade", game, player);
                            break;

                        case TRADE1_DIALOG:
                            if(playerData.dialogIDS.contains("trade1")){
                                new DialogueWindow(uiStage, game, gameDialogs.failtrade1Dialog());
                            } else {
                                new DialogueWindow(uiStage, game, gameDialogs.trade1Dialog());
                            }
                            break;

                        case TRADE2_DIALOG:
                            if(playerData.dialogIDS.contains("trade2")){
                                new DialogueWindow(uiStage, game, gameDialogs.failtrade2Dialog());
                            } else {
                                new DialogueWindow(uiStage, game, gameDialogs.trade2Dialog(player));
                            }
                            break;

                        case CHEST:
                            List<PlayerData.InventorySlot> currentChestContents;

                            if (!chestData.allChestsState.containsKey("trade_1")) {
                                currentChestContents = chestLootGenerator.generateLootForChest();
                                chestData.setChestContents("trade_1", currentChestContents);
                            } else {
                                currentChestContents = chestData.getChestContents("start_1");
                            }


                            new ChestWindow(uiStage, game, currentChestContents, "trade_1");
                            break;

                    }
                }
            }
        });
        uiStage.addActor(saveButton);

        switch (teleportType){
            case "load":
                player = new Player(new Vector2(playerData.x, playerData.y), 52f, 100f, worldManager.getWorld());
                break;

            case "tp":
                player = new Player(new Vector2(93, 698), 52f, 100f, worldManager.getWorld());
                SoundController soundController = player.getSoundController();
                soundController.setWalkSound("sounds/wooden.mp3");
                break;

            case "bossFight":
                player = new Player(new Vector2(playerData.x, playerData.y), 52f, 100f, worldManager.getWorld());
                new DialogueWindow(uiStage, game, gameDialogs.traderWinDialog());
                break;
        }


        worldManager.getWorld().setContactListener(new WorldContactListener(game, player));
        fixtures = TiledObjectsConverter.importObjects(tiledMap, worldManager, 1 / 1f);
        debugRenderer = new Box2DDebugRenderer();




        new ImportantZone(worldManager.getWorld(), new Vector2(97, 755), 64, 20 , ImportantZone.ZoneType.TELEPORT_WINDOW);
        new ImportantZone(worldManager.getWorld(), new Vector2(545, 870), 70, 70 , ImportantZone.ZoneType.SAVE);
        new ImportantZone(worldManager.getWorld(), new Vector2(863, 885), 60, 20 , ImportantZone.ZoneType.TRADE1_DIALOG);
        new ImportantZone(worldManager.getWorld(), new Vector2(1124, 885), 60, 20 , ImportantZone.ZoneType.TRADE2_DIALOG);
        new ImportantZone(worldManager.getWorld(), new Vector2(315, 675), 10, 60 , ImportantZone.ZoneType.SOUND, "sounds/wooden.mp3");
        new ImportantZone(worldManager.getWorld(), new Vector2(369, 675), 10, 60 , ImportantZone.ZoneType.SOUND, "sounds/grass.mp3");
        new ImportantZone(worldManager.getWorld(), new Vector2(1244, 890), 50, 10 , ImportantZone.ZoneType.CHEST);






    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        worldManager.getWorld().step(1 / 60f, 6, 2);

        int mapWidth = tiledMap.getProperties().get("width", Integer.class) * tiledMap.getProperties().get("tilewidth", Integer.class);
        int mapHeight = tiledMap.getProperties().get("height", Integer.class) * tiledMap.getProperties().get("tileheight", Integer.class);
        float cameraHalfWidth = gameCamera.viewportWidth / 2f;
        float cameraHalfHeight = gameCamera.viewportHeight / 2f;
        float cameraX = Math.max(cameraHalfWidth, Math.min(mapWidth - cameraHalfWidth, player.getPosition().x + player.getWidth() / 2f));
        float cameraY = Math.max(cameraHalfHeight, Math.min(mapHeight - cameraHalfHeight, player.getPosition().y + player.getHeight() / 2f));
        gameCamera.position.set(cameraX, cameraY, 0);
        gameCamera.update();

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

//        debugRenderer.render(worldManager.getWorld(), gameCamera.combined);

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
