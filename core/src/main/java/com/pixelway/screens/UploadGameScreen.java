package com.pixelway.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.pixelway.MainClass;
import com.pixelway.database.DatabaseHelper;
import com.pixelway.database.PlayerData;
import com.pixelway.screens.location.StartIslandScreen;
import com.pixelway.screens.location.TPWinterLocationScreen;
import com.pixelway.screens.location.TradeLocationScreen;
import com.pixelway.map.WorldManager;
import com.pixelway.models.characters.Player;

public class UploadGameScreen implements Screen {

    private MainClass game;

    public UploadGameScreen(MainClass game) {
        this.game = game;
    }

    @Override
    public void show() {
        PlayerData loadedPlayerData = DatabaseHelper.loadPlayerData();
        game.setPlayerData(loadedPlayerData);

        WorldManager worldManager = new WorldManager();

        Player player = new Player(
            new Vector2(loadedPlayerData.x, loadedPlayerData.y),
            52f, 100f,
            worldManager.getWorld()
        );

        game.setBgMusic("songs/game.mp3");

        switch (loadedPlayerData.currentMap) {
            case "start":
                game.setScreen(new StartIslandScreen(game, player, loadedPlayerData, false));
                break;
            case "trade":
                game.setScreen(new TradeLocationScreen(game, player, loadedPlayerData, false));
                break;
            case "winter":
                game.setScreen(new TPWinterLocationScreen(game, player, loadedPlayerData, false, false));
                break;
            default:
                game.setScreen(new StartIslandScreen(game, player, loadedPlayerData, false));
                break;
        }

        worldManager.dispose();
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 1, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void dispose() {}
    @Override
    public void hide() {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
}
