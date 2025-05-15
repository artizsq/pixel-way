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
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pixelway.MainClass;

import com.pixelway.database.PlayerData;
import com.pixelway.utils.MoneyChangeListener;
import com.pixelway.utils.VirtualJoystick;

public class ShopScreen extends Window implements MoneyChangeListener {
    private MainClass game;
    private final Image darkOverlay;
    private final BitmapFont font;
    private final Stage stage;
    private Label currentHPLabel;
    private Label currentSTRLabel;
    private Label currentSHIELDLabel;

    public ShopScreen(Stage stage, MainClass game) {
        super("", createWindowStyle());
        VirtualJoystick.inputBlocked = true;
        this.game = game;
        this.stage = stage;

        PlayerData playerData = game.getPlayerData();
        playerData.addMoneyChangeListener(this); // Регистрируем ShopScreen как слушателя

        setModal(true);
        setMovable(false);
        setSize(672, 512);
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2f,
            (Gdx.graphics.getHeight() - getHeight()) / 2f
        );

        TextureRegionDrawable overlayDrawable = createOverlayDrawable();
        darkOverlay = new Image(overlayDrawable);
        darkOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        darkOverlay.addListener(new ClickListener() {}); // блокирует клики

        font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        font.getData().scale(0.3f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.BLACK);



        Label priceLabel = new Label("- ЦЕНЫ -\n\nХП: 15\nСИЛА: 20\nЩИТ: 30", labelStyle);
        priceLabel.setBounds(35, 110, 80, 50);

        this.addActor(priceLabel);


        // Создаем Label для текущих характеристик
        currentHPLabel = new Label(playerData.hp + "", labelStyle);
        currentSTRLabel = new Label(playerData.strength + "", labelStyle);
        currentSHIELDLabel = new Label(playerData.shield + "", labelStyle);

        // Создаем изображения характеристик
        Image HPImage = new Image(new TextureRegion(new Texture(Gdx.files.internal("imgs/hp.png"))));
        Image STRImage = new Image(new TextureRegion(new Texture(Gdx.files.internal("imgs/str.png"))));
        Image ShieldImage = new Image(new TextureRegion(new Texture(Gdx.files.internal("imgs/shield.png"))));

        HPImage.setBounds(120, 330, 80, 84);
        STRImage.setBounds(480, 336, 80, 84);
        ShieldImage.setBounds(308, 140, 72, 84);

        TextButton upgradeHPButton = createUpgradeButton(() -> {
            if (playerData.money < 15) {
                new AlertWindow(stage, "У вас недостаточно средств!");
            } else {
                playerData.subtractMoney(15); // Используем метод PlayerData
                playerData.hp += 1;
                currentHPLabel.setText(playerData.hp + "");
            }
        });
        TextButton upgradeSTRButton = createUpgradeButton(() -> {
            if (playerData.money < 20) {
                new AlertWindow(stage, "У вас недостаточно средств!");
            } else {
                playerData.subtractMoney(20); // Используем метод PlayerData
                playerData.strength += 1;
                currentSTRLabel.setText(playerData.strength + "");
            }
        });
        TextButton upgradeSHIELDButton = createUpgradeButton(() -> {
            if (playerData.money < 30) {
                new AlertWindow(stage, "У вас недостаточно средств!");
            } else {
                playerData.subtractMoney(30); // Используем метод PlayerData
                playerData.shield += 1;
                currentSHIELDLabel.setText(playerData.shield + "");
            }
        });

        upgradeHPButton.setPosition(120, 271);
        upgradeSTRButton.setPosition(473, 271);
        upgradeSHIELDButton.setPosition(296, 80);

        currentHPLabel.setPosition(upgradeHPButton.getX() - currentHPLabel.getWidth() - 20, upgradeHPButton.getY() + (upgradeHPButton.getHeight() - currentHPLabel.getHeight()) / 2f);
        currentSTRLabel.setPosition(upgradeSTRButton.getX() - currentSTRLabel.getWidth() - 20, upgradeSTRButton.getY() + (upgradeSTRButton.getHeight() - currentSTRLabel.getHeight()) / 2f);
        currentSHIELDLabel.setPosition(upgradeSHIELDButton.getX() - currentSHIELDLabel.getWidth() - 20, upgradeSHIELDButton.getY() + (upgradeSHIELDButton.getHeight() - currentSHIELDLabel.getHeight()) / 2f);

        this.addActor(HPImage);
        this.addActor(STRImage);
        this.addActor(ShieldImage);
        this.addActor(upgradeHPButton);
        this.addActor(upgradeSTRButton);
        this.addActor(upgradeSHIELDButton);
        this.addActor(currentHPLabel);
        this.addActor(currentSTRLabel);
        this.addActor(currentSHIELDLabel);

        Texture exitTexture = new Texture(Gdx.files.internal("btns/exit.png"));
        TextureRegionDrawable exitDrawable = new TextureRegionDrawable(new TextureRegion(exitTexture));
        exitDrawable.setMinSize(50, 50);
        ImageButton exitImage = new ImageButton(exitDrawable);
        exitImage.setPosition(7, 454);
        exitImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                darkOverlay.remove();
                ShopScreen.this.remove();
                VirtualJoystick.inputBlocked = false;
            }
        });
        this.addActor(exitImage);

        stage.addActor(darkOverlay);
        stage.addActor(this);
    }

    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        Texture bgTex = new Texture(Gdx.files.internal("imgs/shop.png"));
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

    private TextButton createUpgradeButton(Runnable action) {
        Texture upgradeTexture = new Texture(Gdx.files.internal("btns/shopBtn.png"));
        TextureRegionDrawable upgradeDrawable = new TextureRegionDrawable(new TextureRegion(upgradeTexture));
        upgradeDrawable.setMinSize(168, 50);
        TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();

        BitmapFont buttonfont = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        textButtonStyle.font = buttonfont;
        textButtonStyle.up = upgradeDrawable;
        textButtonStyle.down = upgradeDrawable;
        textButtonStyle.over = upgradeDrawable;

        TextButton button = new TextButton("Улучшить", textButtonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                action.run();
            }
        });

        return button;
    }


    @Override
    public void onMoneyChanged(int newMoney) {
        Gdx.app.log("LISTENER", "Player money: " + newMoney);
    }
}
