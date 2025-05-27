package com.pixelway.models.projectiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.pixelway.MainClass;

public class Bullet {
    private Body body;
    private Texture texture;
    private float speed = 800000f;
    private static final float SCREEN_HEIGHT = 720; // Высота экрана
    private int damage; // Урон пули
    private boolean markedForRemoval = false;
    private Vector2 position;


    // Добавляем World для создания тела Box2D
    public Bullet(Vector2 position, Texture texture, World world, MainClass game) {
        this.texture = texture;
        this.position = position;
        createBody(position, world);
        this.body.setUserData(this); // Важно: привязываем объект Bullet к его Box2D Body
        this.damage = game.getPlayerData().strength;
    }

    private void createBody(Vector2 initialPosition, World world) {
        BodyDef bd = new BodyDef();
        bd.type = BodyDef.BodyType.DynamicBody;
        bd.position.set(initialPosition.x, initialPosition.y); // Начальная позиция пули
        bd.fixedRotation = true;
        bd.gravityScale = 0; // Пуля не должна подвергаться гравитации
        body = world.createBody(bd);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(5f, 5f); // Размеры пули (половина ширины/высоты)

        Fixture f = body.createFixture(shape, 0.1f);
        f.setSensor(true); // Пуля - это сенсор, чтобы она не отталкивала объекты, а только регистрировала столкновения
        shape.dispose();

        body.setLinearVelocity(0, speed); // Пуля движется строго вверх
    }

    public void update(float delta) {
        // Позиция пули уже обновляется Box2D симуляцией
        if (body.getPosition().y > SCREEN_HEIGHT) {
            markedForRemoval = true; // Помечаем для удаления, если вышла за экран
        }
    }

    public void render(SpriteBatch batch) {
        // Отрисовываем текстуру по текущей Box2D позиции тела
        // Учитываем, что body.getPosition() - это центр
        batch.draw(texture, body.getPosition().x - 5, body.getPosition().y - 5, 10, 10);
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public void markForRemoval() {
        this.markedForRemoval = true;
    }

    public void dispose(World world) {
        if (markedForRemoval) {
            world.destroyBody(body);
        }
    }

    public Body getBody() {
        return body;
    }

    public int getDamage() {
        return damage;
    }

    public boolean isOutOfScreen() {
        return position.y > SCREEN_HEIGHT;
    }
}
