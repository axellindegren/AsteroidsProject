package aoop.asteroids.gui.drawables;

import aoop.asteroids.gui.Renderer;
import aoop.asteroids.model.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class Debug implements IDrawable {

    private static final int TEXT_SIZE = 10;
    private static final int SPACING = 10;


    private Collection<String> strings;
    public Debug(){
        strings = new ArrayList<>();
    }


    public void addString(String name, String s){
        strings.add(s + " [" + name + "]");
    }

    @Override
    public void draw(Graphics2D g) {
        int xOffset = 10;
        int yOffset = 0;
        for (String s : strings) {
            yOffset++;
            int yStr = SPACING  + (yOffset * (TEXT_SIZE + SPACING));
            int xStr = g.getClipBounds().x + g.getClipBounds().width;
            g.setColor(Color.WHITE);
            g.drawString(s, xStr - (g.getFontMetrics().stringWidth(s) + xOffset), yStr);
        }
    }

    @Override
    public void onRendererAttached(Renderer parent) { /* Probably not using this */ }
}
