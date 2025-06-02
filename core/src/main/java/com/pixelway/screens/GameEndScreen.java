package com.pixelway.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.pixelway.MainClass;
import com.pixelway.utils.TextManager;

import org.w3c.dom.Text;

import java.util.List;


public class GameEndScreen implements Screen {

    private final MainClass game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture image;
    private TextManager textManager;


    public GameEndScreen(MainClass game) {
        this.game = game;
        this.game.setBgMusic("songs/happy.mp3");
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt"));
        font.getData().setScale(1f);

        image = new Texture(Gdx.files.internal("imgs/intro/gameEnd.png"));

        Sound textSound = Gdx.audio.newSound(Gdx.files.internal("sounds/text/main.wav"));
        List<String> dialogTexts = List.of(
        "С последним ударом Голем рухнул на землю, сотрясая окрестности. Тишина, что наступила после его падения, была пугающе непривычной.\n" +
            "Клан Тонель, лишившись своего вождя, начал стремительно разрушаться изнутри. Без жестокой воли Голема - они были ничем.\n" +
            "Жители, впервые за долгое время, смогли вдохнуть свободно. Их страх рассеялся, будто ночной туман при свете рассвета.\n" +
            "Герой, пришедший из иного мира, не остался, чтобы собирать плоды своей победы. Он просто исчез, так же внезапно, как и появился.\n" +
            "Но память о нём останется навсегда.\n" +
            "Потому что именно он положил конец эпохе ужаса.\n" +
            "Потому что именно он дал миру шанс на новую жизнь..."
        );

        textManager = new TextManager(dialogTexts, 0.12f, textSound, font);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();


        batch.draw(image, Gdx.graphics.getWidth() / 2 - 430, 100, 861, 483);

        textManager.draw(batch, 50, Gdx.graphics.getHeight() - 50);
        batch.end();

        textManager.update(delta);

        if (textManager.isFinished()) {
            dispose();
            game.setBgMusic("songs/main.mp3");
            game.setScreen(new MainMenuScreen(game));
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
    }
}

