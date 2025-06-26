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
import com.badlogic.gdx.utils.Align;
import com.pixelway.MainClass;
import com.pixelway.screens.location.ShipLocationScreen;
import com.pixelway.screens.location.TPWinterLocationScreen;
import com.pixelway.screens.location.TradeLocationScreen;

import com.pixelway.models.characters.Player;
import com.pixelway.utils.VirtualJoystick;

public class TeleportWindow extends Window {

    private final Image darkOverlay;
    private final MainClass game;
    private final Player player;
    private final Stage stage;

    public TeleportWindow(Stage stage, String currentLocation, MainClass game, Player player) {
        super("", createWindowStyle());
        VirtualJoystick.inputBlocked = true;
        this.game = game;
        this.player = player;
        this.stage = stage;

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

        String location2 = "winter";
        String location1 = "start";
        if (currentLocation.equals("start")) {
            location1 = "trade";
        } else if (currentLocation.equals("trade")) {
            location1 = "start";
        } else if (currentLocation.equals("winter")) {
            location2 = "trade";
        }



        Image img1 = createLocationImage("imgs/locations/" + location1 + ".png");
        boolean hasWinterKey = game.getPlayerData().reqTP_items.contains("winterKey");

        Image img2 = createLocationImage("imgs/locations/" + location2 + ".png");

        Stack winterStack = new Stack();
        winterStack.addActor(img2);


        TextButton btn1 = createTeleportButton(font, location1);
        TextButton btn2 = createTeleportButton(font, location2);
        if (!hasWinterKey) {
            Image dim = new Image(createDimDrawable());
            dim.setSize(378, 504);
            winterStack.addActor(dim);

            Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = font;
            labelStyle.fontColor = Color.WHITE;

            Label lockedLabel = new Label("Недоступно", labelStyle);
            lockedLabel.setFontScale(1.2f);
            lockedLabel.setAlignment(Align.center);
            lockedLabel.setSize(378, 504);
            lockedLabel.setPosition(0, 0);

            winterStack.addActor(lockedLabel);

            btn2.setDisabled(true);
        }

        Table contentTable = new Table();
        contentTable.padTop(34f);

        contentTable.add(img1).size(378, 504).padRight(294);
        contentTable.add(winterStack).size(378, 504);
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
    private TextureRegionDrawable createDimDrawable() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 0.6f);
        pixmap.fill();
        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return new TextureRegionDrawable(new TextureRegion(texture));
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

        if (location.equals("winter") && !game.getPlayerData().reqTP_items.contains("winterKey")) {
            return;
        }

        darkOverlay.remove();
        this.remove();
        VirtualJoystick.inputBlocked = false;
        game.getScreen().dispose();

        switch (location) {
            case "start":
                game.setScreen(new ShipLocationScreen(game, player, game.getPlayerData(), true));
                break;
            case "trade":
                game.setScreen(new TradeLocationScreen(game, player, game.getPlayerData(), true));
                break;
            case "winter":
                game.setScreen(new TPWinterLocationScreen(game, player, game.getPlayerData(), false, true));
                break;
        }
    }



}

