package aoop.asteroids.controller.games;

import aoop.asteroids.controller.DatabaseHandler;
import aoop.asteroids.model.GameObject;
import aoop.asteroids.model.Highscore;
import aoop.asteroids.model.ModelContainer;
import aoop.asteroids.network.GameServer;
import aoop.asteroids.controller.InputHandler;
import aoop.asteroids.model.Player;
import aoop.asteroids.network.UDPListener;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class HostGame extends Game implements GameServer.CallBack {

    private Player player;
    private InputHandler ih;
    private int port;
    private GameServer gameServer;
    private HashMap<Integer, Player> players;

    public HostGame(InputHandler ih, int port, Player p) {
        super();
        player = p;
        put(0,player);
        this.ih = ih;
        this.port = port;
        gameServer = new GameServer(this, port);
        players = new HashMap<>();
    }

    @Override
    public void start() {
        Thread t = new Thread(gameServer);
        t.start();
        reset();
    }

    @Override
    public void stop() {
        gameServer.stop();
    }


    @Override
    public boolean isOver() {
        int playersLeft = 0;
        for (Player p : getPlayers()){
            if(!p.getShip().isDestroyed()) playersLeft++;
        }
        return playersLeft < 2;
    }

    @Override
    protected void onGameOver() {
        DatabaseHandler databaseHandler = new DatabaseHandler("highscores");
        databaseHandler.openDatabase();
        getPlayers().forEach((p) -> {
            if (p.getScore() > 0) databaseHandler.storeHighscore(new Highscore(p.getNickname(),p.getScore()));
        });
        databaseHandler.closeDatabase();
        super.onGameOver();
    }

    @Override
    public void update() {
        updatePlayer(player, ih);
        { // parse all players
            updatePlayerList(players);
        }

        {   // create a new ModelContainer and send it
            ModelContainer mc = new ModelContainer();
            ArrayList<GameObject> objs = new ArrayList<>();
            getGameObjects().forEach((o) -> objs.add(o));
            ArrayList<Player> players = new ArrayList<>();
            getPlayers().forEach(p -> players.add(p));
            mc.players = players;
            mc.gameObjects = objs;
            mc.timeStamp = System.currentTimeMillis();
            mc.id = -1;
            gameServer.broadcast(mc.getDocument());
        }
        super.update();
    }

    @Override
    public synchronized void onReceive(ModelContainer mc, int id) {
        if (id < 0) return;
        for (Player p : mc.players) {
            if(p.getID() == id) {
                //p.setScore(0);
                players.put(id,p);
                break;
            }
        }
    }
}
