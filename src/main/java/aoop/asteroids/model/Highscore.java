package aoop.asteroids.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
public class Highscore implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id @GeneratedValue
    private long id;

    private String name;
    private int score;
    
    public Highscore(String name, int score) {
        this.name = name;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    @Override
    public String toString() {
        return String.format("(%d)", this.score);
    }
}

