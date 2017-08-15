package aoop.asteroids.controller.games;

import aoop.asteroids.controller.InputHandler;
import aoop.asteroids.model.Player;

import java.awt.*;


public class SinglePlayerGame extends Game {

    private InputHandler ih;
    private Player player;

    public SinglePlayerGame(InputHandler ih) {
        super();
        this.ih = ih;
        player = new Player("SinglePlayer", Color.white);
        put(0,player);
    }

    @Override
    public void start() {
        reset();
    }

    @Override
    public void stop() {}


    @Override
    public boolean isOver() {
        return player.getShip().isDestroyed();
    }

    @Override
    public void update() {
        updatePlayer(player, ih);
        super.update();
    }
}
