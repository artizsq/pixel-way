package com.pixelway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.pixelway.database.DatabaseHelper;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.pixelway.utils.SoundController;
import com.pixelway.windows.AlertWindow;


public class SettingsScreen implements Screen {
    private final MainClass game;
    private Stage stage;

    private Music music;
    private PlayerData playerData;
    private TextField nameTextField;

    public SettingsScreen(MainClass game) {
        this.game = game;
        this.music = game.getBgMusic();
        this.playerData = game.getPlayerData();

        Viewport viewport = new ExtendViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        createButton(Gdx.graphics.getWidth() - 580, 350, 370, 170, () -> {
            try {
                if (playerData.playerName != null && playerData.playerName.length() > 10) {
                    new AlertWindow(stage, "Имя не может быть длиннее 10 символов!");
                    return;
                }
                game.saveData();
                new AlertWindow(stage, "Настройки сохранены!");

            } catch (Exception e) {
                new AlertWindow(stage, "Ой-ой, произошла ошибка!\nЖаль тебе никто не поможет.");
            }

        }, "Сохранить");
        createButton(Gdx.graphics.getWidth() - 580, 100, 370, 170, () -> {
            SettingsScreen.this.dispose();
            game.setScreen(new MainMenuScreen(game));
        }, "Назад");

        createTextTexture(Gdx.graphics.getWidth() - 650, 680, 500, 200);
        createLabel(Gdx.graphics.getWidth() - 620, 900, 460, 60, "ИМЯ ИГРОКА");
        nameTextField = createTextField(Gdx.graphics.getWidth() - 632, 700, 440, 150, playerData.playerName); // Сохраняем ссылку на TextField


        createLabel(70, 900, 640, 60, "ГРОМКОСТЬ МУЗЫКИ");
        createVolumeSliderWithLabel(100, 750, 400, 100, music, playerData.musicVolume);

        createLabel(70, 600, 640, 60, "ГРОМКОСТЬ ЗВУКОВ");
        createSoundSliderWithLabel(100, 400, 400, 100, playerData.soundVolume);

    }


