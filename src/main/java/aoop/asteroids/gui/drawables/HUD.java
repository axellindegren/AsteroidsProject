package aoop.asteroids.gui.drawables;

import aoop.asteroids.gui.Renderer;
import aoop.asteroids.model.Player;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class HUD implements IDrawable {

    private static final int TEXT_SIZE = 10;
    private static final int SPACING = 10;


    private Collection<Player> players;

    public HUD(Collection<Player> players){
        this.players = players;
    }

    public void removePlayer(Player p) {
        players.remove(p);
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    @Override
    public void draw(Graphics2D g) {
        int xOffset = 10;
        int yOffset = 0;
        for (Player player : players) {
            yOffset++;
            int xStr = xOffset;
            int yStr = SPACING  + (yOffset * (TEXT_SIZE + SPACING));
            g.setColor(player.getColor());
            g.drawString(player.getNickname() + ":", xStr, yStr);
            g.drawString(Integer.toString(player.getScore()), xStr + g.getFontMetrics().stringWidth(player.getNickname()) + xOffset, yStr);
        }
    }

    @Override
    public void onRendererAttached(Renderer parent) { /* Probably not using this */ }
}
