package aoop.asteroids.network;

import aoop.asteroids.model.ModelContainer;
import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class UDPListener {
    public static final String CLASS_NAME = "aoop.asteroids.ConnectionPackage";
    private final static int RECV_TIMEOUT = 3000;
    private final static int BLOCKSIZE = 512;
    private static final Logger logger  = LoggerFactory.getLogger(UDPListener.class);

    protected DatagramSocket socket;

    public static Document newConnectionXML(HashMap<String, String> values) {
        Element rootElement;
        Element connection;
        Element xmlNode;
        Attr javaVersionAttr;
        Attr javaClassAttr;
        Document doc = null;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        if (doc == null) return null;

        rootElement = doc.createElement("java");
        doc.appendChild(rootElement);
        javaVersionAttr = doc.createAttribute("version");
        javaVersionAttr.setValue(System.getProperty("java.version"));
        rootElement.setAttributeNode(javaVersionAttr);
        javaClassAttr = doc.createAttribute("class");
        javaClassAttr.setValue(CLASS_NAME);
        rootElement.setAttributeNode(javaClassAttr);


        connection = doc.createElement("connection");
        rootElement.appendChild(connection);

        for (Map.Entry<String, String> entry : values.entrySet()) {
            xmlNode = doc.createElement(entry.getKey());
            xmlNode.setTextContent(entry.getValue());
            connection.appendChild(xmlNode);
        }

        return doc;

    }

    protected final int initListener(int port){
        if (port <= 0) {
            for (int i = 49152; i <= 65535; i++){
                try{
                    socket = new DatagramSocket(i);
                    socket.setSoTimeout(RECV_TIMEOUT);
                    return i;
                } catch (BindException e) { logger.info("port in use", e); }
                catch (SocketException e) {
                    logger.error("Could not pick port for server", e);
                    return -1;
                }
            }
        } else {
            try {
                socket = new DatagramSocket(port);
                socket.setSoTimeout(RECV_TIMEOUT);
                logger.info("Listener has been started on port {}", port);
            } catch (SocketException e) {
                logger.error("Could not start server on port {}", port, e);
                return -1;
            }
        }
        return port;
    }

    protected abstract void parseDocument(Document d, @NotNull InetAddress address);

    protected boolean receiveAndParse() {return receiveAndParse(3000); }

    protected boolean receiveAndParse(int timeout) {
        DatagramPacket receive = new DatagramPacket(new byte[BLOCKSIZE], BLOCKSIZE);
        try {
            socket.setSoTimeout(timeout);
            socket.receive(receive);
        }catch (SocketTimeoutException e){
            return false;
        } catch (IOException e) {
            logger.error("Something went wrong when message was received", e);
            return false;
        }
        Package p = Package.fromBytes(Arrays.copyOfRange(receive.getData(), 0,receive.getLength()));
        Package[] packages = new Package[p.maxPackages];
        packages[p.packageNum - 1] = p;
        long time = System.currentTimeMillis() + RECV_TIMEOUT;
        try {
            for(int i = 1; i < packages.length && time > System.currentTimeMillis();) {
                    socket.receive(receive);
                    p = Package.fromBytes(Arrays.copyOfRange(receive.getData(), 0,receive.getLength()));
                    if (p != null){
                        packages[p.packageNum - 1] = p;
                        i++;
                    }
            }
        } catch (SocketException e) {
            logger.error("Not all packages were received", e);
            return false;
        } catch (IOException e) {
            logger.error("Not all packages were received", e);
            return false;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < packages.length; i++) {
            if(packages[i] == null) {
                logger.error("not all packages were received");
                return false;
            }
            sb.append(packages[i].data);
        }
        logger.info("Received {} packages", p.maxPackages);

        logger.info("Parsing data...");
        Document document;
        byte[] data = sb.toString().getBytes();
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(data));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            logger.error("Could not parse package");
            return false;
        }
        parseDocument(document, receive.getAddress());

        return true;
    }

    protected boolean send(Document doc, InetAddress address, int port) {
        Transformer transformer;
        try{
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            logger.error("Could not Create transformer", e);
            return false;
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        try {
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
        } catch (TransformerException e) {
            logger.error("could not transform document", e);
        }
        String output = writer.getBuffer().toString();
        for(Package p : Package.fromString(output)) {
            if (!send(p, address, port)) return false;
        }

        return true;
    }

    private boolean send(Package p, InetAddress address, int port) {
        byte[] sendMessage = p.toString().getBytes();
        DatagramPacket packet = new DatagramPacket(sendMessage, sendMessage.length, address, port);
        try {
            socket.send(packet);
        } catch (IOException e) {
            logger.error("Could not send package :(");
            return false;
        }
        return true;
    }

    private static class Package{
        long timestamp;
        int packageNum;
        int maxPackages;
        String data;

        Package() {}
        Package(long timestamp, String data, int packageNum, int maxPackages){
            this.timestamp = timestamp;
            this.data = data;
            this.packageNum = packageNum;
            this.maxPackages = maxPackages;
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder("[");
            sb.append(timestamp).append('/').append(packageNum).append('/').append(maxPackages).append("]");
            sb.append(data);
            sb.append("[end]");
            return sb.toString();
        }

        public static Package[] fromString(String msg) {
            int splitlength = BLOCKSIZE - 32;
            Package[] packages = new Package[(int)Math.ceil((double)msg.length() / (double)splitlength)];
            long timestamp = System.currentTimeMillis();
            for (int i = 0; i < packages.length; i++) {
                if (i == packages.length - 1) {
                    packages[i] = new Package(timestamp, msg.substring(splitlength * i), i + 1, packages.length);
                } else {
                    packages[i] = new Package(timestamp,
                            msg.substring(splitlength * i, (splitlength * i) + splitlength),
                            i + 1, packages.length);
                }
            }
            return packages;
        }

        private static Package fromBytes(byte[] bytes){
            String s = new String(bytes);
            if (s.charAt(0) != '['){
                logger.error("malformed package, has to start with '[");
                return null;
            }
            Pattern pattern = Pattern.compile("\\[(\\d+)/(\\d+)/(\\d+)\\](\\p{ASCII}+)\\[end\\]");
            Matcher m = pattern.matcher(s);
            m.find();
            Package p = new Package();
            try {
                p.timestamp = Long.valueOf(m.group(1));
                p.packageNum = Integer.valueOf(m.group(2));
                p.maxPackages = Integer.valueOf(m.group(3));
            } catch (Exception e) {
                logger.error("malformed package", e);
                return null;
            }

            p.data = m.group(4);

            return p;
        }
    }






}
