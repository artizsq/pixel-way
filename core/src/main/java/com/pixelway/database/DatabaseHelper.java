package com.pixelway.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.pixelway.MainClass;


import java.util.ArrayList;

public class DatabaseHelper {
    private static final String USER_FILE_PATH = "data.json";


    public static PlayerData loadPlayerData() {
        FileHandle file = Gdx.files.local(USER_FILE_PATH);
        if (file.exists()) {
            Json json = new Json();
            return json.fromJson(PlayerData.class, file);
        } else {
            PlayerData data = new PlayerData();
            return data;
        }
    }

    public static PlayerData emptyPlayerData(MainClass game){
        PlayerData data = game.getPlayerData();
        data.x = 527;
        data.y = 540;
        data.inventory = new ArrayList<>();
        data.hp = 10;
        data.money = 50;
        data.activeMissions = new ArrayList<>();
        data.strength = 5;
        data.dialogIDS = new ArrayList<>();
        data.currentMap = "start";
        data.chestItems = new ArrayList<>();
        data.reqTP_items = new ArrayList<>();
        data.shield = 10;
        game.saveData();
        return data;
    }

    public static void savePlayerData(PlayerData data) {
        Json json = new Json();
        FileHandle file = Gdx.files.local(USER_FILE_PATH);
        file.writeString(json.prettyPrint(data), false);
    }
    public static boolean hasSaveData() {
        FileHandle file = Gdx.files.local(USER_FILE_PATH);
        if (!file.exists() || file.length() == 0) return false;

        Json json = new Json();
        PlayerData data = json.fromJson(PlayerData.class, file);

        boolean hasGameplayData = data.x != 527 || data.y != 540 || data.hp < 10 || !data.inventory.isEmpty();

        return hasGameplayData;
    }



}
