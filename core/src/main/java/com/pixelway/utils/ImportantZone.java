package com.pixelway.utils;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.pixelway.database.PlayerData;
import com.pixelway.models.characters.Player;

public class ImportantZone {

    private Body body;
    private ZoneType zoneType;
    private PlayerData playerData;
    private Player player;
    private String soundPath;

    private String map;

    private boolean inZone = false;
    private Stage stage;
    private int dialogID;


    public enum ZoneType {
        TELEPORT,
        DIALOGUE,
        SHOP,
        TELEPORT_WINDOW,
        TRADE1_DIALOG,
        TRADE2_DIALOG,
        FISH_GAME,
        BERRY_GAME,
        FISHMAN_DIALOG,
        BERRY_DIALOG,
        SAVE,
        BOSS_GAME,
        SOUND,
        CHEST,
        SUPER_DIALOGUE,
        CLICK_TELEPORT
    }


    public ImportantZone(World world,
                         Vector2 position,
                         float width,
                         float height,
                         ZoneType zoneType) {
        this(world, position, width, height, zoneType, null);
    }


    public ImportantZone(World world,
                         Vector2 position,
                         float width,
                         float height,
                         ZoneType zoneType,
                         String soundPath) {
        this.zoneType  = zoneType;
        this.soundPath = soundPath;

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(position);
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2, height / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape    = shape;
        fixtureDef.isSensor = true;

        Fixture fixture = body.createFixture(fixtureDef);
        fixture.setUserData(this);

        shape.dispose();
    }

    public ZoneType getZoneType() {
        return zoneType;
    }

    public String getSoundPath() {
        return soundPath;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public void setStageAndDialog(Stage stage, int dialogID){
        this.stage = stage;
        this.dialogID = dialogID;
    }


    public void setNextZone(String map, PlayerData playerData){
        this.map = map;
        this.playerData = playerData;
    }

    public String  getNextZone(){
        return map;
    }


    public int getDialogID(){
        return dialogID;
    }

    public void setDialogID(int dialogID){
        this.dialogID = dialogID;
    }

    public void setStage(Stage stage){
        this.stage = stage;
    }


    public PlayerData getPlayerData(){
        return playerData;
    }
    public Stage getStage(){
        return stage;
    }



}
