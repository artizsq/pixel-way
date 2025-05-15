package com.pixelway.map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Polyline;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ShortArray;

import java.util.ArrayList;
import java.util.Arrays;

public class TiledObjectsConverter {
    public static Array<Fixture> importObjects(TiledMap tiledMap, WorldManager worldManager, float scale) {
        Array<Fixture> fixtures = new Array<>();

        MapObjects objects = tiledMap.getLayers().get("objects").getObjects();

        for (MapObject object : objects) {
            Shape shape;

            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object, scale);
            } else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject) object, scale);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object, scale);
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object, scale);
            } else {
                Gdx.app.error("Tiled Map importing", "Can't import object of " + object.getClass() + " type");
                continue;
            }

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;

            Body body = worldManager.getWorld().createBody(bodyDef);

            Fixture fixture = body.createFixture(shape, 1);

            shape.dispose();

            fixtures.add(fixture);
        }
        return fixtures;
    }

    private static PolygonShape getRectangle(RectangleMapObject rectangleMapObject, float scale) {
        Rectangle rect = rectangleMapObject.getRectangle();
        PolygonShape shape = new PolygonShape();
        Vector2 size = new Vector2((rect.x + rect.width * 0.5f) * scale, (rect.y + rect.height * 0.5f) * scale);
        shape.setAsBox(rect.width / 2 * scale, rect.height / 2 * scale, size, 0);
        return shape;
    }

    private static CircleShape getCircle(CircleMapObject circleObject, float scale) {
        Circle circle = circleObject.getCircle();
        CircleShape shape = new CircleShape();
        shape.setRadius(circle.radius * scale);
        shape.setPosition(new Vector2(circle.x * scale, circle.y * scale));
        return shape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject, float scale) {
        Polygon polygon = polygonObject.getPolygon();

        float[] vertices = polygon.getTransformedVertices();
        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i)
            worldVertices[i] = vertices[i] * scale;

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineMapObject, float scale) {
        Polyline polyline = polylineMapObject.getPolyline();

        float[] vertices = polyline.getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < worldVertices.length; i++)
            worldVertices[i] = new Vector2(vertices[i * 2] * scale, vertices[i * 2 + 1] * scale);

        ChainShape shape = new ChainShape();
        shape.createChain(worldVertices);
        return shape;
    }
}
