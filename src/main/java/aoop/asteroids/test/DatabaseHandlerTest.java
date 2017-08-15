package aoop.asteroids.test;

import aoop.asteroids.controller.DatabaseHandler;
import aoop.asteroids.model.Highscore;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


public class DatabaseHandlerTest {

    private DatabaseHandler databaseHandler;
    private Collection<Highscore> highscoreList;


    @Before
    public void init() throws Exception {
        databaseHandler = new DatabaseHandler("test");
        databaseHandler.openDatabase();
    }

    @After
    public void closeConnection() {
        databaseHandler.closeDatabase();
    }


    @Test
    public void storeHighscore() throws Exception {
        Random rand = new Random(20000000);
        String name = "" + rand.nextLong();
        Highscore hs = new Highscore(name, 2000);
        databaseHandler.storeHighscore(hs);
        Highscore asd = databaseHandler.findHighscore(name);
        assertNotNull(asd);
    }

    @Test
    public void getHighscores() throws Exception {
        highscoreList = databaseHandler.getHighscores();
        assertNotNull(highscoreList); //"No highscores were retrieved", 0 , highscoreList.size());
    }


}