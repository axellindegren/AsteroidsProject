package aoop.asteroids.gui;

import aoop.asteroids.controller.games.Game;
import aoop.asteroids.gui.drawables.Debug;
import aoop.asteroids.gui.drawables.HUD;
import aoop.asteroids.gui.drawables.IDrawable;
import aoop.asteroids.model.Player;

import java.awt.*;
import java.util.Collection;

/**
 *	GameView extends IDrawable and thus provides the actual graphical
 *	representation of the game model.
 *
 *	@author Yannick Stoffers
 */
public class GameView implements IDrawable {

	/** Game model. */
	private Game game;
    private Renderer renderer;


	/** 
	 *	Constructs a new game panel, based on the given model.
	 *
	 *	@param game game model.
	 */
	public GameView(Game game) {
		this.game = game;
		this.game.addObserver ((o, arg) -> {
            if (renderer != null) renderer.repaint();
		});
	}

	@Override
	public void draw(Graphics2D g) {
		game.getGameObjects().forEach((go)-> go.draw(g));
        new HUD(game.getPlayers()).draw(g);
        {
            Debug debug = new Debug();
            debug.addString("Players", "" + game.getPlayers().size());
            debug.addString("Objects","" + game.getGameObjects().size());
            debug.draw(g);
        }

	}

	@Override
	public void onRendererAttached(Renderer parent) {
        renderer = parent;
        if(renderer != null) renderer.repaint();
	}
}
