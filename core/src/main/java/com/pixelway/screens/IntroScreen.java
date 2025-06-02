package com.pixelway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelway.MainClass;
import com.pixelway.screens.location.StartIslandScreen;
import com.pixelway.utils.TextManager;

import java.util.List;

public class IntroScreen implements Screen {
    private SpriteBatch batch;
    private BitmapFont font;
    private Sound talkSound;
    private TextManager textManager;
    private MainClass game;

    private List<Texture> dialogImages;

    public IntroScreen(MainClass game){
        this.game = game;
    }

    @Override
    public void show() {
        game.clearPlayerData();
        batch = new SpriteBatch();
        font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt"));
        font.getData().setScale(1f);
        game.setBgMusic("songs/intro.mp3");
        talkSound = Gdx.audio.newSound(Gdx.files.internal("sounds/text/main.wav"));

        List<String> dialogs = List.of(
            "Это был обычный, солнечный день. Всё было как всегда, ничего нового. Солнце ласково пригревало, воздух был напоен ароматами летних цветов и свежескошенной травы",
            "Вы уже успели поиграть во все любимые игры, перечитать все комиксы, и вам стало ужасно скучно.\n\nРешив развеяться, вы отправились погулять на опушке леса, что граничил с дачным участком.",
            "Вы неспешно брели среди высоких трав и кустарников, как вдруг что-то привлекло ваше внимание. Прямо посреди опушки, там, где трава расступалась, стояла странная звезда",
            "Она была не похожа ни на одну из тех, что вы видели раньше - не металлическая, не деревянная, а словно сотканная из чистого света. \n\nЛюбопытство пересилило любую осторожность, и вы подошли к ней ближе.",
            "Как только ваши пальцы коснулись мерцающей поверхности звезды, мир вокруг вас словно раскололся.\n\nЯркая вспышка ослепила глаза, звуки стихли, и вы почувствовали,\nкак вас затягивает в невидимую воронку.\nГоловокружение нарастало, затем последовала абсолютная, давящая темнота."
        );

        dialogImages = List.of(
            new Texture(Gdx.files.internal("imgs/intro/intro1.png")),
            new Texture(Gdx.files.internal("imgs/intro/intro2.png")),
            new Texture(Gdx.files.internal("imgs/intro/intro3.png")),
            new Texture(Gdx.files.internal("imgs/intro/intro4.png"))
        );

        textManager = new TextManager(dialogs, 0.05f, talkSound, font);
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
            game.setScreen(new StartIslandScreen(game));
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
        talkSound.dispose();
        for (Texture t : dialogImages) {
            t.dispose();
        }
    }

    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
}
