package com.pixelway.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;

public class WorldManager {
    private World world;
    private Vector2 center;



    public WorldManager() {
        world = new World(new Vector2(0, 0), true);
    }




    public World getWorld() {
        return world;
    }

    public void dispose(){
        world.dispose();
    }




}
