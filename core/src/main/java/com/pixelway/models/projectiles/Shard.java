package com.pixelway.models.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Shard extends BossAttack {
    private static final float SHARD_SPEED = 5000f;
    private static final float SHARD_WIDTH = 40f;
    private static final float SHARD_HEIGHT = 60f;

    public Shard(World world, Vector2 startPosition, Texture texture) {
        super(world, startPosition, texture, 10, SHARD_WIDTH - 20, SHARD_HEIGHT);

        body.setLinearVelocity(0, -SHARD_SPEED);
    }
}
