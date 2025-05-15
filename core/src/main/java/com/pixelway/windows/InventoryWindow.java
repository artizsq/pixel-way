package com.pixelway.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
import com.pixelway.utils.VirtualJoystick;

public class InventoryWindow extends Window {
    private final Label nameLabel;
    private final Label descLabel;
    private final Label effectLabel;
    private final ImageButton exitButton;
    private final Image itemImage, moneySign;
    private final Label HP, STR, SHIELD, userNameLabel, moneyLabel;
    private final Label HPValue, STRValue, SHIELDValue;
    private TextButton useButton, deleteButton;
    private Table leftTable; // Сделаем leftTable полем класса, чтобы можно было перестраивать
    private PlayerData.InventorySlot selectedSlot; // Храним выбранный слот для удаления/экипировки
    private MainClass game;
    private Stage stage;

    public InventoryWindow(Stage stage, MainClass game) {
        super("", createWindowStyle());
        this.stage = stage;
        this.game = game;
        VirtualJoystick.inputBlocked = true;

        setModal(true);
        setMovable(false);
        setSize(904 + 512, 904);
        setPosition((Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2);

        leftTable = new Table();
        Texture invTex = new Texture(Gdx.files.internal("imgs/inventory.png"));
        TextureRegionDrawable invDrawable = new TextureRegionDrawable(new TextureRegion(invTex));
        leftTable.setBackground(invDrawable);
        leftTable.setWidth(904);

        TextureRegionDrawable exitButtonTexture = new TextureRegionDrawable(new Texture(Gdx.files.internal("btns/exit.png")));
        exitButtonTexture.setMinSize(75, 75);
        exitButton = new ImageButton(exitButtonTexture);
        exitButton.setPosition(8, getHeight() - 84);
        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                VirtualJoystick.inputBlocked = false;
                InventoryWindow.this.remove();
            }
        });

        leftTable.addActor(exitButton);

        BitmapFont userInfoFont = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        userInfoFont.getData().scale(1f);

        TextureRegionDrawable moneySignDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("imgs/coin.png")));
        moneySign = new Image(moneySignDrawable);
        moneySign.setBounds(490, 665, 48, 48);


        userNameLabel = new Label(game.getPlayerData().playerName, new Label.LabelStyle(userInfoFont, Color.BLACK));

        moneyLabel = new Label("" + game.getPlayerData().money, new Label.LabelStyle(userInfoFont, Color.YELLOW));
        userNameLabel.setPosition(584, 740);
        moneyLabel.setPosition(550, 660);

        leftTable.addActor(userNameLabel);
        leftTable.addActor(moneyLabel);
        leftTable.addActor(moneySign);

        BitmapFont statsFont = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        statsFont.getData().scale(1f);
        Label.LabelStyle leftLabelStyle = new Label.LabelStyle(statsFont, Color.BLACK);

        HP = new Label("HP", leftLabelStyle);
        STR = new Label("Power", leftLabelStyle);
        SHIELD = new Label("Shield", leftLabelStyle);

        HPValue = new Label(game.getPlayerData().hp + "", leftLabelStyle);
        STRValue = new Label(game.getPlayerData().strength + "", leftLabelStyle);
        SHIELDValue = new Label(game.getPlayerData().shield + "", leftLabelStyle);

        HPValue.setPosition(720, 450);
        STRValue.setPosition(720, 320);
        SHIELDValue.setPosition(720, 190);

        HP.setPosition(550, 450);
        STR.setPosition(510, 320);
        SHIELD.setPosition(510, 190);

        leftTable.addActor(HPValue);
        leftTable.addActor(STRValue);
        leftTable.addActor(SHIELDValue);
        leftTable.addActor(HP);
        leftTable.addActor(STR);
        leftTable.addActor(SHIELD);

        populateInventoryTable(); // Заполняем таблицу инвентаря при создании окна

        Table rightTable = new Table();
        Texture itemInfoTex = new Texture(Gdx.files.internal("imgs/itemInfo.png"));
        TextureRegionDrawable itemInfoDrawable = new TextureRegionDrawable(new TextureRegion(itemInfoTex));
        rightTable.setBackground(itemInfoDrawable);
        rightTable.setWidth(512);

        itemImage = new Image(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("null.png")))));
        itemImage.setSize(192, 192);
        itemImage.setPosition(920, getHeight() - 213);

        BitmapFont effectfont = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        Label.LabelStyle descStyle = new Label.LabelStyle(effectfont, Color.BLACK);

        effectLabel = new Label("", descStyle);
        effectLabel.setPosition(1155, getHeight() - 70);

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        font.getData().scale(1f);
        Label.LabelStyle infoStyle = new Label.LabelStyle(font, Color.BLACK);

        nameLabel = new Label("", infoStyle);
        nameLabel.setPosition(930, getHeight() - 270);

        descLabel = new Label("Выберите предмет из инвентаря\nчтобы посмотреть его данные.", descStyle);
        descLabel.setPosition(930, getHeight() - 400);

        BitmapFont buttonsFont = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt"));
        TextureRegionDrawable buttonsTextureRegion = new TextureRegionDrawable(new Texture(Gdx.files.internal("btns/norm.png")));
        buttonsTextureRegion.setMinSize(180, 60);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = buttonsFont;
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = buttonsTextureRegion;
        buttonStyle.down = buttonsTextureRegion;

        deleteButton = new TextButton("Удалить", buttonStyle);
        deleteButton.setPosition(930, getHeight() - 500); // Поменяли позицию
        deleteButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedSlot != null) {
                    game.getPlayerData().inventory.remove(selectedSlot);
                    populateInventoryTable(); // Перестраиваем таблицу после удаления
                    // Очищаем правую панель после удаления
                    nameLabel.setText("");
                    descLabel.setText("Выберите предмет из инвентаря\nчтобы посмотреть его данные.");
                    effectLabel.setText("");
                    itemImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("null.png")))));
                    selectedSlot = null; // Сбрасываем выбранный слот
                }
            }
        });

        useButton = new TextButton("Экипировать", buttonStyle);
        useButton.setPosition(1120, getHeight() - 500); // Поменяли позицию
        useButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selectedSlot != null) {
                    switch (selectedSlot.effectType) {
                        case HP:
                            game.getPlayerData().hp += selectedSlot.effectValue;
                            HPValue.setText(game.getPlayerData().hp);
                            break;
                        case POWER:
                            game.getPlayerData().strength += selectedSlot.effectValue;
                            STRValue.setText(game.getPlayerData().strength);
                            break;
                        case SHIELD:
                            game.getPlayerData().shield += selectedSlot.effectValue;
                            SHIELDValue.setText(game.getPlayerData().shield);
                            break;
                    }
                    game.getPlayerData().inventory.remove(selectedSlot); // Удаляем предмет после экипировки
                    populateInventoryTable(); // Перестраиваем таблицу после экипировки
                    // Очищаем правую панель
                    nameLabel.setText("");
                    descLabel.setText("Выберите предмет из инвентаря\nчтобы посмотреть его данные.");
                    effectLabel.setText("");
                    itemImage.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("null.png")))));
                    selectedSlot = null; // Сбрасываем выбранный слот
                }
            }
        });

        rightTable.addActor(deleteButton); // Поменяли порядок добавления
        rightTable.addActor(useButton);    // Поменяли порядок добавления

        SplitPane.SplitPaneStyle splitStyle = new SplitPane.SplitPaneStyle();
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.CLEAR);
        pixmap.fill();
        Texture handleTexture = new Texture(pixmap);
        pixmap.dispose();
        splitStyle.handle = new TextureRegionDrawable(new TextureRegion(handleTexture));

        SplitPane splitPane = new SplitPane(leftTable, rightTable, false, splitStyle);
        splitPane.setSplitAmount(904f / (904f + 512f)); // Устанавливаем пропорцию разделения
        splitPane.setFillParent(true);

        this.addActor(splitPane);
        this.addActor(itemImage);
        this.addActor(deleteButton); // Поменяли порядок добавления
        this.addActor(useButton);    // Поменяли порядок добавления
        this.addActor(nameLabel);
        this.addActor(descLabel);
        this.addActor(effectLabel);

        stage.addActor(this);
    }

    private void populateInventoryTable() {
        leftTable.clearChildren();
        leftTable.addActor(exitButton);
        leftTable.addActor(userNameLabel);
        leftTable.addActor(moneyLabel);
        leftTable.addActor(moneySign);
        leftTable.addActor(HPValue);
        leftTable.addActor(STRValue);
        leftTable.addActor(SHIELDValue);
        leftTable.addActor(HP);
        leftTable.addActor(STR);
        leftTable.addActor(SHIELD);

        PlayerData data = game.getPlayerData();
        int rows = 3;
        int cols = 2;
        int index = 0;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (index < data.inventory.size()) {
                    final PlayerData.InventorySlot slot = data.inventory.get(index);
                    ImageButton itemButton;

                    if (slot != null && slot.imagePath != null) {
                        TextureRegionDrawable itemTextureRegion = new TextureRegionDrawable(new Texture(Gdx.files.internal(slot.imagePath)));
                        itemTextureRegion.setMinSize(192, 192);
                        itemButton = new ImageButton(itemTextureRegion);
                        itemButton.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                selectedSlot = slot;
                                nameLabel.setText(slot.name);
                                descLabel.setText(slot.itemDesc);
                                effectLabel.setText("Прибавляет +" + slot.effectValue + " к \nтекущему " + slot.effectType);
                                TextureRegionDrawable itemTexture = new TextureRegionDrawable(new Texture(Gdx.files.internal(slot.imagePath)));
                                itemTexture.setMinSize(192, 192);
                                itemImage.setDrawable(itemTexture);
                            }
                        });
                        leftTable.add(itemButton).size(192).padBottom(17).padRight(23);
                    } else {
                        Texture emptyTexture = new Texture(Gdx.files.internal("null.png"));
                        TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(new TextureRegion(emptyTexture));
                        textureRegionDrawable.setMinSize(192, 192);
                        itemButton = new ImageButton(textureRegionDrawable);
                        leftTable.add(itemButton).size(192).padBottom(17).padRight(23);
                    }
                    index++;
                } else {
                    // Если слотов инвентаря меньше, чем ячеек таблицы, добавляем пустые кнопки
                    Texture emptyTexture = new Texture(Gdx.files.internal("null.png"));
                    TextureRegionDrawable textureRegionDrawable = new TextureRegionDrawable(new TextureRegion(emptyTexture));
                    textureRegionDrawable.setMinSize(192, 192);
                    ImageButton itemButton = new ImageButton(textureRegionDrawable);
                    leftTable.add(itemButton).size(192).padBottom(17).padRight(23);
                    index++;
                }
            }
            leftTable.padLeft(-425).row(); // <- Переносим вызов row() после завершения внутреннего цикла
        }
    }

    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        Texture bgTex = new Texture(Gdx.files.internal("null.png")); // или свой фон
        WindowStyle style = new WindowStyle();
        style.titleFont = font;
        style.background = new TextureRegionDrawable(new TextureRegion(bgTex));
        return style;
    }
}
