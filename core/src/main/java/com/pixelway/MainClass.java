package com.pixelway;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.pixelway.database.DatabaseHelper;
import com.pixelway.database.PlayerData;
import com.pixelway.screens.MainMenuScreen;
import com.pixelway.utils.VirtualJoystick;

public class MainClass extends Game {
    public SpriteBatch batch;

    private Music bgMusic;
    private PlayerData playerData;
    private int joystickControllingPointer = -1;


    @Override
    public void create() {
        batch = new SpriteBatch();
        this.playerData = DatabaseHelper.loadPlayerData();

        bgMusic = Gdx.audio.newMusic(Gdx.files.internal("songs/main.mp3"));
        bgMusic.setLooping(true);
        bgMusic.setVolume(playerData.musicVolume);  // стартовый уровень

        bgMusic.play();
        setScreen(new MainMenuScreen(this)); // Открываем главное меню
    }

    public Music getBgMusic() {
        return bgMusic;
    }


    public PlayerData getPlayerData() {
        return playerData;
    }

    public void saveData() {
        DatabaseHelper.savePlayerData(getPlayerData());
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
        playerData = DatabaseHelper.emptyPlayerData(this);
    }

    public void setPlayerData(PlayerData loadedPlayerData) {
        this.playerData = loadedPlayerData;
    }


}
