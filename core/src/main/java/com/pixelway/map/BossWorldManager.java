package com.pixelway.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class BossWorldManager extends WorldManager{
    private World world;
    private Vector2 center;



    public BossWorldManager () {
        world = new World(new Vector2(0, -9.8f), true);
    }




    public World getWorld() {
        return world;
    }

    public void dispose(){
        world.dispose();
    }




}

