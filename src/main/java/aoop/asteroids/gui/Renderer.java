package aoop.asteroids.gui;

import aoop.asteroids.gui.drawables.IDrawable;

import javax.swing.*;
import java.awt.*;


public class Renderer extends JPanel {

    private IDrawable drawable;

    public Renderer() { }

    @Override
    public void paintComponent (Graphics g) {
        super.paintComponent (g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        this.setBackground(Color.black);
        g.setClip(0,0,getWidth(), getHeight());
        if (drawable != null) drawable.draw(g2);
    }

    public synchronized void setDrawable(IDrawable drawable) {
        this.drawable = drawable;
        this.drawable.onRendererAttached(this);
        this.repaint();
    }

}
