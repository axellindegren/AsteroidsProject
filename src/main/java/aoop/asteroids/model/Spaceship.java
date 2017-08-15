package aoop.asteroids.model;

import aoop.asteroids.gui.Renderer;

import java.awt.*;
import java.util.Collection;

/**
 *	This class represents the player in the Asteroids game. A spaceship is able 
 *	to shoot every 20 game ticks (twice per second). 
 *	<p>
 *	A massive difference with other game objects is that the spaceship has a 
 *	certain direction in which it is pointed. This influences the way it is 
 *	drawn and determines the direction of spawned bullets. Although the game 
 *	setting is in outer space, the spaceship is subject to traction and will 
 *	slowly deaccelerate.
 *
 *	@author Yannick Stoffers
 */
public class Spaceship extends GameObject {

	/** Direction the spaceship is pointed in. */
	private double direction;

	/** Amount of game ticks left, until the spaceship can fire again. */
	private int stepsTilFire;

	/** Score of the player. I.e. amount of destroyed asteroids. */
	private int score;

	/** Indicates whether the fire button is pressed. */
	private boolean isFiring;

	/** Indicates whether the accelerate button is pressed. */
	private boolean up;

	/** Indicates whether the turn right button is pressed. */
	private boolean right;

	/** Indicates whether the turn left button is pressed. */
	private boolean left;

    private Player player;


	/** Constructs a new spaceship with default values. */
	public Spaceship (Player p) {
		this (p, new Point (400, 400), 0, 0, 15, 0, false, 0);
	}

	/**
	 *	Constructs a new spaceship using all specified information. Fields that 
	 *	do not have a parameter are initialized to default values. This 
	 *	constructor is primarily used for cloning a spaceship. All parameters 
	 *	are important to access from a cloned instance.
	 *
	 *	@param location location of the spaceship.
	 *	@param velocityX velocity in X direction.
	 *	@param velocityY velocity in Y direction.
	 *	@param radius radius.
	 *	@param direction direction.
	 *	@param up indicator for accelarating button.
	 *	@param score score.
	 */
	private Spaceship (Player player, Point location, double velocityX, double velocityY, int radius, double direction, boolean up, int score) {
		super (location, velocityX, velocityY, radius);
        this.player         = player;
		this.direction 		= direction;
		this.up 			= up;
		this.isFiring 		= false;
		this.left 			= false;
		this.right 			= false;
		this.stepsTilFire 	= 0;
		this.score			= score;
	}

	/** 
	 *	Resets all parameters to default values, so a new game can be started. 
	 */
	public void reinit () {
		this.locationX 		= 400;
		this.locationY 		= 400;
		this.velocityX 		= 0;
		this.velocityY 		= 0;
		this.direction 		= 0;
		this.up 			= false;
		this.isFiring 		= false;
		this.left 			= false;
		this.right 			= false;
		this.destroyed		= false;
		this.stepsTilFire 	= 0;
		this.score 			= 0;
	}

	/**
	 *	Sets the isFiring field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setIsFiring (boolean b)
	{
		this.isFiring = b;
	}

	/**
	 *	Sets the left field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setLeft (boolean b)
	{
		this.left = b;
	}

	/**
	 *	Sets the right field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setRight (boolean b)
	{
		this.right = b;
	}

	/**
	 *	Sets the up field to the specified value.
	 *
	 *	@param b new value of the field.
	 */
	public void setUp (boolean b)
	{
		this.up = b;
	}	

