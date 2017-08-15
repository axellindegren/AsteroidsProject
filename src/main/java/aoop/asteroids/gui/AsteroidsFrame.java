package aoop.asteroids.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

/**
 *	AsteroidsFrame is a class that extends JFrame and thus provides a game 
 *	window for the Asteroids game.
 *
 *	@author Yannick Stoffers
 */
public class AsteroidsFrame extends JFrame {

	/** serialVersionUID */
	public static final long serialVersionUID = 1L;

	/** Quit action. */
	private AbstractAction quitAction;

	/** New game action. */
	private AbstractAction newGameAction;

	/** The panel in which the game is painted. */
	private JPanel panel;

	/** 
	 *	Constructs a new Frame
	 *
	 *	@param keyListener key listener that catches the users actions.
	 */
	public AsteroidsFrame (KeyListener keyListener) {

		this.initActions ();

		this.setTitle ("Asteroids");
		this.setSize (800, 800);
		this.addKeyListener (keyListener);

		this.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

		JMenuBar mb = new JMenuBar ();
		JMenu m = new JMenu ("Game");
		mb.add (m);
		m.add (this.quitAction);
		m.add (this.newGameAction);
		this.setJMenuBar (mb);

		this.setVisible (true);

	}


	/** Initializes the quit- and new game action. */
	private void initActions() {
		// Quits the application
		this.quitAction = new AbstractAction ("Quit") 
		{
			public static final long serialVersionUID = 2L;

			@Override
			public void actionPerformed (ActionEvent arg0) 
			{
				System.exit(0);
			}
		};
		
		// Creates a new model
		this.newGameAction = new AbstractAction ("New Game") {
			public static final long serialVersionUID = 3L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				//AsteroidsFrame.this.newGame ();
			}
		};

	}

	public void setPanel(JPanel panel) {
        if (this.panel != null) this.remove(this.panel);
        this.panel = panel;
        this.add(this.panel);
        requestFocus();
		repaint();
	}
	
}
