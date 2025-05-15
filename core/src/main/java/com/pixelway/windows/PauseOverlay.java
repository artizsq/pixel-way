package com.pixelway.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pixelway.MainClass;
import com.pixelway.database.DatabaseHelper;
import com.pixelway.database.PlayerData;
import com.pixelway.screens.MainMenuScreen;
import com.pixelway.utils.VirtualJoystick;

public class PauseOverlay extends Window {

    private final Image darkOverlay;
    private final Stage stage; // Добавлено поле для доступа к Stage
    private MainClass game;


    public PauseOverlay(Stage stage, MainClass game) {
        super("", createWindowStyle());
        this.game = game;
        this.stage = stage; // Сохраняем ссылку на Stage

        VirtualJoystick.inputBlocked = true;
        // Размер и позиция окна
        this.setSize(600, 400);
        this.setPosition(
            (Gdx.graphics.getWidth() - this.getWidth()) / 2f,
            (Gdx.graphics.getHeight() - this.getHeight()) / 2f
        );
        this.setMovable(false);

        TextureRegionDrawable overlayDrawable = createOverlayDrawable();
        darkOverlay = new Image(overlayDrawable);
        darkOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        darkOverlay.addListener(new ClickListener() {});

        // Шрифт
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));

        // Сообщение
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.font.getData().scale(0.5f);
        labelStyle.fontColor = Color.BLACK;

        Label message = new Label("", labelStyle);

        this.add(message).padTop(50f).row();


        TextButton contButton = createContinueButton(font);
        TextButton menuButton = createMenuButton(font);
        TextButton exitButton = createExitButton(font);

        float buttonWidth = 240;
        float buttonHeight = 100;

        contButton.setSize(buttonWidth, buttonHeight);
        menuButton.setSize(buttonWidth, buttonHeight);
        exitButton.setSize(buttonWidth, buttonHeight);


        this.add(contButton).width(buttonWidth).height(buttonHeight).padBottom(20f).row();
        this.add(menuButton).width(buttonWidth).height(buttonHeight).padBottom(20f).row();
        this.add(exitButton).width(buttonWidth).height(buttonHeight).padBottom(90f);



        stage.addActor(darkOverlay);
        stage.addActor(this);
    }

    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        Texture bgTex = new Texture(Gdx.files.internal("imgs/alert.png"));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(bgTex));

        WindowStyle style = new WindowStyle();
        style.titleFont = font;
        style.background = background;
        return style;
    }

    private static TextureRegionDrawable createOverlayDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.6f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
    }

    private TextButton createContinueButton(BitmapFont font) {
        return createButton("Продолжить", font, () -> {
            game.getPlayerData().money += 100;
            Gdx.app.log("INFO", "User money: " + game.getPlayerData().money);
            darkOverlay.remove();
            PauseOverlay.this.remove();
            VirtualJoystick.inputBlocked = false;
        });
    }

    private TextButton createMenuButton(BitmapFont font) {
        return createButton("Выйти в меню", font, () -> {
            darkOverlay.remove();
            PauseOverlay.this.remove();
            VirtualJoystick.inputBlocked = false;

            game.getScreen().dispose();
            game.setBgMusic("songs/main.mp3");
            game.setScreen(new MainMenuScreen(game));
        });
    }

    private TextButton createExitButton(BitmapFont font) {
        return createButton("Выйти из игры", font, () -> {
            Gdx.app.exit();
        });
    }

    private TextButton createButton(String text, BitmapFont font, Runnable action) {
        Texture okTex = new Texture(Gdx.files.internal("btns/norm.png"));
        TextureRegionDrawable okDrawable = new TextureRegionDrawable(new TextureRegion(okTex));
        okDrawable.setMinSize(240, 100);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = okDrawable;
        buttonStyle.down = okDrawable;

        TextButton button = new TextButton(text, buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });
        return button;
    }
}
