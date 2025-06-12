package com.pixelway.screens.minigames;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.pixelway.MainClass;
import com.pixelway.models.characters.Player;
import com.pixelway.screens.location.StartIslandScreen;
import com.pixelway.windows.AlertWindow;

public class BerryCatchGame implements Screen {

    private final MainClass game;
    private final Player player;

    private Stage stage;
    private SpriteBatch batch;
    private Texture berryTexture, basketTexture, backTexture, badBerryTexture;
    private ImageButton backButton;
    private Image basket;
    private Array<Image> berries;

    private float timer = 30f;
    private int berriesCaught = 0;
    private boolean isGameOver = false;
    private BitmapFont font, gameOverFont;
    private GlyphLayout layout;
    private Image darkOverlay;
    private Texture backgroundTexture;

    private TextButton returnButton;
    private int alertCount = 0;

    public BerryCatchGame(MainClass game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        layout = new GlyphLayout();

        backgroundTexture = new Texture("imgs/berry/back.png");
        berryTexture = new Texture("imgs/berry/berry.png");
        basketTexture = new Texture("imgs/berry/basket.png");
        backTexture = new Texture("btns/back.png");
        badBerryTexture = new Texture("imgs/berry/badBerry.png");


        font = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        font.getData().setScale(1.5f);

        gameOverFont = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        gameOverFont.getData().setScale(2f);

        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backTexture));
        backDrawable.setMinSize(128, 128);
        backButton = new ImageButton(backDrawable);
        backButton.setPosition(10, Gdx.graphics.getHeight() - 138);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (berriesCaught > 0) {
                    if (alertCount == 1) {
                        dispose();
                        berriesCaught = 0;
                        game.setScreen(new StartIslandScreen(game, player, game.getPlayerData(), false));

                    } else {
                        new AlertWindow(stage, "Нажмите еще раз для выхода!\nВы потеряете весь прогресс");
                        alertCount++;
                    }
                } else {
                    dispose();
                    berriesCaught = 0;
                    game.setScreen(new StartIslandScreen(game, player, game.getPlayerData(), false));
                }
            }
        });
        stage.addActor(backButton);

        basket = new Image(basketTexture);
        basket.setSize(200, 120);
        basket.setPosition((Gdx.graphics.getWidth() - basket.getWidth()) / 2f, 20);
        stage.addActor(basket);

        berries = new Array<>();
    }

    private void spawnBerry() {
        if (isGameOver) return;
        Image berry = new Image(berryTexture);
        berry.setSize(64, 64);
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - berry.getWidth());
        berry.setPosition(x, Gdx.graphics.getHeight());
        berry.setUserObject("good");
        stage.addActor(berry);
        berries.add(berry);
    }

    private void spawnBadBerry() {
        if (isGameOver) return;
        Image badBerry = new Image(badBerryTexture);
        badBerry.setSize(64, 64);
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - badBerry.getWidth());
        badBerry.setPosition(x, Gdx.graphics.getHeight());
        badBerry.setUserObject("bad");
        stage.addActor(badBerry);
        berries.add(badBerry);
    }



    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.45f, 0.45f, 0.45f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (!isGameOver && (Gdx.input.isTouched() || Gdx.input.isButtonPressed(0))) {
            float inputX = Gdx.input.getX();
            float basketX = inputX - basket.getWidth() / 2f;
            basket.setX(MathUtils.clamp(basketX, 0, Gdx.graphics.getWidth() - basket.getWidth()));
        }

        if (!isGameOver) {
            timer -= delta;

            if (timer <= 0) {
                timer = 0;
                isGameOver = true;

                for (Image berry : berries) {
                    berry.remove();
                }
                berries.clear();

                darkOverlay = new Image(createOverlayDrawable());
                darkOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                stage.addActor(darkOverlay);
                darkOverlay.toFront();

                TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
                style.font = font;
                returnButton = new TextButton("-> Вернуться <-", style);
                returnButton.setSize(300, 100);
                returnButton.setPosition(
                    (Gdx.graphics.getWidth() - returnButton.getWidth()) / 2f,
                    Gdx.graphics.getHeight() / 2f - 250);
                returnButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        dispose();
                        game.setScreen(new StartIslandScreen(game, player, game.getPlayerData(), false));
                    }
                });
                stage.addActor(returnButton);
                returnButton.toFront();
                backButton.toFront();
            }

            if (MathUtils.randomBoolean(delta * 1f)) {
                spawnBerry();
            }

            if (MathUtils.randomBoolean(delta * 0.3f)) {
                spawnBadBerry();
            }

            for (int i = berries.size - 1; i >= 0; i--) {
                Image berry = berries.get(i);
                berry.setY(berry.getY() - 300 * delta);

                Rectangle berryRect = new Rectangle(berry.getX(), berry.getY(), berry.getWidth(), berry.getHeight());
                Rectangle basketRect = new Rectangle(basket.getX(), basket.getY(), basket.getWidth(), basket.getHeight());

                if (berryRect.overlaps(basketRect)) {
                    Object type = berry.getUserObject();
                    if ("bad".equals(type)) {
                        berriesCaught = Math.max(0, berriesCaught - 3);
                    } else {
                        berriesCaught++;
                    }
                    berry.remove();
                    berries.removeIndex(i);
                } else if (berry.getY() + berry.getHeight() < 0) {
                    berry.remove();
                    berries.removeIndex(i);
                }
            }
        }

        stage.act(delta);
        stage.draw();

        batch.begin();

        String timeText = (int) timer + " сек.";
        font.draw(batch, timeText, Gdx.graphics.getWidth() - 250, Gdx.graphics.getHeight() - 50);

        String scoreText = "Счёт: " + berriesCaught;
        font.draw(batch, scoreText, (Gdx.graphics.getWidth() - 200) / 2f, Gdx.graphics.getHeight() - 50);

        if (isGameOver) {
            game.getPlayerData().berryCount = berriesCaught;
            String gameOverMsg = "ИГРА ОКОНЧЕНА!\nСЧЕТ: " + berriesCaught;

            layout.setText(gameOverFont, gameOverMsg);
            float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float y = (Gdx.graphics.getHeight() + layout.height) / 2f;
            gameOverFont.draw(batch, layout, x, y);
        }

        batch.end();
    }


    private static TextureRegionDrawable createOverlayDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.6f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override public void resize(int width, int height) { stage.getViewport().update(width, height, true); }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        stage.dispose();
        berryTexture.dispose();
        basketTexture.dispose();
        backTexture.dispose();
        font.dispose();
        gameOverFont.dispose();
        if (darkOverlay != null && darkOverlay.getDrawable() instanceof TextureRegionDrawable) {
            TextureRegion tex = ((TextureRegionDrawable) darkOverlay.getDrawable()).getRegion();
            if (tex.getTexture() != null) tex.getTexture().dispose();
        }

        badBerryTexture.dispose();
        batch.dispose();
        backgroundTexture.dispose();

    }
}
