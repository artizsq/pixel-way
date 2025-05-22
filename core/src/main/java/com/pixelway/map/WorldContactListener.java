package com.pixelway.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.pixelway.MainClass;
import com.pixelway.database.DatabaseHelper;
import com.pixelway.database.PlayerData;
import com.pixelway.gameScreens.TPWinterGameScreen;
import com.pixelway.models.Player;
import com.pixelway.utils.ImportantZone;
import com.pixelway.utils.SoundController;
import com.pixelway.gameScreens.ShipGameScreen;
import com.pixelway.gameScreens.StartIslandScreen;
import com.pixelway.windows.AlertWindow;
import com.pixelway.windows.SuperDialogue;

public class WorldContactListener implements ContactListener {
    private final MainClass game;
    private final Player player;

    private final SoundController soundController;
    private PlayerData playerData;



    public WorldContactListener(MainClass game, Player player) {
        this.game = game;
        this.player = player;

        this.soundController = player.getSoundController();
    }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        Object udA = a.getUserData();
        Object udB = b.getUserData();

        if (udA instanceof ImportantZone) {
            handleZone((ImportantZone) udA);
        } else if (udB instanceof ImportantZone) {
            handleZone((ImportantZone) udB);
        }


    }

    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        Object udA = a.getUserData();
        Object udB = b.getUserData();

        if (udA instanceof ImportantZone) {
            handleZoneExit((ImportantZone) udA);
        } else if (udB instanceof ImportantZone) {
            handleZoneExit((ImportantZone) udB);
        }
    }

    private void handleZone(ImportantZone zone) {
        player.setInZone(true);
        player.setZone(zone.getZoneType());
        switch (zone.getZoneType()) {
            case TELEPORT:
                handleTransition(zone.getNextZone(), zone.getPlayerData());
                break;
            case SOUND:
                handleSoundChange(zone);
                break;

            case SUPER_DIALOGUE:
                playerData = game.getPlayerData();
                switch (zone.getDialogID()){
                    case 0:
                        if (!playerData.dialogIDS.contains("0")) {
                            new SuperDialogue(zone.getStage(), "Что? Где я? Что я тут делаю?\nНужно срочно найти кого-нибудь.");
                            playerData.dialogIDS.add("0");
                        }

                        break;
                    case 1:
                        if (!playerData.dialogIDS.contains("1")) {
                            new SuperDialogue(zone.getStage(), "О, кажется, я вижу кого-то!\nНужно подойти и спросить, что здесь происходит.");
                            playerData.dialogIDS.add("1");
                        }

                        break;
                }
        }
    }

    private void handleZoneExit(ImportantZone zone){
        player.setInZone(false);

    }

    private void handleSoundChange(ImportantZone zone) {
        String newSoundPath = zone.getSoundPath();
        if (newSoundPath != null) {

                soundController.setWalkSound(newSoundPath);

        }
    }

    private void handleTransition(String mapName, PlayerData playerData) {
        switch (mapName) {
            case "shipMap":
                game.setScreen(new ShipGameScreen(game, player, playerData));
                break;
            case "startMap":
                game.setScreen(new StartIslandScreen(game, player, playerData, true) );
                break;
            case "winter2":
                game.setScreen(new TPWinterGameScreen(game, player, playerData, true));
                break;
            default:
                Gdx.app.log("TELEPORT", "Неизвестная карта: " + mapName);
        }
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
