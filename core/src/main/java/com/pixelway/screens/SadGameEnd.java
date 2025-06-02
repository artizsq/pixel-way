package com.pixelway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelway.MainClass;
import com.pixelway.screens.location.StartIslandScreen;
import com.pixelway.utils.TextManager;

import java.util.List;

public class SadGameEnd implements Screen {
    private final MainClass game;
    private SpriteBatch batch;
    private BitmapFont font;

    private final String fullText;

    private Sound textSound;
    private TextManager textManager;
    private List<Texture> dialogImages;

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

        textSound = Gdx.audio.newSound(Gdx.files.internal("sounds/text/main.wav"));
        List<String> dialogs = List.of(
            fullText
        );
        dialogImages = List.of(
            new Texture(Gdx.files.internal("imgs/intro/sad.png"))
        );
        textManager = new TextManager(dialogs, 0.12f, textSound, font);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        int index = textManager.getCurrentDialogIndex();
        if (index < dialogImages.size()) {
            Texture img = dialogImages.get(index);
            batch.draw(img, Gdx.graphics.getWidth() / 2 - 432, 100, 864, 576);
        }

        textManager.draw(batch, 50, Gdx.graphics.getHeight() - 50);
        batch.end();

        textManager.update(delta);

        if (textManager.isFinished()) {
            dispose();
            game.setBgMusic("songs/game.mp3");
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
        textSound.dispose();
        for (Texture t : dialogImages) {
            t.dispose();
        }
    }
}
