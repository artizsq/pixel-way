package com.pixelway.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
//import com.pixelway.windows.InventoryWindow;
import com.pixelway.windows.InventoryWindow;
import com.pixelway.windows.PauseOverlay;

public class BaseUIManager implements MoneyChangeListener {
    private final Stage stage;
    private Label playerName, playerMoney;
    private final ImageButton image, pauseButton;
    private final MainClass game;
    private final PlayerData playerData;
    private final Image moneySign;
    private VirtualJoystick joystick;

    public BaseUIManager(Stage stage, PlayerData playerData, MainClass game) {
        this.stage = stage;
        this.playerData = playerData;
        this.game = game;

        playerData.addMoneyChangeListener(this);

        TextureRegionDrawable moneySignDrawable = new TextureRegionDrawable(new Texture(Gdx.files.internal("imgs/coin.png")));
        moneySign = new Image(moneySignDrawable);
        moneySign.setBounds(350, 920, 32, 32);

        TextureRegionDrawable imageTexture = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("imgs/miniplayer.png"))));
        imageTexture.setMinSize(128, 128);
        image = new ImageButton(imageTexture);
        image.setBounds(200, 905, 128, 128);
        image.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new InventoryWindow(stage, game);
                Gdx.app.log("PLAYER_INFO", "Clicked!");
            }
        });

        TextureRegionDrawable pauseButtonDrawable = new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal("btns/pauseBtn.png"))));
        pauseButtonDrawable.setMinSize(150, 150);
        pauseButton = new ImageButton(pauseButtonDrawable);
        pauseButton.setBounds(20, 900, 150, 150);
        pauseButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                new PauseOverlay(stage, game);
            }
        });

        BitmapFont playerNameFont = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        playerNameFont.getData().scale(1f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(playerNameFont, Color.WHITE);
        playerName = new Label(playerData.playerName, labelStyle);
        playerName.setBounds(355, 940, 100, 125);

        BitmapFont playerMoneyFont = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        playerMoneyFont.getData().scale(0.5f);
        Label.LabelStyle playerMoneyStyle = new Label.LabelStyle(playerMoneyFont, Color.YELLOW);
        playerMoney = new Label(playerData.money + "", playerMoneyStyle);
        playerMoney.setBounds(390, 875, 100, 125);

        joystick = new VirtualJoystick(242, 300, 240, 100, game);
    }

    public void init() {
        stage.addActor(joystick);
        stage.addActor(playerMoney);
        stage.addActor(image);
        stage.addActor(playerName);
        stage.addActor(pauseButton);
        stage.addActor(moneySign);
    }


    public VirtualJoystick getJoystick() {
        return joystick;
    }

    @Override
    public void onMoneyChanged(int newMoney) {
        playerMoney.setText(newMoney + "");
    }

    public void dispose() {
        playerName.getStyle().font.dispose();
        playerMoney.getStyle().font.dispose();
        joystick.dispose();
        playerData.removeMoneyChangeListener(this);

    }
}
