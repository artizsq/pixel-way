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

import javax.xml.crypto.Data;

public class MainMenuScreen implements Screen {

    private final MainClass game;
    private Stage stage;
    private Texture backgroundTexture;
    private TextureRegionDrawable sunTextureRegion;
    private Image backgroundImage;

    private Music music;
    private PlayerData playerData;
    private int alertCount = 0;

    public MainMenuScreen(MainClass game) {
        this.game = game;
        music = game.getBgMusic();

        playerData = game.getPlayerData();

        music.setVolume(playerData.musicVolume);
        Viewport viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        backgroundTexture = new Texture(Gdx.files.internal("btns/menu/menu.png"));
        backgroundImage = new Image(backgroundTexture);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        sunTextureRegion = new TextureRegionDrawable(new Texture(Gdx.files.internal("btns/menu/sun.png")));
        sunTextureRegion.setMinSize(484, 456);
        Image sunImage = new Image(sunTextureRegion);
        sunImage.setPosition(Gdx.graphics.getWidth() - 500, Gdx.graphics.getHeight() - 460);
        stage.addActor(sunImage);


        // Кнопка "Новая игра"
        createButton(85, 630, 368, 184, "btns/menu/start.png", () -> {
            if(!DatabaseHelper.hasSaveData()){
                dispose();
                game.setScreen(new IntroScreen(game));
            } else {
                if(alertCount == 1){
                    dispose();
                    game.setScreen(new IntroScreen(game));
                } else {
                    new AlertWindow(stage, "У вас есть сохранения!\nНажмите еще раз чтобы продолжить.");
                    alertCount++;
                }
            }
        });

        // Кнопка "Загрузить игру"
        createButton(70, 380,  432, 220, "btns/menu/load.png", () -> {
            dispose();
            game.setScreen(new UploadGameScreen(game));
        });

        // Кнопка "Настройки"
        createButton(50, 120, 472, 240, "btns/menu/set.png", () -> {
            dispose();
            game.setScreen(new SettingsScreen(game));
        });

        // Кнопка "Выход"
        createButton(Gdx.graphics.getWidth() - 500, 30, 425, 210, "btns/menu/camen.png", () -> {
            dispose();
            Gdx.app.exit();
        });
    }

    private void createButton(float x, float y, float width, float height, String imagePath, Runnable action) {
        Texture buttonTexture = new Texture(Gdx.files.internal(imagePath));

        TextureRegionDrawable buttonDrawable = new TextureRegionDrawable(buttonTexture);
        buttonDrawable.setMinSize(width, height);

        ImageButton button = new ImageButton(buttonDrawable);
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
    public void show() {
    }

    @Override
    public void hide() {}

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        backgroundTexture.dispose();
        stage.dispose();
    }
}
