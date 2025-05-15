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
import com.pixelway.gameScreens.StartIslandScreen;
import com.pixelway.windows.AlertWindow;

public class MainMenuScreen implements Screen {

    private final MainClass game;
    private Stage stage;
    private Texture backgroundTexture;
    private Image backgroundImage;

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
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("imgs/mainMenu.jpg"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true); // Растягиваем на весь экран
        stage.addActor(backgroundImage);


        createButton(85, 603, 343, 179, () -> {
            if(!DatabaseHelper.hasSaveData()){
                game.setBgMusic("songs/game.mp3");
                game.setScreen(new StartIslandScreen(game));
            } else {
                if(alertCount == 1){
                    game.setBgMusic("songs/game.mp3");
                    game.setScreen(new StartIslandScreen(game));
                } else {
                    new AlertWindow(stage, "У вас есть сохранения!\nНажмите еще раз чтобы продолжить.");
                    alertCount++;
                }

            }

        });
        createButton(70, 380,  397, 201, () -> {
            game.setScreen(new UploadGameScreen(game));
        });
        createButton(50, 120, 445, 223, () -> game.setScreen(new SettingsScreen(game)));// Настройки
        createButton(1600, 30, 300, 175, () -> {Gdx.app.exit();}); // Выход из игры

    }

    private void createButton(float x, float y, float width, float height, Runnable action) {
        TextureRegionDrawable invisibleTexture = new TextureRegionDrawable(new Texture("none.png")); // Прозрачная текстура
        ImageButton button = new ImageButton(invisibleTexture);
        button.setColor(1, 1, 1, 0);
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
        // Очистка экрана
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Обновление и отрисовка сцены
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Обновляем Viewport при изменении размера окна
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
        backgroundTexture.dispose();
        stage.dispose();

    }
}
