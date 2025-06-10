package com.pixelway.models.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class Boulder extends BossAttack {
    private static final float BOULDER_SPEED = 3000f;
    private static final float BOULDER_WIDTH = 64f;
    private static final float BOULDER_HEIGHT = 64f;

    public Boulder(World world, Vector2 startPosition, Texture texture) {
        super(world, startPosition, texture, 30, BOULDER_WIDTH, BOULDER_HEIGHT);

        float direction = (startPosition.x < 640) ? 1 : -1;
        body.setLinearVelocity(direction * BOULDER_SPEED, 0);
    }
}
