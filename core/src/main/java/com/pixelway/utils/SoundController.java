package com.pixelway.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.pixelway.database.DatabaseHelper;

public class SoundController {
    private AssetManager assets;
    private Sound currentWalkSound;

    public SoundController(String defaultSoundPath) {
        assets = new AssetManager();
        assets.load("sounds/grass.mp3", Sound.class);
        assets.load("sounds/wooden.mp3", Sound.class);
        assets.load("sounds/hit.mp3", Sound.class);
        assets.load("sounds/pluh.mp3", Sound.class);
        assets.load("sounds/shield.mp3", Sound.class);
        assets.load("sounds/text/main.wav", Sound.class);

        assets.finishLoading();

        if (assets.isLoaded(defaultSoundPath, Sound.class)) {
            currentWalkSound = assets.get(defaultSoundPath, Sound.class);
        } else {
            Gdx.app.error("SoundController", "Default sound not loaded: " + defaultSoundPath);
        }
    }


    public void playWalk() {
        float volume = DatabaseHelper.loadPlayerData().soundVolume;
        if (currentWalkSound != null) {
            currentWalkSound.play(volume);
        }
    }


    public void setWalkSound(String newSoundPath) {
        if (newSoundPath == null || newSoundPath.isEmpty()) {
            return;
        }

        if (assets.isLoaded(newSoundPath, Sound.class)) {
            currentWalkSound = assets.get(newSoundPath, Sound.class);
        } else {
            Gdx.app.error("SoundController", "Sound not loaded: " + newSoundPath);
        }
    }



    public void dispose() {
        assets.dispose();
    }

}
