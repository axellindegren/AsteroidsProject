package aoop.asteroids.model;


import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.ConversionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ModelContainer implements Serializable {
    private static transient final Logger logger = LoggerFactory.getLogger(ModelContainer.class);

    public ArrayList<Player> players;
    public ArrayList<GameObject> gameObjects;
    public long timeStamp;
    public int id;


    public Document getDocument() {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XStream xstream = new XStream();
        xstream.toXML(this,out);
        Document d = null;
        try {
            d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream(out.toByteArray()));
        } catch (SAXException | ParserConfigurationException | IOException e) {
            logger.error("Could not parse ModelContainer to document", e);
        }
        return d;
    }

    public static ModelContainer fromDocument(Document d) {
        Transformer transformer;
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            logger.error("Could not create transformer", e);
            return null;
        }
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        try{
            transformer.transform(new DOMSource(d), new StreamResult(writer));
        } catch (TransformerException e) {
            logger.error("Could not transform", e);
            return null;
        }
        XStream xstream = new XStream();
        Object o;
        try {
            o = xstream.fromXML(writer.getBuffer().toString());
        } catch (ConversionException e) {
            logger.error("could not convert", e);
            return null;
        }
        if (o instanceof ModelContainer) return (ModelContainer)o;
        logger.error("Invalid object");
        return null;
    }
}
