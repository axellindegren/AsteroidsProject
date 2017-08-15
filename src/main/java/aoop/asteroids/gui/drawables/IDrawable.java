package aoop.asteroids.gui.drawables;


import aoop.asteroids.gui.Renderer;

import java.awt.*;

public interface IDrawable {
    void draw(Graphics2D g);
    void onRendererAttached(Renderer parent);
}