    private void createButton(float x, float y, float width, float height, Runnable action, String text) {
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(new Texture("btns/sett.png"));
        textureRegionDrawable.setMinSize(width, height);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt"));
        font.getData().scale(0.5f);
        textButtonStyle.over = textureRegionDrawable;
        textButtonStyle.up = textureRegionDrawable;
        textButtonStyle.down = textureRegionDrawable;
        textButtonStyle.font = font;
        textButtonStyle.fontColor = Color.BLACK;

        TextButton button = new TextButton(text, textButtonStyle);
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

    private void createTextTexture(float x, float y, float width, float height) {
        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("btns/norm.png")));
        Image image = new Image(textureRegionDrawable);
        image.setBounds(x, y, width, height);
        stage.addActor(image);
    }

    private void createLabel(float x, float y, float width, float height, String text) {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/mono.fnt"));
        font.getData().setScale(2f);
        LabelStyle labelStyle = new LabelStyle(font, Color.BLACK);

        Label label = new Label(text, labelStyle);
        label.setBounds(x, y, width, height);
        stage.addActor(label);
    }

    private TextField createTextField(float x, float y, float width, float height, String text) {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        font.getData().setScale(4f);


        Pixmap pixCursor = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixCursor.setColor(Color.BLACK);
        pixCursor.fill();
        TextureRegionDrawable cursorDrawable = new TextureRegionDrawable(
            new TextureRegion(new Texture(pixCursor))
        );
        pixCursor.dispose();

        Pixmap pixSelect = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixSelect.setColor(0f, 0.5f, 1f, 0.3f);
        pixSelect.fill();
        TextureRegionDrawable selectionDrawable = new TextureRegionDrawable(
            new TextureRegion(new Texture(pixSelect))
        );
        pixSelect.dispose();

        TextField.TextFieldStyle style = new TextField.TextFieldStyle(
            font,
            Color.BLACK,
            cursorDrawable,
            selectionDrawable,
            null
        );


        TextField textField = new TextField(text, style);
        textField.setBounds(x, y, width, height);
        stage.addActor(textField);

        textField.setTextFieldListener((field, c) -> {
            if (field.getText().length() <= 7) {
                playerData.playerName = field.getText();
            } else {
                field.setText(playerData.playerName);
                new AlertWindow(stage, "Имя не может быть\nдлиннее 7 символов!");
            }
        });
        return textField;
    }

    private void createVolumeSliderWithLabel(float x, float y, float width, float height, Music music, float initialValue) {
        Skin skin = new Skin();

        Texture backgroundTexture = new Texture(Gdx.files.internal("texture/slider/back.png"));
        Texture knobTexture = new Texture(Gdx.files.internal("texture/slider/knob.png"));
        Texture knobBeforeTexture = new Texture(Gdx.files.internal("texture/slider/knobBefore.png"));

        skin.add("background", new TextureRegion(backgroundTexture));
        skin.add("knob", new TextureRegion(knobTexture));
        skin.add("knobBefore", new TextureRegion(knobBeforeTexture));

        Drawable backgroundDrawable = skin.getDrawable("background");
        Drawable knobDrawable = skin.getDrawable("knob");
        Drawable knobBeforeDrawable = skin.getDrawable("knobBefore");

        Slider.SliderStyle style = new Slider.SliderStyle(backgroundDrawable, knobDrawable);
        style.knobBefore = knobBeforeDrawable;

        final Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, style);
        volumeSlider.setBounds(x, y, width, height);
        volumeSlider.setValue(initialValue);

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        font.getData().scale(2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        final Label volumeLabel = new Label((int) (initialValue * 100) + "%", labelStyle);
        volumeLabel.setPosition(x + width + 30, y + height / 4f); // справа от слайдера
        createTextTexture(x + width + 20, y + height / 4f, 163, 99);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                music.setVolume(volume);
                playerData.musicVolume = volume;
                volumeLabel.setText((int) (volume * 100) + "%");
            }
        });

        stage.addActor(volumeSlider);
        stage.addActor(volumeLabel);

    }

    private void createSoundSliderWithLabel(float x, float y, float width, float height, float initialValue) {
        Skin skin = new Skin();

        Texture backgroundTexture = new Texture(Gdx.files.internal("texture/slider/back.png"));
        Texture knobTexture = new Texture(Gdx.files.internal("texture/slider/knob.png"));
        Texture knobBeforeTexture = new Texture(Gdx.files.internal("texture/slider/knobBefore.png"));

        skin.add("background", new TextureRegion(backgroundTexture));
        skin.add("knob", new TextureRegion(knobTexture));
        skin.add("knobBefore", new TextureRegion(knobBeforeTexture));

        Drawable backgroundDrawable = skin.getDrawable("background");
        Drawable knobDrawable = skin.getDrawable("knob");
        Drawable knobBeforeDrawable = skin.getDrawable("knobBefore");

        Slider.SliderStyle style = new Slider.SliderStyle(backgroundDrawable, knobDrawable);
        style.knobBefore = knobBeforeDrawable;

        final Slider volumeSlider = new Slider(0f, 1f, 0.01f, false, style);
        volumeSlider.setBounds(x, y, width, height);
        volumeSlider.setValue(initialValue);

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        font.getData().scale(2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);
        final Label volumeLabel = new Label((int) (initialValue * 100) + "%", labelStyle);
        volumeLabel.setPosition(x + width + 30, y + height / 4f); // справа от слайдера
        createTextTexture(x + width + 20, y + height / 4f, 163, 99);
        volumeSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                float volume = volumeSlider.getValue();
                playerData.soundVolume = volume;
                volumeLabel.setText((int) (volume * 100) + "%");
            }
        });

        stage.addActor(volumeSlider);
        stage.addActor(volumeLabel);

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.56f, 0.34f, 0.23f, 1);
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
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        Gdx.gl.glClearColor(0f, 0f, 0f, 0f);
        stage.dispose();

    }
}
