package com.pixelway.models.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class ArrowAttack extends BossAttack{
    private static final float ARROW_SPEED = 3000f;
    private static final float ARROW_WIGHT = 25f;
    private static final float ARROW_HEIGHT = 64f;

    public ArrowAttack(World world, Vector2 startPosition, Texture texture){
        super(world, startPosition, texture, 20, ARROW_WIGHT, ARROW_HEIGHT);

        body.setLinearVelocity(0, -ARROW_SPEED);
    }
}
