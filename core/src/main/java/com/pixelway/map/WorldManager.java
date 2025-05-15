package com.pixelway.map;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class WorldManager {
    private World world;
    private Vector2 center;


    public WorldManager() {
        world = new World(new Vector2(0, 0), true);
    }

    // density = плотность
    // friction = трение
    // restitution = упругость
    public Fixture createDynamicBox(float width, float height, float density, float friction, float restitution) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.fixedRotation = true;

        Body body = world.createBody(bodyDef);

        center = new Vector2(width + width/3.5f, height);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height, center, 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = friction;
        fixtureDef.restitution = restitution;

        Fixture fixture = body.createFixture(fixtureDef);

        shape.dispose();

        return fixture;
    }

    public Fixture createStaticBody(float width, float height, float density) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;

        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height);

        Fixture fixture = body.createFixture(shape, density);

        shape.dispose();

        return fixture;
    }

    public World getWorld() {
        return world;
    }

    public void dispose(){
        world.dispose();
    }




}
