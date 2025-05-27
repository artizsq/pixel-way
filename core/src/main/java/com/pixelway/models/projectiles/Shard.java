package com.pixelway.models.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Shard extends BossAttack {
    private static final float SHARD_SPEED = 5000f;
    private static final float SHARD_WIDTH = 20f; // Определяем размеры как константы
    private static final float SHARD_HEIGHT = 40f;

    public Shard(World world, Vector2 startPosition, Texture texture) {
        // Передаем ширину и высоту в конструктор BossAttack
        super(world, startPosition, texture, 10, SHARD_WIDTH, SHARD_HEIGHT);

        body.setLinearVelocity(0, -SHARD_SPEED);
    }
}
