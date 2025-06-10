package com.pixelway.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.pixelway.utils.VirtualJoystick;


public class AlertWindow extends Window {

    private final Image darkOverlay;

    public AlertWindow(Stage stage, String messageText) {
        super("", createWindowStyle());
        VirtualJoystick.inputBlocked = true;


        setModal(true);
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

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));


        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        labelStyle.font.getData().scale(0.5f);
        labelStyle.fontColor = Color.BLACK;
        Label message = new Label(messageText, labelStyle);

        this.add(message).padTop(100f);
        this.row();

        TextButton okButton = createOkButton(font);
        okButton.setSize(120, 50);


        this.row();
        this.add(okButton)
            .expandY()
            .bottom()
            .padBottom(50f);


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

    private TextButton createOkButton(BitmapFont font) {
        Texture okTex = new Texture(Gdx.files.internal("btns/norm.png"));
        TextureRegionDrawable okDrawable = new TextureRegionDrawable(new TextureRegion(okTex));
        okDrawable.setMinSize(240, 100);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = okDrawable;
        buttonStyle.down = okDrawable;

        TextButton button = new TextButton("OK", buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                darkOverlay.remove();
                AlertWindow.this.remove();
                VirtualJoystick.inputBlocked = false;
            }
        });
        return button;
    }


}
