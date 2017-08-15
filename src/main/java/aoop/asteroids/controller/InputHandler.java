package aoop.asteroids.controller;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener{
    private static int N_KEYS = 256;
    private boolean keysPressed[];
    private KeyListener keyListener;

    public InputHandler() {
        keysPressed = new boolean[N_KEYS];
        for (int i = 0; i < N_KEYS; i++) keysPressed[i] = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if(keyListener != null) keyListener.keyTyped(e);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() < N_KEYS) keysPressed[e.getKeyCode()] = true;
        if (keyListener != null) keyListener.keyPressed(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() < N_KEYS) keysPressed[e.getKeyCode()] = false;
        if (keyListener != null) keyListener.keyReleased(e);
    }

    public boolean isPressed(int key) {
        return key < N_KEYS && keysPressed[key];
    }

    public void setKeyListener(KeyListener keyListener) {
        this.keyListener = keyListener;
    }
}