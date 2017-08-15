package aoop.asteroids.controller;


import aoop.asteroids.controller.games.Game;

public class GameThread implements Runnable{

    private boolean isRunning;
    private Game game;

    public GameThread(){
        isRunning = true;
    }


    public synchronized void startGame(Game game){
        if (this.game != null) this.game.stop();
        this.game = game;
        this.game.start();
    }

    private synchronized void tick() {
        if (game == null) return;
        game.update();
    }

    public synchronized void stop() { isRunning = false; }

    /**
     *	This method allows this object to run in its own thread, making sure
     *	that the same thread will not perform non essential computations for
     *	the game. The thread will not stop running until the program is quit.
     *	If the game is aborted or the player died, it will wait 100
     *	milliseconds before reevaluating and continuing the simulation.
     *	<p>
     *	While the game is not aborted and the player is still alive, it will
     *	measure the time it takes the program to perform a game tick and wait
     *	40 minus execution time milliseconds to do it all over again. This
     *	allows the game to update every 40th millisecond, thus keeping a steady
     *	25 frames per second.
     *	<p>
     *	Decrease waiting time to increase fps. Note
     *	however, that all game mechanics will be faster as well. I.e. asteroids
     *	will travel faster, bullets will travel faster and the spaceship may
     *	not be as easy to control.
     */
    @Override
    public void run() {
        long executionTime, sleepTime;
        while(isRunning) {
                if (game != null) {
                    executionTime = System.currentTimeMillis ();
                    tick();
                    executionTime -= System.currentTimeMillis ();
                    sleepTime = 40 - executionTime;

                } else sleepTime = 100;

                try {
                    Thread.sleep (sleepTime);
                } catch (InterruptedException e) {
                    System.err.println ("Could not perfrom action: Thread.sleep(...)");
                    System.err.println ("The thread that needed to sleep is the game thread, responsible for the game loop (update -> wait -> update -> etc).");
                    e.printStackTrace ();
                }

        }
    }
}
