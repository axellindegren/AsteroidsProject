package aoop.asteroids.controller.games;

import aoop.asteroids.model.*;
import aoop.asteroids.network.GameListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class SpectateGame extends Game implements GameListener.CallBack{

    protected String address;
    protected int port;
    protected GameListener gameListener;
    protected ModelContainer modelContainer;
    protected Player player;

    public SpectateGame(String address, int port) {
        super();
        this.address = address;
        this.port = port;
        gameListener = new GameListener(this, address, port, 123);
    }


    @Override
    public void start() {
        Thread t = new Thread(gameListener);
        t.start();
        reset();
    }

    @Override
    public boolean isOver() {
        int playersLeft = 0;
        for (Player p : getPlayers()){
            if(!p.getShip().isDestroyed()) playersLeft++;
        }
        return playersLeft < 2;
    }

    public void update(){
        if (modelContainer != null) {
            ArrayList<GameObject> gos = new ArrayList<>();
            Map<Integer, Player> players = new HashMap<>();
            modelContainer.gameObjects.forEach((go) -> {
                if (!(go instanceof Spaceship)) gos.add(go);
            });
            final int id = gameListener.getID();
            modelContainer.players.forEach((p) -> {
                if(p.getID() != id) {
                    players.put(p.getID(), p);
                } else {
                    if (p.getShip().isDestroyed()) player.getShip().destroy(new DestroyReason() {});
                }
            });
            if(player != null && player.getID() >= 0) players.put(player.getID(), player);
            objects = gos;
            updatePlayerList(players);
        }
        super.update();
    }

    @Override
    public void stop() {
        gameListener.stop();
    }

    @Override
    public synchronized void onReceive(ModelContainer mc) {
        if (modelContainer == null){
            modelContainer = mc;
            return;
        }

        if (modelContainer.timeStamp < mc.timeStamp){
            modelContainer = mc;
        }
    }
}

