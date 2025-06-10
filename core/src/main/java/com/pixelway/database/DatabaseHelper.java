package com.pixelway.database;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.pixelway.MainClass;


import java.util.ArrayList;

public class DatabaseHelper {
    private static final String USER_FILE_PATH = "data.json";
    private static final String CHEST_FILE_PATH = "chestData.json";


    public static PlayerData loadPlayerData() {
        FileHandle file = Gdx.files.local(USER_FILE_PATH);
        if (file.exists()) {
            Json json = new Json();
            return json.fromJson(PlayerData.class, file);
        } else {
            return new PlayerData();
        }
    }


    public static PlayerData emptyPlayerData() {
        PlayerData oldData = loadPlayerData();

        PlayerData data = new PlayerData();

        data.playerName = oldData.playerName;
        data.musicVolume = oldData.musicVolume;
        data.soundVolume = oldData.soundVolume;

        data.x = 527;
        data.y = 540;
        data.inventory = new ArrayList<>();
        data.hp = 10;
        data.money = 50;
        data.activeMissions = new ArrayList<>();
        data.strength = 5;
        data.dialogIDS = new ArrayList<>();
        data.currentMap = "";
        data.chestItems = new ArrayList<>();
        data.reqTP_items = new ArrayList<>();
        data.shield = 0;
        data.isGameStarted = true;

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

        return data.isGameStarted &&
            data.x != 547 &&
            data.y != 540 &&
            data.currentMap != "";
    }

    public static ChestData loadChestData() {
        Json json = new Json();
        FileHandle file = Gdx.files.local(CHEST_FILE_PATH);
        if (file.exists()) {
            return json.fromJson(ChestData.class, file.readString());
        }
        return new ChestData();
    }

    public static void saveChestData(ChestData chestData){
        Json json = new Json();
        FileHandle file = Gdx.files.local(CHEST_FILE_PATH);
        file.writeString(json.prettyPrint(chestData), false);
    }




}
