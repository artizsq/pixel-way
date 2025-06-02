package com.pixelway.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align; // Импортируем Align для выравнивания текста

import java.util.List;

public class TextManager {
    private final List<String> dialogs;
    private int currentDialogIndex = 0;

    private final StringBuilder currentDisplayLine = new StringBuilder();
    private float timer = 0;
    private final float typeSpeed;
    private int charIndex = 0;

    private final Sound textSound;
    private final BitmapFont font;
    private final float maxWidth;

    private boolean allDialogsFinished = false;
    private boolean typingFinished = false;
    private boolean started = false;

    public TextManager(List<String> dialogs, float typeSpeed, Sound textSound, BitmapFont font) {
        this.dialogs = dialogs;
        this.typeSpeed = typeSpeed;
        this.textSound = textSound;
        this.font = font;
        this.maxWidth = Gdx.graphics.getWidth() * 0.95f;

        startCurrentDialog();
    }

    private void startCurrentDialog() {
        currentDisplayLine.setLength(0);
        timer = 0;
        charIndex = 0;
        typingFinished = false;
        started = false;
    }

    public void update(float delta) {
        if (allDialogsFinished) return;

        if (!started) {
            started = true;
            return;
        }

        if (!typingFinished) {
            timer += delta;
            if (timer >= typeSpeed) {
                String fullDialog = dialogs.get(currentDialogIndex);

                while (timer >= typeSpeed && charIndex < fullDialog.length()) {
                    char nextChar = fullDialog.charAt(charIndex);
                    currentDisplayLine.append(nextChar);
                    charIndex++;
                    timer -= typeSpeed;

                    if (nextChar != ' ' && nextChar != '\n') {
                        textSound.play(0.3f);
                    }

                    if (charIndex >= fullDialog.length()) {
                        typingFinished = true;
                        break;
                    }
                }
            }
        }

        if (Gdx.input.justTouched()) {
            if (!typingFinished) {
                currentDisplayLine.setLength(0);
                currentDisplayLine.append(dialogs.get(currentDialogIndex));
                charIndex = dialogs.get(currentDialogIndex).length();
                typingFinished = true;
            } else {
                currentDialogIndex++;
                if (currentDialogIndex < dialogs.size()) {
                    startCurrentDialog();
                } else {
                    allDialogsFinished = true;
                }
            }
        }
    }

    public void draw(SpriteBatch batch, float x, float y) {
        font.draw(batch, currentDisplayLine, x, y, maxWidth, Align.topLeft, true);
    }

    public int getCurrentDialogIndex() {
        return currentDialogIndex;
    }

    public boolean isFinished() {
        return allDialogsFinished;
    }
}
