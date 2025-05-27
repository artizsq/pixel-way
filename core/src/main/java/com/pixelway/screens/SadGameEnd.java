package com.pixelway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelway.MainClass;

public class SadGameEnd implements Screen {
    private final MainClass game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Music music;

    private final String fullText;


    private StringBuilder currentText = new StringBuilder();
    private float timer = 0;
    private float typeSpeed = 0.1f;
    private int charIndex = 0;

    public SadGameEnd(MainClass game) {


        this.game = game;
        fullText = "Не время сдаваться, " + game.getPlayerData().playerName + "!\n\n"
        + "Нажми, чтобы загрузить последнее сохранение.";
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt"));
        font.getData().setScale(1.5f);
        game.setBgMusic("songs/sad.mp3");






    }

    @Override
    public void render(float delta) {
        // Очистка экрана — чёрный фон
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Текст по буквам
        timer += delta;
        if (timer >= typeSpeed && charIndex < fullText.length()) {
            currentText.append(fullText.charAt(charIndex++));

            timer = 0;
        }

        batch.begin();
        font.draw(batch, currentText.toString(), 60, Gdx.graphics.getHeight() - 100);
        batch.end();


        if (Gdx.input.justTouched() && charIndex >= fullText.length()) {
            game.setScreen(new UploadGameScreen(game));
        }
    }

    @Override
    public void resize(int width, int height) { }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        music.dispose();
    }
}
