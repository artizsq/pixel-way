package com.pixelway.gameScreens.minigames;

import com.badlogic.gdx.Game;
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
import com.pixelway.gameScreens.StartIslandScreen;
import com.pixelway.models.Player;
import com.pixelway.windows.AlertWindow;

public class FishCatchGame implements Screen {
    private final MainClass game;
    private Stage stage;
    private Texture fishTexture, backTexture;
    private ImageButton backButton;
    private BitmapFont gameOverFont;
    private BitmapFont font;
    private SpriteBatch batch;
    private Array<Image> fishList;
    private int fishCaught = 0;
    private float timer = 30f;
    private Player player;
    private boolean isGameOver = false;
    private Image darkOverlay;
    private TextButton returnButton;
    private GlyphLayout layout;
    private int alertCount = 0;


    public FishCatchGame(MainClass game, Player player) {
        this.game = game;
        this.player = player;
    }

    @Override
    public void show() {
        System.out.println(game.getPlayerData().x + " " + game.getPlayerData().y);
        batch = new SpriteBatch();
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        layout = new GlyphLayout();


        fishTexture = new Texture("texture/fish.png");
        backTexture = new Texture("btns/back.png");
        gameOverFont = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        gameOverFont.getData().scale(2f);

        font = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        font.getData().setScale(1.5f);


        fishList = new Array<>();

        // Кнопка назад
        TextureRegionDrawable backDrawable = new TextureRegionDrawable(new TextureRegion(backTexture));
        backDrawable.setMinSize(128, 128);
        backButton = new ImageButton(backDrawable);
        backButton.setPosition(10, Gdx.graphics.getHeight() - backButton.getHeight() - 10);
        backButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(fishCaught > 0){
                    if(alertCount == 1){
                        game.setScreen(new StartIslandScreen(game, player, game.getPlayerData(), false));
                        fishCaught = 0;
                    } else {
                        new AlertWindow(stage, "Нажмите еще раз для выхода!\nВы потеряте весь прогресс");
                        alertCount++;
                    }
                } else {
                    game.setScreen(new StartIslandScreen(game, player, game.getPlayerData(), false));
                    fishCaught = 0;
                }
            }
        });
        stage.addActor(backButton);
    }

    private void spawnFish() {
        if (isGameOver) return;

        Image fish = new Image(fishTexture);
        fish.setSize(fishTexture.getWidth() * 1.5f, fishTexture.getHeight() * 1.5f);
        float x = MathUtils.random(0, Gdx.graphics.getWidth() - fish.getWidth());
        float y = MathUtils.random(0, Gdx.graphics.getHeight() - fish.getHeight() - 50);
        fish.setPosition(x, y);

        fish.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!isGameOver) {
                    fishCaught++;
                    fish.remove();
                    fishList.removeValue(fish, true);
                }
            }
        });

        stage.addActor(fish);
        fishList.add(fish);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.2f, 0.6f, 1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!isGameOver) {
            timer -= delta;

            if (timer <= 0) {
                timer = 0;
                isGameOver = true;

                for (Image fish : fishList) {
                    fish.remove();
                }
                fishList.clear();

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
                        game.setScreen(new StartIslandScreen(game, player, game.getPlayerData(), false));
                    }
                });
                stage.addActor(returnButton);
                returnButton.toFront();
                backButton.toFront();
            }

            if (MathUtils.randomBoolean(delta * 0.5f)) {
                spawnFish();
            }
        }

        stage.act(delta);
        stage.draw();

        batch.begin();

        String timeText = (int) timer + " сек.";
        font.draw(batch, timeText, Gdx.graphics.getWidth() - 250, Gdx.graphics.getHeight() - 50);

        String scoreText = "Счёт: " + fishCaught;
        font.draw(batch, scoreText, (Gdx.graphics.getWidth() - 200) / 2f, Gdx.graphics.getHeight() - 50);



        if (isGameOver) {
            game.getPlayerData().fishCount = fishCaught;
            String gameOverMsg = "ИГРА ОКОНЧЕНА!\nСЧЕТ: " + fishCaught;

            layout.setText(gameOverFont, gameOverMsg);

            float x = (Gdx.graphics.getWidth() - layout.width) / 2f;
            float y = (Gdx.graphics.getHeight() + layout.height) / 2f;

            gameOverFont.draw(batch, layout, x, y);
        }



        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    private static TextureRegionDrawable createOverlayDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.6f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {
        stage.dispose();
        fishTexture.dispose();
        backTexture.dispose();
        font.dispose();
        batch.dispose();
        if (darkOverlay != null && darkOverlay.getDrawable() instanceof TextureRegionDrawable) {
            TextureRegion tex = ((TextureRegionDrawable) darkOverlay.getDrawable()).getRegion();
            if (tex.getTexture() != null) tex.getTexture().dispose();
        }
    }
}
