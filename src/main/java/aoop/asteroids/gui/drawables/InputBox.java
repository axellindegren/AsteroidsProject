package aoop.asteroids.gui.drawables;

import aoop.asteroids.gui.Renderer;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputBox extends MenuItem implements KeyListener {

    private final static int SPACING = 20;


    private Renderer renderer;
    private String value;
    private char[] allowedChars;

    public InputBox(String name, Menu.MenuEventCallback menuEventCallback) {
        this(name, menuEventCallback, new char[] {});
    }

    public InputBox(String name, Menu.MenuEventCallback menuEventCallback, char[] allowedChars) {
        super(name, menuEventCallback);
        this.allowedChars   = allowedChars;
        this.value          = "";
    }

    public String getValue() {
        return value;
    }

    @Override
    public void draw(Graphics2D g) {
        Rectangle clipBounds = g.getClipBounds();
        FontMetrics fm = g.getFontMetrics();
        int xBox = clipBounds.x;
        int yBox = clipBounds.y;
        int xStr = xBox + SPACING;
        int yStr = yBox + (clipBounds.height / 2) + (fm.getHeight() / 2) - (SPACING / 2);

        if (hasFocus()) {
            g.fillRect(xBox, yBox, clipBounds.width - 1, clipBounds.height - 1);
            if (value.equals("")) {
                g.setColor(Color.GRAY);
                g.drawString(getName(), xStr, yStr);
            } else {
                g.setColor(Color.BLACK);
                g.drawString(value, xStr, yStr);
            }
            g.setColor(Color.WHITE);
        } else {
            g.drawRect(xBox, yBox, clipBounds.width - 1, clipBounds.height - 1);
            if (value.equals("")) {
                g.setColor(Color.GRAY);
                g.drawString(getName(), xStr, yStr);
                g.setColor(Color.WHITE);
            } else {
                g.drawString(value, xStr, yStr);
            }

        }
    }

    @Override
    public void onRendererAttached(Renderer parent) {
        renderer = parent;
        if(renderer != null) renderer.repaint();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == KeyEvent.VK_ENTER) return;
        if (e.getKeyChar() == KeyEvent.VK_BACK_SPACE) {
            if (value.length() > 0)value = value.substring(0, value.length()-1);
        } else {
            if (allowedChars != null && allowedChars.length > 0) {
                boolean found = false;
                for (char c : allowedChars){
                    if (c == e.getKeyChar()){
                        found = true;
                        break;
                    }
                }
                if(!found) return;
            }
            value += e.getKeyChar();
        }
        if (renderer != null) renderer.repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) { /*we don't need this one */}

    @Override
    public void keyReleased(KeyEvent e) {
        if( e.getKeyCode() == KeyEvent.VK_ENTER) unSelect();
    }
}
