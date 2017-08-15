package aoop.asteroids.gui.drawables;


import aoop.asteroids.gui.Renderer;

import java.awt.*;

public class Button extends MenuItem {

    private final static int SPACING = 20;

    private Renderer renderer;

    public Button(String name, Menu.MenuEventCallback menuEventCallback) {
        super(name, menuEventCallback);
    }



    @Override
    public void draw(Graphics2D g) {
        Rectangle clipBounds = g.getClipBounds();
        FontMetrics fm = g.getFontMetrics();
        int width = (clipBounds.width - (SPACING * 2));
        int xBox = g.getClipBounds().x;
        int yBox = g.getClipBounds().y;
        int xStr = xBox + (width / 2) - (fm.stringWidth(getName()) / 2);
        int yStr = yBox + (g.getClipBounds().height / 2) + (fm.getHeight() / 2) - (SPACING / 2);

        if (hasFocus()) {
            g.fillRect(xBox, yBox, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
            g.setColor(Color.BLACK);
            g.drawString(getName(), xStr, yStr);
            g.setColor(Color.WHITE);
        } else {
            g.drawRect(xBox, yBox, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
            g.drawString(getName(), xStr, yStr);
        }
    }

    @Override
    public void onRendererAttached(Renderer parent) {
        renderer = parent;
        if(renderer != null) renderer.repaint();
    }

}
