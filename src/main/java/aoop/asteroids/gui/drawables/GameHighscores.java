package aoop.asteroids.gui.drawables;

import aoop.asteroids.controller.DatabaseHandler;
import aoop.asteroids.gui.Renderer;
import aoop.asteroids.model.Highscore;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

public class GameHighscores extends MenuItem {

    private final static int SPACING = 20;

    private Renderer renderer;
    private Collection<Highscore> highscoreList;

    public GameHighscores(String name) {
        super(name, (item) -> {});
        DatabaseHandler databaseHandler = new DatabaseHandler("highscores");
        databaseHandler.openDatabase();
        highscoreList = databaseHandler.getHighscores();
        databaseHandler.closeDatabase();
    }

    @Override
    public void draw(Graphics2D g) {
        Rectangle clipBounds = g.getClipBounds();
        FontMetrics fm = g.getFontMetrics();
        int width = (clipBounds.width - (SPACING * 2));
        int xBox = g.getClipBounds().x;
        int yBox = g.getClipBounds().y;
        int xStr = xBox + SPACING;
        int yStr = yBox + (g.getClipBounds().height / 10) + (fm.getHeight() / 2) - (SPACING / 2);
        int yStrOffset = 0;



        g.setColor(Color.white);
        if (hasFocus()) {
            g.fillRect(xBox, yBox, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
        }else {
            g.drawRect(xBox, yBox, g.getClipBounds().width - 1, g.getClipBounds().height - 1);
        }
        for (Highscore highscore : highscoreList) {
            yStrOffset++;
            if (yStrOffset > 11) break;
            if (hasFocus()) {
                g.setColor(Color.BLACK);
                g.drawString(highscore.getName() + ": " + highscore.getScore(), xStr , yStr + (fm.getHeight() * yStrOffset));
                g.setColor(Color.WHITE);
            } else {
                g.setColor(Color.white);
                g.drawString(highscore.getName() + ": " + highscore.getScore(), xStr, yStr + (fm.getHeight() * yStrOffset));
            }
        }


    }

    @Override
    public void onRendererAttached(Renderer parent) {
        renderer = parent;
        if(renderer != null) renderer.repaint();
    }
}
