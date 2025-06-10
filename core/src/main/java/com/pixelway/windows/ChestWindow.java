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
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
import com.pixelway.utils.VirtualJoystick;

import java.util.List;

public class ChestWindow extends Window {

    private final Image darkOverlay;
    private final MainClass game;
    private final String chestId;
    private final List<PlayerData.InventorySlot> currentChestItems;

    public ChestWindow(Stage stage, MainClass game, List<PlayerData.InventorySlot> chestItems, String chestId) {
        super("", createWindowStyle());
        this.game = game;
        this.chestId = chestId;
        this.currentChestItems = chestItems;

        VirtualJoystick.inputBlocked = true;
        setSize(606, 252);
        setPosition((Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2);

        TextureRegionDrawable overlayDrawable = createOverlayDrawable();
        darkOverlay = new Image(overlayDrawable);
        darkOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        darkOverlay.addListener(new ClickListener() {});

        Table chestContentTable = new Table();
        chestContentTable.setFillParent(true);
        chestContentTable.padLeft(40);
        chestContentTable.align(com.badlogic.gdx.utils.Align.left);

        int maxItemsInARow = 3;
        int itemsInCurrentRow = 0;

        for (PlayerData.InventorySlot slot : currentChestItems) {
            if (slot != null && slot.name != null && !slot.name.isEmpty()) {
                TextureRegionDrawable itemTexture = new TextureRegionDrawable(new Texture(Gdx.files.internal(slot.imagePath)));
                itemTexture.setMinSize(122, 122);
                ImageButton itemButton = new ImageButton(itemTexture);
                itemButton.setSize(122, 122);
                itemButton.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        new ChestItemWindow(stage, game, slot, currentChestItems, chestId);
                        darkOverlay.remove();
                        ChestWindow.this.remove();
                    }
                });
                chestContentTable.add(itemButton).size(122, 122).padRight(80);
                itemsInCurrentRow++;

                if (itemsInCurrentRow >= maxItemsInARow) {
                    chestContentTable.row();
                    itemsInCurrentRow = 0;
                }
            }
        }

        this.addActor(chestContentTable);

        Texture exitTexture = new Texture(Gdx.files.internal("btns/exit.png"));
        TextureRegionDrawable exitDrawable = new TextureRegionDrawable(new TextureRegion(exitTexture));
        exitDrawable.setMinSize(50, 50);
        ImageButton exitImage = new ImageButton(exitDrawable);
        exitImage.setPosition(0, getHeight() - exitDrawable.getMinHeight());
        exitImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                darkOverlay.remove();
                ChestWindow.this.remove();
                VirtualJoystick.inputBlocked = false;
            }
        });
        this.addActor(exitImage);

        stage.addActor(darkOverlay);
        stage.addActor(this);
    }

    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        Texture bgTex = new Texture(Gdx.files.internal("imgs/chest.png"));
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
}

class ChestItemWindow extends Window{
    private final Image overlay;
    private final Image itemImage;
    private final TextButton grabButton;
    private final Label itemName;
    private final PlayerData.InventorySlot selectedSlot;
    private final List<PlayerData.InventorySlot> currentChestItems;
    private final String chestId;
    private final MainClass game;
    private final Stage stage;
    public ChestItemWindow(Stage stage, MainClass game, PlayerData.InventorySlot slot, List<PlayerData.InventorySlot> chestItems, String chestId){
        super("", createWindowStyle());
        this.stage = stage;
        this.game = game;
        this.selectedSlot = slot;
        this.currentChestItems = chestItems;
        this.chestId = chestId;

        setSize(336, 336);
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2,
            (Gdx.graphics.getHeight() - getHeight()) / 2
        );

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/defBold.fnt")); // Используй AssetManager!
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);

        itemName = new Label(selectedSlot.name, labelStyle);
        itemName.setPosition(70, 80);
        this.addActor(itemName);

        TextureRegionDrawable grabTextureRegion = new TextureRegionDrawable(new Texture(Gdx.files.internal("btns/norm.png"))); // Используй AssetManager!
        grabTextureRegion.setMinSize(180, 60);
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = font;
        buttonStyle.fontColor = Color.BLACK;
        buttonStyle.up = grabTextureRegion;
        buttonStyle.down = grabTextureRegion;

        grabButton = new TextButton("Забрать", buttonStyle);
        grabButton.setPosition((getWidth() - grabButton.getWidth()) / 2, 10);
        grabButton.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                PlayerData playerData = game.getPlayerData();

                playerData.addItem(selectedSlot.name,
                    selectedSlot.effectType, selectedSlot.effectValue, selectedSlot.quantity, selectedSlot.itemDesc, selectedSlot.imagePath);

                currentChestItems.remove(selectedSlot);

                overlay.remove();
                ChestItemWindow.this.remove();

                new ChestWindow(stage, game, currentChestItems, chestId);
            }
        });
        this.addActor(grabButton);

        TextureRegionDrawable itemImageDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal(selectedSlot.imagePath))); // Используй AssetManager!
        itemImageDrawable.setMinSize(192, 192);
        itemImage = new Image(itemImageDrawable);
        itemImage.setPosition((getWidth() - itemImage.getWidth()) / 2, 120);
        this.addActor(itemImage);

        TextureRegionDrawable overlayDrawable = createOverlayDrawable();
        overlay = new Image(overlayDrawable);
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.addListener(new ClickListener() {});


        Texture exitTexture = new Texture(Gdx.files.internal("btns/exit.png"));
        TextureRegionDrawable exitDrawable = new TextureRegionDrawable(new TextureRegion(exitTexture));
        exitDrawable.setMinSize(50, 50);
        ImageButton exitImage = new ImageButton(exitDrawable);
        exitImage.setPosition(0, getHeight() - exitDrawable.getMinHeight());
        exitImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                overlay.remove();
                ChestItemWindow.this.remove();
                new ChestWindow(stage, game, currentChestItems, chestId);
            }
        });
        this.addActor(exitImage);

        stage.addActor(overlay);
        stage.addActor(this);
    }

    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        Texture bgTex = new Texture(Gdx.files.internal("imgs/chestItem.png"));
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
}
