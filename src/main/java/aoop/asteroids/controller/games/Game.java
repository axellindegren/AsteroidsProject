package aoop.asteroids.controller.games;

import aoop.asteroids.controller.InputHandler;
import aoop.asteroids.gui.drawables.CollisionWithObject;
import aoop.asteroids.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.*;

/**
 *	The game class is the backbone of all simulations of the asteroid game. It 
 *	contains all game object and keeps track of some other required variables 
 *	in order to specify game rules.
 *	<p>
 *	The game rules are as follows:
 *	<ul>
 *		<li> All game objects are updated according to their own rules every 
 *			game tick. </li>
 *		<li> Every 200th game tick a new asteroid is spawn. An asteroid cannot 
 *			spawn within a 50 pixel radius of the player. </li>
 *		<li> There is a maximum amount of asteroids that are allowed to be 
 *			active simultaneously. Asteroids that spawn from destroying a 
 *			larger asteroid do count towards this maximum, but are allowed to 
 *			spawn if maximum is exceeded. </li>
 *		<li> Destroying an asteroid spawns two smaller asteroids. I.e. large 
 *			asteroids spawn two medium asteroids and medium asteroids spawn two 
 *			small asteroids upon destruction. </li>
 *		<li> The player dies upon colliding with either a buller or an 
 *			asteroid. </li>
 *		<li> Destroying every 5th asteroid increases the asteroid limit by 1, 
 *			increasing the difficulty. </li>
 *	</ul>
 *	<p>
 *	This class implements Runnable, so all simulations will be run in its own 
 *	thread. This class extends Observable in order to notify the view element 
 *	of the program, without keeping a reference to those objects.
 *
 *	@author Yannick Stoffers
 */
public abstract class Game extends Observable {
    private static Logger logger = LoggerFactory.getLogger(Game.class);

	/** All players of game */
	private Map<Integer,Player> players;

    /** All game objects */
	protected Collection<GameObject> objects;

	/** Random number generator. */
	private static Random rng;

	/** Game tick counter for spawning random asteroids. */
	private int cycleCounter;

	/** Asteroid limit. */
	private int asteroidsLimit;
    private int asteroidsCount;

	/** Initializes a new game from scratch. */
	public Game () {
		players = new HashMap<>();
		Game.rng = new Random ();
		this.reset();
	}

	/** Sets all game data to hold the values of a new game. */
	public void reset() {
		this.cycleCounter = 0;
		this.asteroidsLimit = 7;
        this.objects = new ArrayList<>();
        players.forEach((k, p)-> {p.reset(); objects.add(p.getShip());});
	}

	public Collection<Player> getPlayers() {
        Collection<Player> c = new ArrayList<>();
        players.forEach((k, p) -> c.add(p.clone()));
        return c;
    }

	public Collection<GameObject> getGameObjects() {
		Collection<GameObject> c = new ArrayList<>();
		objects.forEach((o) -> c.add(o.clone()));
		return c;
	}

	protected synchronized void put(int id, Player p) {
        players.put(id, p);
    }

	public synchronized void add(GameObject go) {
        objects.add(go);
    }

    protected synchronized void updatePlayerList(Collection<Player> newPlayers){
        Map<Integer, Player> m = new HashMap<>();
        newPlayers.forEach((p)-> m.put(p.getID(), p));
        updatePlayerList(m);
    }

    protected synchronized void updatePlayerList(Map<Integer, Player> newPlayers){
        Map<Integer, Player> m = new HashMap<>();
        newPlayers.forEach((k,p) -> {
            if (players.containsKey(k)) {
                Spaceship ship = players.get(k).getShip();
                players.get(k).setScore(p.getScore());
                players.get(k).setNickname(p.getNickname());
                p.getShip().setOwner(players.get(k));
                players.get(k).setShip(p.getShip());
                objects.remove(ship);
                if (!p.getShip().isDestroyed() && !ship.isDestroyed()) {
                    objects.add(p.getShip());
                } else {
                    p.getShip().destroy(new DestroyReason() {});
                }
                logger.info("Ship {} destroyed : {}", k, p.getShip().isDestroyed() || ship.isDestroyed());
            } else {
                m.put(k,p);
            }
        });
        players.forEach((k,v) -> {
            if (!m.containsKey(k)) m.put(k,v);
        });
        players = m;
    }

