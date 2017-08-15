package aoop.asteroids.gui.drawables;

import aoop.asteroids.model.DestroyReason;
import aoop.asteroids.model.GameObject;


public class CollisionWithObject extends DestroyReason {

    GameObject gameObject;

    public CollisionWithObject(GameObject gameObject) {
        this.gameObject = gameObject;
    }

    public GameObject getGameObject() {
        return gameObject;
    }
}
