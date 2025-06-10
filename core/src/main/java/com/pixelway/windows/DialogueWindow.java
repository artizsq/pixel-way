package com.pixelway.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.pixelway.MainClass;
import com.pixelway.models.dialogs.DialogData;
import com.pixelway.utils.VirtualJoystick;

public class DialogueWindow extends Window {
    private final Label nameLabel;
    private Label textLabel;
    private final Image npcImage;
    private final Stage stage;
    private DialogData currentDialogData;

    private final Table optionsTable;
    private final Image overlay;
    private final MainClass game;

    private final TextButton optionButton1;
    private TextButton optionButton2;

    private BitmapFont defaultFont;
    private BitmapFont smallerFont;
    private Label.LabelStyle defaultLabelStyle;

    private static final float DEFAULT_FONT_SCALE = 0.6f;
    private static final float SMALLER_FONT_SCALE = 0.4f;
    private static final float NPC_IMAGE_SIZE = 128f;




    public DialogueWindow(Stage stage, MainClass game, DialogData dialogData) {
        super("", createWindowStyle());
        this.stage = stage;
        this.game = game;
        this.currentDialogData = dialogData;
        VirtualJoystick.inputBlocked = true;

        setDebug(false);

        overlay = new Image();
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.addListener(new ClickListener() {});

        defaultFont = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        defaultFont.getData().scale(DEFAULT_FONT_SCALE);
        defaultLabelStyle = new Label.LabelStyle(defaultFont, Color.WHITE);

        smallerFont = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        smallerFont.getData().scale(SMALLER_FONT_SCALE);

        BitmapFont nameFont = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        nameFont.getData().scale(0.2f);
        Label.LabelStyle nameStyle = new Label.LabelStyle(nameFont, Color.LIGHT_GRAY);

        nameLabel = new Label(dialogData.name, nameStyle);
        textLabel = new Label(dialogData.text, defaultLabelStyle);
        textLabel.setWrap(true);
        textLabel.setAlignment(Align.topLeft);

        npcImage = new Image(new TextureRegionDrawable(new Texture(Gdx.files.internal("imgs/npc/" + dialogData.imagePath))));
        npcImage.setSize(NPC_IMAGE_SIZE, NPC_IMAGE_SIZE);

        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = defaultFont;
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = new TextureRegionDrawable(new Texture(Gdx.files.internal("btns/dial_btn.png")));

        optionButton1 = new TextButton(dialogData.option1, buttonStyle);
        if (!(dialogData.option2 == null)){
            optionButton2 = new TextButton(dialogData.option2, buttonStyle);
            optionButton2.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    closeDialog();
                }
            });
        }

        optionButton1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if(currentDialogData.dialogAction != null){
                    currentDialogData.dialogAction.execute();
                }


                if (currentDialogData.newDialogData != null) {
                    currentDialogData = currentDialogData.newDialogData;
                    updateDialog();
                } else {
                    closeDialog();
                }
            }
        });



        optionsTable = new Table();
        layoutButtons();

        VerticalGroup mainGroup = new VerticalGroup();
        mainGroup.align(Align.topLeft);
        mainGroup.pad(20);
        mainGroup.space(10);
        mainGroup.grow();
        mainGroup.fill();

        mainGroup.addActor(nameLabel);
        mainGroup.addActor(textLabel);

        Table wrapper = new Table();
        wrapper.setFillParent(true);
        wrapper.top().left();
        wrapper.add(mainGroup).expand().fill().row();
        wrapper.add(optionsTable).padBottom(10).center();

        this.clear();
        this.add(wrapper).expand().fill();


        pack();
        float minHeight = 300;
        float desiredHeight = getPrefHeight();
        float newHeight = Math.max(minHeight, desiredHeight);
        setSize(Math.max(1100, getWidth()), newHeight);

        float bottomY = 100;
        setPosition((Gdx.graphics.getWidth() - getWidth()) / 2f, bottomY);
        moveBy(0, newHeight - minHeight);



        float npcX = getX() - npcImage.getWidth() - 20;
        float npcY = getY() + getHeight() - npcImage.getHeight() + 64;
        npcImage.setPosition(npcX, npcY);

        stage.addActor(overlay);
        stage.addActor(this);
        stage.addActor(npcImage);
    }

    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));

        Texture ninePatchTexture = new Texture(Gdx.files.internal("imgs/dialog.png"));
        NinePatch ninePatch = new NinePatch(ninePatchTexture, 0, 0, 0, 0);
        NinePatchDrawable background = new NinePatchDrawable(ninePatch);

        WindowStyle style = new WindowStyle();
        style.titleFont = font;
        style.background = background;
        return style;
    }


    private void layoutButtons() {
        optionsTable.clear();
        optionsTable.add(optionButton1).width(450).height(80).padRight(20);
        optionsTable.add(optionButton2).width(450).height(80);
    }

    private void updateDialog() {
        nameLabel.setText(currentDialogData.name);

        textLabel.setText(currentDialogData.text);
        optionButton1.setText(currentDialogData.option1);
        if (currentDialogData.option2 == null){
            optionButton2.remove();
        } else {
            optionButton2.setText(currentDialogData.option2);
        }

    }


    private void closeDialog() {
        this.remove();
        VirtualJoystick.inputBlocked = false;
        npcImage.remove();
        overlay.remove();
    }


}
