package aoop.asteroids.network;

import aoop.asteroids.model.ModelContainer;
import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.codegen.CompilerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;

public class GameServer extends UDPListener implements  Runnable {

    private final Logger logger = LoggerFactory.getLogger(GameServer.class);
    private final static long MAX_TIMEOUT = 30000;

    private int port;
    private int clientCounter;
    private boolean isRunning;
    private HashMap<Integer, Client> clients;
    private CallBack callback;


    public interface CallBack {
        void onReceive(ModelContainer mc, int id);
    }


    public GameServer(CallBack cb) {
        this(cb, -1);
    }


    public GameServer(CallBack cb, int port) {
        this.callback = cb;
        this.port = port;
        clientCounter = 1;
        clients = new HashMap<>();
    }

    public HashMap<Integer, Client> getClients() { return clients; }

    public int getPort() { return port; }



    @Override
    public void run() {
        isRunning = false;
        port = initListener(port);
        if (port > 0){
            isRunning = true;
            logger.info("Server has started successfully on port {}!", port);
        } else {
            logger.error("could not start server", port);
            return;
        }
        while (isRunning) {
            receiveAndParse();
        }
        socket.close();
    }


    /**
     * Sends hi to client from id
     * @param message a debug message
     * @param id
     */
    private synchronized boolean sendHi(String message, int id){
        HashMap<String, String> values = new HashMap<>();
        values.put("message", message);
        values.put("id", Integer.toString(id));
        Client c = clients.get(id);
        if (c == null) return false;
        return send(UDPListener.newConnectionXML(values), c.getAddress(), c.getPort());
    }

    public void stop() {
        isRunning = false;
    }

    public synchronized void broadcast(Document d) {
        for (Map.Entry<Integer, Client> entry : clients.entrySet()) {
            Client c = entry.getValue();
            if (!send(d, c.getAddress(), c.getPort())) {
                logger.error("Could not broadcast to client ", c.getId());
            }
        }
    }

    /**
     * Method to parse the document
     *  document should contain the following:
     *       <?xml version=1.0 encoding="UTF-8"?>
     *        <java version="__java_version__" class="aoop.asteroids.Asteroids">
     *            <connection>
     *                <message>Hi, I want to connect/Hi, I'm alive</message>
     *                <port>43212</port>
     *                <type>1</type>
     *            </connection>
     *        </java>
     *
     * @param doc document
     * @param address address from which it has been received
     */
    @Override
    protected void parseDocument(Document doc, @NotNull InetAddress address) {
        Node rootElement = doc.getFirstChild();
        String nodeName = rootElement.getNodeName();
        if (nodeName.equalsIgnoreCase("java")) {
            int id = -1;
            int port = -1;

            NodeList nl = doc.getElementsByTagName("id");
            if(nl.getLength() == 1) id = Integer.parseInt(nl.item(0).getTextContent());
            nl = doc.getElementsByTagName("port");
            if(nl.getLength() == 1) port = Integer.parseInt(nl.item(0).getTextContent());
            nl = doc.getElementsByTagName("message");
            if(nl.getLength() == 1) logger.info("Client {} says: {}", id, nl.item(0).getTextContent());
            if (id < 0) {
                id = clientCounter++;
                clients.put(id, new Client(id, address, port));
                logger.info("Client {} has successfully connected!", id);
                sendHi("Hi, welcome to the server dude!", id);
            } else {
                logger.info("Client {} is still alive!", id);
                clients.get(id).updateTimeout();
            }
        } else {
            ModelContainer mc = ModelContainer.fromDocument(doc);
            if (mc != null) callback.onReceive(mc, mc.id);
        }
    }


    /**
     * Client class, holds data about clients, probably will be moved
     */
    public class Client {
        private int id;
        private int port;
        private InetAddress address;
        private long timeOut;

        private Client(int id, InetAddress address, int port) {
            this.id = id;
            this.address = address;
            this.port = port;
            timeOut = System.currentTimeMillis();
        }

        public int getId() { return id; }

        public int getPort() { return port; }

        public InetAddress getAddress() { return address; }

        public long getTimeOut() { return timeOut; }

        public void updateTimeout() {
            timeOut = System.currentTimeMillis();
        }
    }
}
