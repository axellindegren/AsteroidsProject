package aoop.asteroids.model;

import java.awt.*;
import java.io.Serializable;

/**
 *	Player is the controller class that listens to the users input (key events) 
 *	and forwards those events to the spaceship class. Where they will influence 
 *	the behaviour of the spaceship.
 *
 *	@author Yannick Stoffers
 */
public class Player {
	private String nickname;
	private Color color;
	private int score;
	private Spaceship ship;
    private int id;

	public Player(String nickname, Color color) {
		this.nickname = nickname;
		this.color = color;
		this.ship = new Spaceship(this);
        this.id = -1;
		reset();
	}

	public void reset(){
		score = 0;
		ship.reinit();
    }

    public Player clone() {
        Player p = new Player(nickname, color);
        p.ship = ship;
        p.id = id;
        p.score = score;
        return p;
    }

    public int getID() { return id;}
    public void setID(int id) {this.id = id;}

    public void setShip(Spaceship ship) { this.ship = ship; }
	public Spaceship getShip() { return this.ship; }


	public void setNickname(String name) { this.nickname = name; }
	public String getNickname() { return nickname; }

	public Color getColor() { return color; }

    public void setScore(int score) {this.score = score; }
	public int getScore() { return score; }
}
