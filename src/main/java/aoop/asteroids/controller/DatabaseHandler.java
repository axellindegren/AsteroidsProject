package aoop.asteroids.controller;


import aoop.asteroids.model.Highscore;

import javax.persistence.*;
import java.util.Collection;
import java.util.List;

public class DatabaseHandler {

    private EntityManagerFactory emf;
    private EntityManager em;
    private String filename;

    public DatabaseHandler(String filename) {
        this.filename = filename;
    }

    public void openDatabase() {
        emf = Persistence.createEntityManagerFactory("$objectdb/db/"+ filename +".odb");
        em = emf.createEntityManager();
    }

    public void closeDatabase() {
        em.close();
        emf.close();
    }

    public Collection<Highscore> getHighscores() {
        em.getTransaction().begin();
       TypedQuery<Highscore> query =
                em.createQuery("SELECT hs FROM Highscore hs ORDER BY hs.score DESC", Highscore.class);
        em.getTransaction().commit();

        List<Highscore> resultList = query.getResultList();
        return resultList;
    }


    public void storeHighscore(Highscore hs) {
        em.getTransaction().begin();
        em.persist(hs);
        em.getTransaction().commit();
    }

    public Highscore findHighscore(String name) {
        TypedQuery<Highscore> query = em.createQuery(
                "SELECT hs FROM Highscore hs WHERE hs.name = :name", Highscore.class);
        try {
            return query.setParameter("name", name).getSingleResult();
        } catch(NoResultException e) {
            return null;
        }
    }

    public EntityManager getEntityManager() { return em; }

}
