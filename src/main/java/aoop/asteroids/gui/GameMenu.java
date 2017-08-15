package aoop.asteroids.gui;

import aoop.asteroids.Asteroids;
import aoop.asteroids.controller.InputHandler;
import aoop.asteroids.controller.games.*;
import aoop.asteroids.gui.drawables.Button;
import aoop.asteroids.gui.drawables.GameHighscores;
import aoop.asteroids.gui.drawables.InputBox;
import aoop.asteroids.gui.drawables.Menu;
import aoop.asteroids.model.Player;

import java.awt.*;
import java.util.Random;

public class GameMenu {
    private InputHandler ih;

    private Menu mainMenu;
    private Menu hostMenu;
    private Menu joinMenu;
    private Menu spectateMenu;
    private Menu highscoreMenu;

    public GameMenu() {
        ih = Asteroids.getInstance().getInputhandler();

        mainMenu = new Menu("Asteroids");
        hostMenu = new Menu("Host Game");
        joinMenu = new Menu("Join Game");
        spectateMenu = new Menu("Spectate Game");
        highscoreMenu = new Menu("Highscores");

        mainMenu.addItem(new Button("Singleplayer",   (name) -> {
            setMenu(null);
            startGame(new SinglePlayerGame(ih));
        }));
        mainMenu.addItem(new Button("Host Game",       (name) -> setMenu(hostMenu)));
        mainMenu.addItem(new Button("Join Game",       (name) -> setMenu(joinMenu)));
        mainMenu.addItem(new Button("Spectate Game",   (name) -> setMenu(spectateMenu)));
        mainMenu.addItem(new Button("Highscores",    (name) -> setMenu(highscoreMenu)));


        // ------------- host menu ----------------- //
        Menu.MenuEventCallback hostInputCallback = (item) -> {
            if (item.isSelected()) {
                setMenu(null);
                ih.setKeyListener((InputBox) item);
            }else {
                setMenu(hostMenu);
            }};

        InputBox hostNickName = new InputBox("Nickname", hostInputCallback);
        InputBox hostPort = new InputBox("Port (default:1234)", hostInputCallback, new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});

        hostMenu.addItem(hostNickName);
        hostMenu.addItem(hostPort);

        hostMenu.addItem(new Button("Start", (item) -> {
                int port = (hostPort.getValue().equals(""))?1234:Integer.valueOf(hostPort.getValue());
                setMenu(null);
                startGame(new HostGame(ih, port, new Player(hostNickName.getValue(), randomColor())));
                }));
        hostMenu.addItem(new Button("Back", (item) -> setMenu(mainMenu)));

        // ------------- join menu ----------------- //
        Menu.MenuEventCallback joinInputCallback = (item) -> {
            if (item.isSelected()) {
                setMenu(null);
                ih.setKeyListener((InputBox) item);
            }else {
                setMenu(joinMenu);
            }};

        InputBox joinNickName = new InputBox("Nickname", joinInputCallback);
        InputBox joinHost = new InputBox("Hostname", joinInputCallback);
        InputBox joinPort = new InputBox("Port(default:1234)", joinInputCallback, new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});

        joinMenu.addItem(joinNickName);
        joinMenu.addItem(joinHost);
        joinMenu.addItem(joinPort);

        joinMenu.addItem(new Button("Connect", (item) -> {
            if (joinHost.getValue().equals("") || joinNickName.getValue().equals("")) return;
            int port = (joinPort.getValue().equals(""))? 1234 : Integer.valueOf(joinPort.getValue());
            setMenu(null);
            startGame(new MultiPlayerGame(ih, joinHost.getValue(), port, new Player(joinNickName.getValue(),randomColor())));
        }));
        joinMenu.addItem(new Button("Back", (item) -> setMenu(mainMenu)));


        // ------------- Spectate menu ----------------- //
        Menu.MenuEventCallback spectateInputCallback = (item) -> {
            if (item.isSelected()) {
                setMenu(null);
                ih.setKeyListener((InputBox) item);
            }else {
                setMenu(spectateMenu);
            }};

        InputBox spectateHost = new InputBox("Hostname", spectateInputCallback);
        InputBox spectatePort = new InputBox("Port(default:1234)", spectateInputCallback, new char[] {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'});

        spectateMenu.addItem(spectateHost);
        spectateMenu.addItem(spectatePort);
        spectateMenu.addItem(new Button("Connect", (item) -> {
            if (spectateHost.getValue().equals("")) return;
            int port = (spectatePort.getValue().equals(""))? 1234 : Integer.valueOf(spectatePort.getValue());
            setMenu(null);
            startGame(new SpectateGame(spectateHost.getValue(), port));
        }));
        spectateMenu.addItem(new Button("Back", (item) -> setMenu(mainMenu)));

        // ------------- Highscore menu ----------------- //

        highscoreMenu.addItem(new GameHighscores("Highscores"));
        highscoreMenu.addItem(new Button("Back", (item) -> setMenu(mainMenu)));
    }

    private void startGame(Game game) {
        Asteroids.getInstance().setDrawable(new GameView(game));
        Asteroids.getInstance().startGame(game);
    }

    private void setMenu(Menu m){
        Asteroids.getInstance().getInputhandler().setKeyListener(m);
        if(m != null) {
            Asteroids.getInstance().setDrawable(m);
        }
    }

    private Color randomColor() {
        Random r = new Random(System.currentTimeMillis());
        return new Color(r.nextInt());
    }

    public void show(){
        setMenu(mainMenu);
    }
}
