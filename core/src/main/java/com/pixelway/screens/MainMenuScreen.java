package com.pixelway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixelway.MainClass;

import com.pixelway.database.DatabaseHelper;
import com.pixelway.database.PlayerData;
import com.pixelway.screens.location.StartIslandScreen;
import com.pixelway.windows.AlertWindow;

public class MainMenuScreen implements Screen {

    private final MainClass game;
    private Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;

    // --- START OF CORRECTION ---
    // Declare a field for the invisible button texture so it's loaded only once
    private Texture invisibleButtonTexture;
    // --- END OF CORRECTION ---

    private static final float WORLD_WIDTH = 1920;
    private static final float WORLD_HEIGHT = 1080;
    private Music music;
    private PlayerData playerData;
    private int alertCount = 0;

    public MainMenuScreen(MainClass game) {
        this.game = game;
        music = game.getBgMusic();

        playerData = game.getPlayerData();

        music.setVolume(playerData.musicVolume);
        Viewport viewport = new FitViewport(WORLD_WIDTH, WORLD_HEIGHT);
        stage = new Stage(viewport); // Stage correctly creates its own internal SpriteBatch here.
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("imgs/mainMenu.jpg"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true); // Растягиваем на весь экран
        stage.addActor(backgroundImage);

        // --- START OF CORRECTION ---
        // Load the invisible button texture ONCE in the constructor
        invisibleButtonTexture = new Texture("none.png");
        // --- END OF CORRECTION ---

        createButton(85, 603, 343, 179, () -> {
            if(!DatabaseHelper.hasSaveData()){
                // You called dispose() here, which caused the problem if not managed carefully by MainClass
                // If MainClass's setScreen already disposes the old screen, you don't need this.
                // Assuming MainClass.setScreen handles disposal.
                game.setBgMusic("songs/game.mp3");
                game.setScreen(new StartIslandScreen(game)); // Use game.setScreenAndDisposeOld if you have it!
            } else {
                if(alertCount == 1){
                    // Same as above, if MainClass.setScreen handles disposal, this dispose() is redundant.
                    game.setBgMusic("songs/game.mp3");
                    game.setScreen(new StartIslandScreen(game)); // Use game.setScreenAndDisposeOld if you have it!
                } else {
                    new AlertWindow(stage, "У вас есть сохранения!\nНажмите еще раз чтобы продолжить.");
                    alertCount++;
                }
            }
        });

        createButton(70, 380,  397, 201, () -> {
            // Same as above, if MainClass.setScreen handles disposal, this dispose() is redundant.
            game.setScreen(new UploadGameScreen(game)); // Use game.setScreenAndDisposeOld if you have it!
        });

        createButton(50, 120, 445, 223, () -> {
            // Same as above, if MainClass.setScreen handles disposal, this dispose() is redundant.
            game.setScreen(new SettingsScreen(game)); // Use game.setScreenAndDisposeOld if you have it!
        }); // Настройки

        createButton(1600, 30, 300, 175, () -> {
            Gdx.app.exit();
        }); // Выход из игры
    }

    private void createButton(float x, float y, float width, float height, Runnable action) {
        // --- START OF CORRECTION ---
        // Use the already loaded invisibleButtonTexture
        TextureRegionDrawable invisibleDrawable = new TextureRegionDrawable(invisibleButtonTexture);
        // --- END OF CORRECTION ---

        ImageButton button = new ImageButton(invisibleDrawable); // Use the shared drawable
        button.setColor(1, 1, 1, 0); // Make it invisible
        button.setBounds(x, y, width, height);
        button.addListener(event -> {
            if (event.toString().equals("touchDown")) {
                action.run();
                return true;
            }
            return false;
        });
        stage.addActor(button);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void show() {}

    @Override
    public void hide() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void dispose() {
        backgroundTexture.dispose(); // Dispose background texture
        stage.dispose();             // Dispose stage (which includes its internal SpriteBatch and actors)

        // --- START OF CORRECTION ---
        // Dispose the invisible button texture
        if (invisibleButtonTexture != null) {
            invisibleButtonTexture.dispose();
        }
        // --- END OF CORRECTION ---
    }
}