	/**
	 *	Defines the behaviour of the spaceship. In each game tick the ship 
	 *	turns when a turn button is pressed. The speed at which it turns is 2% 
	 *	of a full rotation per game tick. Afterwards the spaceships velocity 
	 *	will be updated if the player wants to accelerate. The velocity however 
	 *	is resticted to 10 pixels per game tick in both X and Y direction. 
	 *	Afterwards the location of the ship will be updated and the velocity 
	 *	decreased to account for traction.
	 */
	@Override 
	public void nextStep (Collection<GameObject> cue) {
		this.stepsTilCollide = Math.max (0, this.stepsTilCollide - 1);
		
		// Update direction if turning.
		if (this.left ) this.direction -= 0.04 * Math.PI;
		if (this.right) this.direction += 0.04 * Math.PI;
		if (this.up) {
            // Update speed if accelerating, but constrain values.
			this.velocityX = Math.max (-10, Math.min (10, this.velocityX + Math.sin (direction) * 0.4));
			this.velocityY = Math.max (-10, Math.min (10, this.velocityY - Math.cos (direction) * 0.4));
		}

		// Update location.
		this.locationX = (800 + this.locationX + this.velocityX) % 800;
		this.locationY = (800 + this.locationY + this.velocityY) % 800;

		// Decrease speed due to traction.
		this.velocityX *= 0.99;
		this.velocityY *= 0.99;

		// Decrease firing step counter.
		if (this.stepsTilFire != 0)
			this.stepsTilFire--;

		if(isFiring()) {
            double direction = getDirection ();
            cue.add(new Bullet(player, getLocation (), getVelocityX () + Math.sin (direction) * 15, getVelocityY () - Math.cos (direction) * 15));
            setFired ();
        }
	}

	/**
	 *	Returns a copy of the spaceship. Note that only interesting fields are 
	 *	copied into the clone.
	 *
	 *	@return an exact copy of the spaceship.
	 */
	public Spaceship clone () {
		return new Spaceship (player, this.getLocation (), this.velocityX, this.velocityY, this.radius, this.direction, this.up, this.score);
	}

	/**
	 *	Returns current direction.
	 *
	 *	@return the direction.
	 */
	public double getDirection ()
	{
		return this.direction;
	}

	/**
	 *	Returns whether the spaceship is accelerating.
	 *
	 *	@return true if up button is pressed, false otherwise.
	 */
	public boolean isAccelerating ()
	{
		return this.up;
	}

	/**
	 *	Returns whether the spaceship is firing.
	 *
	 *	@return true if the spacehip is firing, false otherwise.
	 */
	public boolean isFiring ()
	{
		return this.isFiring && this.stepsTilFire == 0;
	}

	/**
	 *	Sets the fire tick counter to 20. For the next 20 game ticks this 
	 *	spaceship is not allowed to fire.
	 */
	public void setFired ()
	{
		this.stepsTilFire = 20;
	}

	/** Increments score field. */
	public void increaseScore ()
	{
		this.score++;
	}

	/**
	 *	Returns current score.
	 *
	 *	@return the score.
	 */
	public int getScore () {
		return this.score;
	}

	@Override
	public void draw(Graphics2D g) {
		// Draw body of the spaceship
		Polygon p = new Polygon ();
		p.addPoint ((int)(getLocation ().x + Math.sin (getDirection ()				  ) * 20), (int)(getLocation ().y - Math.cos (getDirection ()				 ) * 20));
		p.addPoint ((int)(getLocation ().x + Math.sin (getDirection () + 0.8 * Math.PI) * 20), (int)(getLocation ().y - Math.cos (getDirection () + 0.8 * Math.PI) * 20));
		p.addPoint ((int)(getLocation ().x + Math.sin (getDirection () + 1.2 * Math.PI) * 20), (int)(getLocation ().y - Math.cos (getDirection () + 1.2 * Math.PI) * 20));

		g.setColor (player.getColor());
		g.fill (p);
		g.setColor (Color.WHITE);
		g.draw (p);

		// Spaceship accelerating -> continue, otherwise abort.
		if (!isAccelerating ()) return;

		// Draw flame at the exhaust
		p = new Polygon ();
		p.addPoint ((int)(getLocation ().x - Math.sin (getDirection ()				  ) * 25), (int)(getLocation ().y + Math.cos (getDirection ()				 ) * 25));
		p.addPoint ((int)(getLocation ().x + Math.sin (getDirection () + 0.9 * Math.PI) * 15), (int)(getLocation ().y - Math.cos (getDirection () + 0.9 * Math.PI) * 15));
		p.addPoint ((int)(getLocation ().x + Math.sin (getDirection () + 1.1 * Math.PI) * 15), (int)(getLocation ().y - Math.cos (getDirection () + 1.1 * Math.PI) * 15));
		g.setColor(Color.yellow);
		g.fill(p);
	}

	@Override
	public void onRendererAttached(Renderer parent) {

	}

    public void setOwner(Player owner) {
        this.player = owner;
    }
}
