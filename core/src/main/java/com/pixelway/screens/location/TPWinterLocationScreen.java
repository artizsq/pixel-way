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
import com.pixelway.map.TiledObjectsConverter;
import com.pixelway.map.WorldContactListener;
import com.pixelway.map.WorldManager;
import com.pixelway.models.characters.Player;
import com.pixelway.models.dialogs.GameDialogs;
import com.pixelway.utils.BaseUIManager;
import com.pixelway.utils.ImportantZone;
import com.pixelway.windows.AlertWindow;
import com.pixelway.windows.DialogueWindow;

public class TPWinterLocationScreen implements Screen {

    private final MainClass game;
    private OrthographicCamera gameCamera;   // Game world camera
    private OrthographicCamera uiCamera;     // UI camera
    private String mapPath;
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
    private Stage gameStage;   // Stage for the game world
    private Stage uiStage;     // Stage for the UI
    private boolean isTeleport = false;
    private BaseUIManager baseUIManager;
    private boolean isLongTeleport;
    private GameDialogs gameDialogs;


    public TPWinterLocationScreen(MainClass game, Player player, PlayerData playerData) {
        this.game = game;

        this.worldManager = new WorldManager();
        this.player = player;
        this.playerData = playerData;
    }
    public TPWinterLocationScreen(MainClass game, Player player, PlayerData playerData, boolean isTeleport, boolean isLongTeleport) {
        this.game = game;
        this.isTeleport = isTeleport;
        this.worldManager = new WorldManager();
        this.player = player;
        this.playerData = playerData;
        this.isLongTeleport = isLongTeleport;

        this.gameDialogs = new GameDialogs(game);
    }



    @Override
    public void show() {
        tiledMap = new TmxMapLoader().load("maps/winter1.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();


        gameCamera = new OrthographicCamera();
        gameCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        gameCamera.viewportWidth = 640 / 1.5f;  // Apply zoom level
        gameCamera.viewportHeight = 360 / 1.5f; // Apply zoom level
        gameCamera.position.set(gameCamera.viewportWidth / 2f, gameCamera.viewportHeight / 2f, 0);
        gameCamera.update();
        gameStage = new Stage(new ExtendViewport(640, 360, gameCamera)); // Use the initial viewport dimensions

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        uiCamera.update();
        uiStage = new Stage(new ScreenViewport(uiCamera));

        InputMultiplexer multiplexer = new InputMultiplexer();
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        if(isTeleport){
            player = new Player(new Vector2(920, player.getPosition().y + 50), 52f, 100f, worldManager.getWorld());
        } else if (isLongTeleport){
            player = new Player(new Vector2(550, 200), 52f, 100f, worldManager.getWorld());

        }else {
            player = new Player(new Vector2(playerData.x, playerData.y), 52f, 100f, worldManager.getWorld());
        }



        worldManager.getWorld().setContactListener(new WorldContactListener(game, player));
        fixtures = TiledObjectsConverter.importObjects(tiledMap, worldManager, 1 / 1f);
        debugRenderer = new Box2DDebugRenderer();

        baseUIManager = new BaseUIManager(uiStage, playerData, game);
        baseUIManager.init();

        // Create save button
        buttonTexture = new Texture(Gdx.files.internal("btns/mainBtn.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        saveButton = new ImageButton(buttonDrawable);

        // Position and size the save button
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
                if (player.getInZone()){
                    switch (player.getZoneType()){
                        case TELEPORT_WINDOW:
                            new DialogueWindow(uiStage, game, gameDialogs.darkDialogue(uiStage, player, "winter"));

                            break;
                        case SAVE:
                            playerData.x = player.getPosition().x + 26;
                            playerData.y = player.getPosition().y + 50;
                            playerData.currentMap = "winter";
                            game.saveData();
                            new AlertWindow(uiStage, "Успешно сохранил данные!");
                            break;
                    }
                }
            }
        });
        uiStage.addActor(saveButton);


        new ImportantZone(worldManager.getWorld(), new Vector2(480, 240), 80, 20, ImportantZone.ZoneType.TELEPORT_WINDOW);
        new ImportantZone(worldManager.getWorld(), new Vector2(953, 190), 5, 250, ImportantZone.ZoneType.TELEPORT).setNextZone("winter2", game.getPlayerData());
        new ImportantZone(worldManager.getWorld(), new Vector2(673, 280), 70, 70, ImportantZone.ZoneType.SAVE);


        new ImportantZone(worldManager.getWorld(), new Vector2(93, 190), 5, 250, ImportantZone.ZoneType.SUPER_DIALOGUE).setStageAndDialog(uiStage, 2);



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
