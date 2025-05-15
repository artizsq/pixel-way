package com.pixelway.windows;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.pixelway.MainClass;
import com.pixelway.gameScreens.ShipGameScreen;
import com.pixelway.gameScreens.TradeLocationScreen;
import com.pixelway.models.Player;
import com.pixelway.utils.VirtualJoystick;

public class TeleportWindow extends Window {

    private final Image darkOverlay;
    private final MainClass game;
    private final Player player;

    public TeleportWindow(Stage stage, String currentLocation, MainClass game, Player player) {
        super("", createWindowStyle());
        VirtualJoystick.inputBlocked = true;
        this.game = game;
        this.player = player;

        setModal(true);
        setSize(1274, 672);
        setPosition(
            (Gdx.graphics.getWidth() - getWidth()) / 2f,
            (Gdx.graphics.getHeight() - getHeight()) / 2f
        );
        setMovable(false);

        darkOverlay = new Image(createOverlayDrawable());
        darkOverlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        darkOverlay.addListener(new ClickListener() {});

        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));

        String location1, location2 = "winter";
        if (currentLocation.equals("start")) {
            location1 = "trade";
        } else if (currentLocation.equals("trade")) {
            location1 = "start";
        } else {
            location1 = "start"; // fallback
        }

        // Изображения
        Image img1 = createLocationImage("imgs/locations/" + location1 + ".png");
        Image img2 = createLocationImage("imgs/locations/" + location2 + ".png");

        // Кнопки
        TextButton btn1 = createTeleportButton(font, location1);
        TextButton btn2 = createTeleportButton(font, location2);

        // Layout
        Table contentTable = new Table();
        contentTable.padTop(34f);

        contentTable.add(img1).size(378, 504).padRight(294);
        contentTable.add(img2).size(378, 504);
        contentTable.row().padTop(2f);
        contentTable.add(btn1).size(240, 100).padRight(294);
        contentTable.add(btn2).size(240, 100);



        add(contentTable).expand().center();


        Texture exitTexture = new Texture(Gdx.files.internal("btns/exit.png"));
        TextureRegionDrawable exitDrawable = new TextureRegionDrawable(new TextureRegion(exitTexture));
        exitDrawable.setMinSize(80, 80);
        ImageButton exitImage = new ImageButton(exitDrawable);
        exitImage.setPosition(14, getHeight() - 90);
        exitImage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                darkOverlay.remove();
                TeleportWindow.this.remove();
                VirtualJoystick.inputBlocked = false;
            }
        });
        this.addActor(exitImage);

        stage.addActor(darkOverlay);
        stage.addActor(this);
    }




    private static WindowStyle createWindowStyle() {
        BitmapFont font = new BitmapFont(Gdx.files.internal("fonts/def.fnt"));
        Texture bgTex = new Texture(Gdx.files.internal("imgs/teleport.png"));
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

    private Image createLocationImage(String path) {
        Texture texture = new Texture(Gdx.files.internal(path));
        return new Image(new TextureRegionDrawable(new TextureRegion(texture)));
    }

    private TextButton createTeleportButton(BitmapFont font, final String location) {
        Texture tex = new Texture(Gdx.files.internal("btns/norm.png"));
        TextureRegionDrawable drawable = new TextureRegionDrawable(new TextureRegion(tex));

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.font = font;
        style.fontColor = Color.BLACK;
        style.up = drawable;
        style.down = drawable;

        TextButton button = new TextButton("Телепорт", style);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                teleportTo(location);
            }
        });
        return button;
    }

    private void teleportTo(String location) {
//        System.out.println(location);
        darkOverlay.remove();
        this.remove();
        VirtualJoystick.inputBlocked = false;
        switch (location){
            case "start":
                game.setScreen(new ShipGameScreen(game, player, game.getPlayerData(), true));
                break;
            case "trade":
                game.setScreen(new TradeLocationScreen(game, player, game.getPlayerData(), true));
                break;
        }


    }
}