    public synchronized void remove(int id) {
        Player p = players.get(id);
        players.remove(id);
        objects.remove(p.getShip());
    }

    protected void onGameOver(){
        reset();
    }

    public abstract boolean isOver();

	/**
	 *	Method invoked at every game tick. It updates all game objects first. 
	 *	Then it adds a bullet if the player is firing. Afterwards it checks all 
	 *	objects for collisions and removes the destroyed objects. Finally the 
	 *	game tick counter is updated and a new asteroid is spawn upon every 
	 *	200th game tick.
	 */
	public void update () {
        if (isOver()) onGameOver();
        Collection<GameObject> cue = new ArrayList<>();
        for (GameObject object : objects) {
            object.nextStep(cue);
            objects.forEach((o) -> {
                if(o.collides(object)){
                    o.destroy(new CollisionWithObject(object));
                    object.destroy(new CollisionWithObject(o));
                }
            });
        }
        objects.addAll(cue);

		this.removeDestroyedObjects ();

		if (this.cycleCounter == 0 && asteroidsCount < this.asteroidsLimit) this.addRandomAsteroid ();
		this.cycleCounter++;
		this.cycleCounter %= 200;

		this.setChanged ();
		this.notifyObservers ();
	}

	/** 
	 *	Adds a randomly sized asteroid at least 50 pixels removed from the 
	 *	player.
	 */
	private void addRandomAsteroid () {
        if (players.size() <= 0) return;
		int prob = Game.rng.nextInt (3000);
        ArrayList<Point> ships = new ArrayList<>();
        players.forEach((k, p) -> ships.add(p.getShip().getLocation()));
		Point loc;
		int x, y;

        boolean failed;
		do {
			loc = new Point (Game.rng.nextInt (800), Game.rng.nextInt (800));
            failed = false;
            for (Map.Entry<Integer, Player> p : players.entrySet()) {
                x = loc.x - p.getValue().getShip().getLocation().x;
                y = loc.y - p.getValue().getShip().getLocation().y;
                if (Math.sqrt (x * x + y * y) >= 50) failed = true;
            }
		} while (!failed);

		if (prob < 1000)		add (new LargeAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
		else if (prob < 2000)	add (new MediumAsteroid (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
		else					add (new SmallAsteroid  (loc, Game.rng.nextDouble () * 6 - 3, Game.rng.nextDouble () * 6 - 3));
	}


	/**
	 *	Removes all destroyed objects. Destroyed asteroids increase the score 
	 *	and spawn two smaller asteroids if it wasn't a small asteroid. New 
	 *	asteroids are faster than their predecessor and travel in opposite 
	 *	direction.
	 */
	private void removeDestroyedObjects () {
		Collection <GameObject> obj = new ArrayList <> ();
        for(GameObject o : objects){
            if(!o.isDestroyed()){
                obj.add(o);
            } else {
                if (o instanceof Asteroid) {
                    Collection<GameObject> successor = ((Asteroid) o).getSuccessors();
                    obj.addAll(successor);
                }
            }
        }
        objects = obj;
	}

	protected static void updatePlayer(Player player, InputHandler ih) {
		player.getShip().setUp((ih.isPressed(KeyEvent.VK_UP)));
		player.getShip().setLeft((ih.isPressed(KeyEvent.VK_LEFT)));
		player.getShip().setRight((ih.isPressed(KeyEvent.VK_RIGHT)));
		player.getShip().setIsFiring((ih.isPressed(KeyEvent.VK_SPACE)));
	}


	public abstract void start();

	public abstract void stop();
}
