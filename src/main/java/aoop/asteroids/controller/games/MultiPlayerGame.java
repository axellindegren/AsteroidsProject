package aoop.asteroids.controller.games;

import aoop.asteroids.controller.InputHandler;
import aoop.asteroids.model.DestroyReason;
import aoop.asteroids.model.ModelContainer;
import aoop.asteroids.model.Player;
import aoop.asteroids.network.GameListener;
import com.sun.istack.internal.NotNull;

import java.awt.*;
import java.util.ArrayList;


public class MultiPlayerGame extends SpectateGame {

    private InputHandler ih;

    public MultiPlayerGame(InputHandler ih, String address, int port, @NotNull Player p) {
        super(address, port);
        this.ih = ih;
        this.player = p;
        player.getShip().destroy(new DestroyReason() {});
    }

    @Override
    public void update() {
        updatePlayer(player, ih);
        player.setID(gameListener.getID());
        if(!getPlayers().contains(player) && player.getID() >= 0){
            put(player.getID(), player);
        }

        super.update();

        {   // Create package and send stuff
            ModelContainer mc = new ModelContainer();
            mc.players = new ArrayList<>();
            mc.players.add(player.clone());
            mc.id = gameListener.getID();
            mc.timeStamp = System.currentTimeMillis();
            gameListener.send(mc.getDocument());
        }
    }

}
