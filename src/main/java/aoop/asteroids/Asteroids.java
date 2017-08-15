package aoop.asteroids;

import aoop.asteroids.controller.DatabaseHandler;
import aoop.asteroids.controller.GameThread;
import aoop.asteroids.controller.InputHandler;
import aoop.asteroids.controller.games.Game;
import aoop.asteroids.gui.AsteroidsFrame;
import aoop.asteroids.gui.Renderer;
import aoop.asteroids.gui.GameMenu;
import aoop.asteroids.gui.drawables.IDrawable;
import aoop.asteroids.model.Asteroid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *	Main class of the Asteroids program.
 *	<p>
 *	Holds objects we don't want to be passed around in constructors all the
 *	time.
 *	</p>
 *	<p>
 *	Asteroids is simple game, in which the player is represented by a small 
 *	spaceship. The goal is to destroy as many asteroids as possible and thus 
 *	survive for as long as possible.
 *
 *	@author Yannick Stoffers
 */
public class Asteroids {

	/** singleton instance */
	private static Asteroids instance;
	/** the frame that holds the different views */
	private AsteroidsFrame frame;
	private GameThread gameThread;
    private Renderer renderer;

	/** the player (Should probably become a generic keyInputHandler)*/
	private InputHandler ih;

	/** Constructs a new instance of the program. */
	private Asteroids () {
		gameThread = new GameThread();
        renderer = new Renderer();
		Thread t = new Thread(gameThread);
		t.start();
		ih = new InputHandler();
		frame = new AsteroidsFrame(ih);
        frame.setPanel(renderer);
	}

	/** Method to get the Asteroids instance
	 *
	 *  @return Singleton instance of Asteroids
	 */
	public static Asteroids getInstance() {
		if (instance == null) instance = new Asteroids();
		return instance;
	}

	/** Method to get the Player
	 *
	 *  @return AsteroidsFrame that holds the different views
	 */
	public InputHandler getInputhandler() { return ih; }

    public void setDrawable(IDrawable drawable){
        renderer.setDrawable(drawable);
    }

    public void stop() {
        gameThread.stop();
        System.exit(0);
    }

    public void startGame(Game g){
        gameThread.startGame(g);
    }

    public void reset() {
        GameMenu gameMenu = new GameMenu();
        gameMenu.show();
    }

    private static final Logger logger = LoggerFactory.getLogger(Asteroids.class);

	/**
	 *	Main function.
	 *
	 *	@param args input arguments.
	 */
	public static void main (String [] args) {
		if (System.getProperty ("os.name").contains ("Mac")) {
			System.setProperty ("apple.laf.useScreenMenuBar", "true");
		}
		logger.info("Application started!");
        Asteroids.getInstance().reset();
	}
	
}
