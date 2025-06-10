package com.pixelway.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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
import com.pixelway.utils.VirtualJoystick;

public class SuperDialogue extends Window {
    private final Image darkOverlay;
    private final Label messageLabel;

    private static final float MIN_WIDTH = 800f;
    private static final float MIN_HEIGHT = 300f;
    private static final float PADDING = 50f;
    private static final float BUTTON_HEIGHT = 90f;
    private static final float BUTTON_WIDTH = 300;
    private static final float BUTTON_PAD_BOTTOM = -2f;
    private static final float MESSAGE_PAD_TOP = -50f;
    private static final float WINDOW_BOTTOM_OFFSET = 100f;

    public SuperDialogue(Stage stage, String text) {
        super("", createWindowStyle());
        VirtualJoystick.inputBlocked = true;

        setModal(true);
        setMovable(false);

        darkOverlay = new Image();
        darkOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        darkOverlay.addListener(new ClickListener() {}); // блокирует клики

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        font.getData().scale(0.6f);

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.fontColor = Color.WHITE;

        messageLabel = new Label(text, labelStyle);
        messageLabel.setWrap(true);
        messageLabel.setAlignment(1);

        this.add(messageLabel).width(MIN_WIDTH - 2 * PADDING).padTop(MESSAGE_PAD_TOP).padLeft(PADDING).padRight(PADDING).growY();
        this.row();

        TextButton continueButton = createContinueButton(font);
        this.add(continueButton)
            .height(BUTTON_HEIGHT)
            .width(BUTTON_WIDTH)
            .bottom()
            .padBottom(BUTTON_PAD_BOTTOM);

        pack();

        float calculatedWidth = Math.max(MIN_WIDTH, messageLabel.getWidth() + 2 * PADDING);
        float calculatedHeight = Math.max(MIN_HEIGHT, messageLabel.getHeight() + MESSAGE_PAD_TOP + BUTTON_HEIGHT + BUTTON_PAD_BOTTOM + getPadTop() + getPadBottom());
        setSize(calculatedWidth, calculatedHeight);

        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2f,
            WINDOW_BOTTOM_OFFSET
        );

        stage.addActor(darkOverlay);
        stage.addActor(this);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        float preferredHeight = messageLabel.getPrefHeight();
        float newHeight = Math.max(MIN_HEIGHT, preferredHeight + MESSAGE_PAD_TOP + BUTTON_HEIGHT + BUTTON_PAD_BOTTOM + getPadTop() + getPadBottom());

        if (getHeight() != newHeight) {
            setHeight(newHeight);
            setPosition((Gdx.graphics.getWidth() - getWidth()) / 2f, WINDOW_BOTTOM_OFFSET);
        }

        float preferredWidth = messageLabel.getPrefWidth();
        float newWidth = Math.max(MIN_WIDTH, preferredWidth + 2 * PADDING);
        if (getWidth() != newWidth) {
            setWidth(newWidth);
            setPosition((Gdx.graphics.getWidth() - getWidth()) / 2f, getY());
        }
    }

    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        Texture bgTex = new Texture(Gdx.files.internal("imgs/dialog.png"));
        TextureRegionDrawable background = new TextureRegionDrawable(new TextureRegion(bgTex));

        WindowStyle style = new WindowStyle();
        style.titleFont = font;
        style.background = background;
        return style;
    }

    private TextButton createContinueButton(BitmapFont font) {
        Texture btnTex = new Texture(Gdx.files.internal("btns/dial_btn.png"));
        TextureRegionDrawable btnDrawable = new TextureRegionDrawable(new TextureRegion(btnTex));
        btnDrawable.setMinSize(BUTTON_WIDTH, BUTTON_HEIGHT);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.WHITE;
        style.up = btnDrawable;
        style.down = btnDrawable;

        TextButton button = new TextButton("Продолжить", style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                darkOverlay.remove();
                SuperDialogue.this.remove();
                VirtualJoystick.inputBlocked = false;
            }
        });
        return button;
    }
}
