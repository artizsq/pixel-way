package com.pixelway.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.pixelway.MainClass;
import com.pixelway.database.PlayerData;
import com.pixelway.screens.location.TPWinterLocationScreen;
import com.pixelway.screens.location.WinterLocationScreen;
import com.pixelway.models.characters.Player;
import com.pixelway.utils.ImportantZone;
import com.pixelway.utils.SoundController;
import com.pixelway.screens.location.ShipLocationScreen;
import com.pixelway.screens.location.StartIslandScreen;
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

        player.incrementZoneContact();
        player.setZone(zone.getZoneType());
        System.out.println(zone.getZoneType() + " status: " + player.getInZone());
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

                    case 2:
                        if (!playerData.dialogIDS.contains("2")) {
                            new SuperDialogue(zone.getStage(), "То предсказание, неужели я серьзено должен помочь им?");
                            playerData.dialogIDS.add("2");
                        }
                        break;
                    case 3:
                        if (!playerData.dialogIDS.contains("3")) {
                            new SuperDialogue(zone.getStage(), "Торговец рассказал, что тут находится их база. Я должен уничтожить её.");
                            playerData.dialogIDS.add("3");
                        }
                        break;
                    case 4:
                        if (!playerData.dialogIDS.contains("4")) {
                            new SuperDialogue(zone.getStage(), "Я чувствую на себе ответственность. Нельзя их подвести!");
                            playerData.dialogIDS.add("4");
                        }
                        break;


                }
        }
    }

    private void handleZoneExit(ImportantZone zone){
        player.decrementZoneContact();
        System.out.println(zone.getZoneType() + " status: " + player.getInZone());

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
                game.setScreen(new ShipLocationScreen(game, player, playerData));
                break;
            case "startMap":
                game.setScreen(new StartIslandScreen(game, player, playerData, true) );
                break;
            case "winter2":
                game.setScreen(new WinterLocationScreen(game, player, playerData, true));
                break;
            case "winter1":

                game.setScreen(new TPWinterLocationScreen(game, player, playerData, true, false));
                break;
            default:
                Gdx.app.log("TELEPORT", "Неизвестная карта: " + mapName);
        }
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
