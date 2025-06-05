package com.pixelway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Json;
import com.pixelway.database.ChestData;
import com.pixelway.database.DatabaseHelper;
import com.pixelway.database.PlayerData;
import com.pixelway.screens.MainMenuScreen;
import com.pixelway.utils.loot.ChestLootGenerator;

public class MainClass extends Game {
    public SpriteBatch batch;
    private ChestData chestData;
    private Music bgMusic;
    private PlayerData playerData;
    private int joystickControllingPointer = -1;
    private ChestLootGenerator chestLootGenerator;


    @Override
    public void create() {
        batch = new SpriteBatch();
        this.playerData = DatabaseHelper.loadPlayerData();
        chestData = DatabaseHelper.loadChestData();

        chestLootGenerator = new ChestLootGenerator();

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("songs/main.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(playerData.musicVolume);

        bgMusic.play();
        setScreen(new MainMenuScreen(this));
    }

    public Music getBgMusic() {
        return bgMusic;
    }


    public PlayerData getPlayerData() {
        return playerData;
    }

    public void saveData() {
        DatabaseHelper.saveChestData(loadChestData());
        DatabaseHelper.savePlayerData(getPlayerData());
    }

    public ChestLootGenerator getChestLootGenerator() {
        return chestLootGenerator;
    }

    public void setBgMusic(String path) {
        if (this.bgMusic != null) {
            this.bgMusic.stop();        // Stop the current music
            this.bgMusic.dispose();     // Release resources of the current music
        }
        this.bgMusic = Gdx.audio.newMusic(Gdx.files.internal(path));
        this.bgMusic.setLooping(true);
        this.bgMusic.setVolume(playerData.musicVolume); // Apply current volume
        this.bgMusic.play();            // Start playing the new music
    }

    public int getJoystickControllingPointer() {
        return joystickControllingPointer;
    }

    public void setJoystickControllingPointer(int joystickControllingPointer) {
        this.joystickControllingPointer = joystickControllingPointer;
    }



    @Override
    public void render() {
        super.render(); // Отрисовка текущего экрана
    }

    @Override
    public void dispose() {
        batch.dispose();
        if (bgMusic != null) {
            bgMusic.dispose(); // Dispose of music on game exit
        }

    }

    public void clearPlayerData() {
        playerData = DatabaseHelper.emptyPlayerData();
        saveData();
    }

    public void setPlayerData(PlayerData loadedPlayerData) {
        this.playerData = loadedPlayerData;
    }

    public ChestData loadChestData() {
        return chestData;
    }

    public void setChestData(ChestData chestData) {
        this.chestData = chestData;
    }


}
