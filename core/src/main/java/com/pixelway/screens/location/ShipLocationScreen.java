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
import com.pixelway.utils.BaseUIManager;
import com.pixelway.utils.ImportantZone;
import com.pixelway.utils.SoundController;
import com.pixelway.windows.TeleportWindow;

public class ShipLocationScreen implements Screen {

    private final MainClass game;
    private OrthographicCamera gameCamera;
    private OrthographicCamera uiCamera;

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
    private boolean isTeleport;
    private BaseUIManager baseUIManager;

    public ShipLocationScreen(MainClass game, Player player, PlayerData playerData) {
        this.game = game;
        this.worldManager = new WorldManager();
        this.player = player;
        this.playerData = playerData;
    }

    public ShipLocationScreen(MainClass game, Player player, PlayerData playerData, boolean isTeleport) {
        this.game = game;
        this.worldManager = new WorldManager();
        this.player = player;
        this.playerData = playerData;
        this.isTeleport = isTeleport;
    }

    @Override
    public void show() {
        tiledMap = new TmxMapLoader().load("maps/shipMap.tmx");
        renderer = new OrthogonalTiledMapRenderer(tiledMap);
        batch = new SpriteBatch();

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
        multiplexer.addProcessor(uiStage);
        Gdx.input.setInputProcessor(multiplexer);

        if (isTeleport){
            player = new Player(new Vector2(772, 483), 52f, 100f, worldManager.getWorld());
            SoundController soundController = player.getSoundController();
            soundController.setWalkSound("sounds/wooden.mp3");

        } else {
            player = new Player(new Vector2(33 + 10, player.getPosition().y + 50), 52f, 100f, worldManager.getWorld());

        }

        worldManager.getWorld().setContactListener(new WorldContactListener(game, player));
        fixtures = TiledObjectsConverter.importObjects(tiledMap, worldManager, 1 / 1f);
        debugRenderer = new Box2DDebugRenderer();


        baseUIManager = new BaseUIManager(uiStage, playerData, game);
        baseUIManager.init();


        buttonTexture = new Texture(Gdx.files.internal("btns/mainBtn.png"));
        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(new TextureRegion(buttonTexture));
        mainButton = new ImageButton(buttonDrawable);


        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float buttonWidth = 240 * 2f;
        float buttonHeight = 240 * 2f;
        float buttonX = screenWidth - buttonWidth - 20;
        float buttonY = 20;
        mainButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
        mainButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(player.getInZone()){
                    switch (player.getZoneType()){
                        case TELEPORT_WINDOW:
                            new TeleportWindow(uiStage, "start", game, player);
                    }
                }
            }
        });
        uiStage.addActor(mainButton);

        new ImportantZone(worldManager.getWorld(), new Vector2(0, 550), 10, 300, ImportantZone.ZoneType.TELEPORT).setNextZone("startMap", playerData);
        new ImportantZone(worldManager.getWorld(), new Vector2(450, 480), 10, 64, ImportantZone.ZoneType.SOUND, "sounds/wooden.mp3");
        new ImportantZone(worldManager.getWorld(), new Vector2(380, 480), 10, 64, ImportantZone.ZoneType.SOUND, "sounds/grass.mp3");
        new ImportantZone(worldManager.getWorld(), new Vector2(880, 400), 30, 70, ImportantZone.ZoneType.TELEPORT_WINDOW);

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
