package com.pixelway.models.characters;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixelway.MainClass;
import com.pixelway.screens.GameEndScreen;
import com.pixelway.screens.location.TradeLocationScreen;
import com.pixelway.screens.minigames.TraderBattleScreen;
import com.pixelway.utils.HPChangeListener;
import com.pixelway.windows.DialogueWindow;

import java.util.ArrayList;
import java.util.List;

public class TraderBoss extends Boss {
    private int health = 1000;

    private Player player;

    public TraderBoss(Vector2 position, float width, float height, World world, MainClass game, int health, String texturePath, Player player) {
        super(position, width, height, world, game, health, texturePath);

        this.player = player;

    }

    public int getHealth() {
        return health;
    }
    @Override
    protected void onBossDefeated() {
        dispose();
        game.setBgMusic("songs/game.mp3");
        game.setScreen(new TradeLocationScreen(game, player, game.getPlayerData(), "bossFight"));

    }
}
