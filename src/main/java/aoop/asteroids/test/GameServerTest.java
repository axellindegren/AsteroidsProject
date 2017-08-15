package aoop.asteroids.test;

import aoop.asteroids.model.Asteroid;
import aoop.asteroids.model.GameObject;
import aoop.asteroids.model.ModelContainer;
import aoop.asteroids.network.GameListener;
import aoop.asteroids.network.GameServer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class GameServerTest implements GameListener.CallBack, GameServer.CallBack {

    private static final int CONNECTIONS = 3;

    private GameServer gs;
    private ArrayList<GameListener> gameListeners;
    private int messageCount;


    @Before
    public void init() throws Exception{
        gs = new GameServer(this);
        Thread t = new Thread(gs);
        t.start();
        Thread.sleep(100);
        gameListeners = new ArrayList<>();
        for (int i = 0; i < CONNECTIONS; i++) {
            GameListener gl = new GameListener(this,"localhost", gs.getPort(), 1);
            t = new Thread(gl);
            t.start();
            gameListeners.add(gl);
        }
        Thread.sleep(100);
        messageCount = 0;
    }


    @After
    public void destroy() throws Exception{
        gs.stop();
        gameListeners.forEach(GameListener::stop);
    }


    @Test
    public void testBroadcast() throws Exception {
        gs.broadcast(new ModelContainer().getDocument());
        Thread.sleep(100);
        assertEquals(3, messageCount);
    }


    @Test
    public void testSendHi() throws Exception {
        ArrayList<GameServer.Client> clients = new ArrayList<>();
        gs.getClients().forEach((k, v) -> clients.add(v));
        if (clients.size() == 0) fail("No clients :/");
        boolean result = TestUtilities.callMethod(gs, "sendHi", "Hi mate", clients.get(0).getId());
        assertTrue("Could not send Hi", result);
    }

    @Test
    public void testGetClients() throws Exception{
        assertEquals("Not all clients were connected", CONNECTIONS , gs.getClients().size());
    }

    @Test
    public void testPackageSize() throws Exception{
        ModelContainer mc = new ModelContainer();
        ArrayList<GameObject> objs = new ArrayList<>();
        for (int i = 0; i < 300; i++) {
            objs.add(new Asteroid(new Point(1, 2), 1, 3, 4));
        }
        mc.gameObjects = objs;
        mc.timeStamp = 2;
        mc.id = -1;
        gs.broadcast(mc.getDocument());
        Thread.sleep(1000);
        assertEquals(3, messageCount);
    }

    @Override
    public void onReceive(ModelContainer mc, int id) {
        if (mc == null) fail("No ModelContainer was received");
        // Server result
    }

    @Override
    public synchronized void onReceive(ModelContainer mc) {
        messageCount++;
    }
}