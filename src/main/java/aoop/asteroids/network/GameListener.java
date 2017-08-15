package aoop.asteroids.network;

import aoop.asteroids.model.ModelContainer;
import com.sun.istack.internal.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;

public class GameListener extends UDPListener implements Runnable {
    private final Logger logger = LoggerFactory.getLogger(GameListener.class);

    private int localPort;
    private int serverPort;
    private int id;
    private boolean isRunning;
    private DatagramSocket listener;
    private int type;
    private InetAddress address;

    private CallBack callBack;

    public interface CallBack {
        void onReceive(ModelContainer mc);
    }

    public GameListener(CallBack cb, String address, int port, int type) {
        try {
            this.address = InetAddress.getByName(address);
        } catch (UnknownHostException e) {
            logger.error("Unknown host", e);
        }
        this.serverPort = port;
        this.type = type;
        this.id = -1;
        callBack = cb;

    }

    private boolean sendHi(String message) {
        HashMap<String, String> values = new HashMap<>();
        values.put("message", message);
        values.put("port", Integer.toString(localPort));
        values.put("type", Integer.toString(type));
        values.put("id", Integer.toString(id));
        return send(UDPListener.newConnectionXML(values), address, serverPort);
    }

    @Override
    public void run() {
        // find open port for listener and start it
        localPort = initListener(-1);
        if (localPort <= 0) return;

        logger.info("0.0.0.0:{} Trying to establish connection {}:{}", localPort, address, serverPort);

        for (int i = 0; i < 3; i++) {// send hi
            sendHi("Hi, I want to connect");
            // receive id, set id
            logger.info("0.0.0.0:{} Waiting for response...", localPort);
            if (receiveAndParse()) break;
            logger.info("0.0.0.0:{} Retrying...", localPort);
        }

        if (id >= 0) {
            isRunning = true;
            logger.info("0.0.0.0:{} listener started successfully (recv id {})", localPort, id);
        } else {
            logger.warn("0.0.0.0:{} listener failed to start :(", localPort);
        }
        while (isRunning) {
            receiveAndParse();
        }
        socket.close();
    }

    public boolean send(Document d){
        return send(d,address, serverPort);
    }

    @Override
    protected void parseDocument(Document d, @NotNull InetAddress address) {
        Node rootElement = d.getFirstChild();
        String nodeName = rootElement.getNodeName();
        if (nodeName.equalsIgnoreCase("java")) {
            NodeList nl = d.getElementsByTagName("id");
            if(nl.getLength() == 1) id = Integer.parseInt(nl.item(0).getTextContent());
            nl = d.getElementsByTagName("message");
            if(nl.getLength() == 1) logger.info("({}): Server says: {}",id, nl.item(0).getTextContent());
        } else {
            callBack.onReceive(ModelContainer.fromDocument(d));
        }
    }

    public void stop() { isRunning = false; }

    public int getID() {return this.id;}
}
