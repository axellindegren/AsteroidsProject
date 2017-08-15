package aoop.asteroids.model;


public class OutOfSteps extends DestroyReason {
    GameObject gameObject;

    public OutOfSteps(GameObject gameObject) {
        this.gameObject = gameObject;
    }

}
