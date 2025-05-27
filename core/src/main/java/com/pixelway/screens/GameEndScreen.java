package com.pixelway.screens;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelway.MainClass;


public class GameEndScreen implements Screen {

    private final MainClass game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Music music;

    private final String fullText =
        "Когда дым рассеялся,\n" +
            "и последний удар был нанесён,\n" +
            "тишина окутала долину.\n\n" +
            "Деревня лежала в руинах, но она была свободна.\n" +
            "Жители, уцелевшие в аду, начали строить новую жизнь.\n\n" +
            "Клан Тонель, лишённый своего лидера, не смог продержаться долго.\n" +
            "Они пали один за другим, как тени прошлого.\n\n" +
            "Ты стал героем.\n" +
            "Тем, кто положил конец эпохе страха и тьмы.\n\n" +
            "Твоё имя будет жить в легендах.\n" +
            "А мир... наконец вздохнул свободно.";


    private StringBuilder currentText = new StringBuilder();
    private float timer = 0;
    private float typeSpeed = 0.1f;
    private int charIndex = 0;

    public GameEndScreen(MainClass game) {
        this.game = game;
        this.game.setBgMusic("songs/happy.mp3");
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt"));
        font.getData().setScale(1f);
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

        // Клик — возврат в меню
        if (Gdx.input.justTouched() && charIndex >= fullText.length()) {
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
        music.dispose();
    }
}

