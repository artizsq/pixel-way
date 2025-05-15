package com.pixelway.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.pixelway.database.DatabaseHelper;

public class SoundController {
    private AssetManager assets;
    private Sound currentWalkSound;
    private Sound previousWalkSound;
    private String currentSoundPath;

    public SoundController(String defaultSoundPath) {
        assets = new AssetManager();
        // Загружаем нужные звуки
        assets.load("sounds/grass.mp3", Sound.class);
        assets.load("sounds/wooden.mp3", Sound.class);
//        assets.load("sounds/stone.mp3", Sound.class);
        assets.finishLoading();

        // Ставим звук по умолчанию
        if (assets.isLoaded(defaultSoundPath, Sound.class)) {
            currentWalkSound = assets.get(defaultSoundPath, Sound.class);
            currentSoundPath = defaultSoundPath;
        } else {
            Gdx.app.error("SoundController", "Default sound not loaded: " + defaultSoundPath);
        }
    }

    /** Проиграть звук шагов */
    public void playWalk() {
        float volume = DatabaseHelper.loadPlayerData().soundVolume;
        if (currentWalkSound != null) {
            currentWalkSound.play(volume);
        }
    }

    /**
     * Сменить звук ходьбы.
     * Перед сменой сохраняет текущий звук как предыдущий.
     *
     * @param newSoundPath путь к новому звуку ("sounds/stone.mp3" и т.д.)
     */
    public void setWalkSound(String newSoundPath) {
        if (newSoundPath == null || newSoundPath.isEmpty()) {
            Gdx.app.error("SoundController", "Invalid sound path");
            return;
        }

        if (assets.isLoaded(newSoundPath, Sound.class)) {
            previousWalkSound = currentWalkSound; // Сохраняем текущий звук перед заменой
            currentWalkSound = assets.get(newSoundPath, Sound.class);
            currentSoundPath = newSoundPath;
            Gdx.app.log("SoundController", "Звук шагов изменён на: " + newSoundPath);
        } else {
            Gdx.app.error("SoundController", "Sound not loaded: " + newSoundPath);
        }
    }

    /**
     * Вернуть предыдущий звук шагов, если он был сохранён.
     */
    public void revertToPrevious() {
        if (previousWalkSound != null) {
            Sound temp = currentWalkSound;
            currentWalkSound = previousWalkSound;
            previousWalkSound = temp; // Чтобы можно было туда-сюда менять
            Gdx.app.log("SoundController", "Возвращён предыдущий звук шагов");
        } else {
            Gdx.app.error("SoundController", "Нет сохранённого предыдущего звука");
        }
    }

    /** Освобождение ресурсов */
    public void dispose() {
        assets.dispose();
    }
    public boolean isCurrentSound(String soundPath) {
        if (currentWalkSound == null || !assets.isLoaded(soundPath, Sound.class)) {
            return false;
        }
        return currentWalkSound == assets.get(soundPath, Sound.class);
    }
}
