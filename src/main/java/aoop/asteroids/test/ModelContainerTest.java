package aoop.asteroids.test;

import aoop.asteroids.model.*;
import com.thoughtworks.xstream.XStream;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.*;


public class ModelContainerTest {

    ModelContainer mc;

    @Before
    public void init(){
        mc = new ModelContainer();
        ArrayList<GameObject> objects = new ArrayList<>();
        Player p = new Player("name", Color.BLACK);
        objects.add(new Spaceship(p));
        objects.add(new Asteroid(new Point(1,2),3.0,4.0,5));
        objects.add(new Asteroid(new Point(2,3),4.0, 5.0,6));
        objects.add(new Bullet(p, new Point(1,2),3.0,4.0));
        mc.gameObjects = objects;
        mc.id = -1;
    }

    @Test
    public void testDocumentConversion() throws Exception {
        Document d1 = mc.getDocument();
        ModelContainer result = ModelContainer.fromDocument(d1);
        Document d2 = result.getDocument();
        d1.normalizeDocument();
        d2.normalizeDocument();
        assertTrue("conversion failed", d2.isEqualNode(d1));
    }


}