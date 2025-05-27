package com.pixelway.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.pixelway.MainClass;

public class VirtualJoystick extends Actor {
    private Texture joystickBackground;
    private Texture joystickKnob;

    private Vector2 joystickPosition;
    private Vector2 knobPosition;

    private float joystickRadius;
    private float knobRadius;
    private float outerRadiusThreshold;

    private Vector2 direction;
    private MainClass game; // Ссылка на MainClass
    public static boolean inputBlocked;

    public VirtualJoystick(float x, float y, float joystickRadius, float knobRadius, MainClass game) {
        this.joystickPosition = new Vector2(x, y);
        this.knobPosition = new Vector2(x, y);
        this.joystickRadius = joystickRadius;
        this.knobRadius = knobRadius;
        this.direction = new Vector2(0, 0);
        this.outerRadiusThreshold = joystickRadius * 2f;
        this.game = game; // Получаем ссылку на MainClass

        joystickBackground = new Texture("texture/joystick/background.png");
        joystickKnob = new Texture("texture/joystick/knob.png");

        setBounds(x - joystickRadius, y - joystickRadius, joystickRadius * 2, joystickRadius * 2);
    }

    @Override
    public void act(float delta) {
        if (game != null && inputBlocked) { // Используем нестатическую переменную из BaseUIManager
            direction.set(new Vector2(0, 0));
            knobPosition.set(joystickPosition);
            game.setJoystickControllingPointer(-1); // Сбрасываем управление
            return;
        }
        super.act(delta);

        boolean isTouched = false;
        int currentTouchingPointer = -1;

        for (int i = 0; i < Gdx.input.getMaxPointers(); i++) {
            if (Gdx.input.isTouched(i)) {
                isTouched = true;
                currentTouchingPointer = i;
                break;
            }
        }

        if (isTouched) {
            Vector2 touchPos = new Vector2(Gdx.input.getX(currentTouchingPointer), Gdx.graphics.getHeight() - Gdx.input.getY(currentTouchingPointer));

            if (game.getJoystickControllingPointer() == -1) { // Читаем из MainClass
                if (touchPos.dst(joystickPosition) <= joystickRadius) {
                    game.setJoystickControllingPointer(currentTouchingPointer); // Записываем в MainClass
                    direction.set(touchPos).sub(joystickPosition).nor();
                    knobPosition.set(joystickPosition).add(direction.cpy().scl(joystickRadius * 0.5f));
                }
            } else if (game.getJoystickControllingPointer() == currentTouchingPointer) { // Читаем из MainClass
                direction.set(touchPos).sub(joystickPosition).nor();
                float distance = touchPos.dst(joystickPosition);
                if (distance > joystickRadius * 0.5f) {
                    knobPosition.set(joystickPosition).add(direction.cpy().scl(joystickRadius * 0.5f));
                } else {
                    knobPosition.set(touchPos);
                }
            }
        } else {
            game.setJoystickControllingPointer(-1); // Сбрасываем управление
            direction.setZero();
            knobPosition.set(joystickPosition);
        }

        if (knobPosition.dst(joystickPosition) > joystickRadius * 0.5f) {
            direction.set(knobPosition).sub(joystickPosition).nor();
            knobPosition.set(joystickPosition).add(direction.cpy().scl(joystickRadius * 0.5f));
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.draw(joystickBackground, joystickPosition.x - joystickRadius, joystickPosition.y - joystickRadius, joystickRadius * 2, joystickRadius * 2);
        batch.draw(joystickKnob, knobPosition.x - knobRadius, knobPosition.y - knobRadius, knobRadius * 2, knobRadius * 2);
    }

    public Vector2 getDirection() {
        return new Vector2(direction);
    }

    public void reset() {
        direction.setZero();
        knobPosition.set(joystickPosition);
        game.setJoystickControllingPointer(-1); // Сбрасываем при ресете
    }

    public void dispose() {
        joystickBackground.dispose();
        joystickKnob.dispose();
    }
}
