package com.costi.csw9.Repository;

import com.costi.csw9.Model.FriendEvent;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class EventDaoImpl implements EventRepository{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public FriendEvent findById(Long id) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("SELECT e FROM Events e WHERE id = " + id);
        FriendEvent res = (FriendEvent) query.getSingleResult();
        // Close session
        session.close();

        return res;
    }

    //TODO: Check if this works at all
    @Override
    public List<FriendEvent> findByOrganizer(Long id) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Events e WHERE organizerId = " + id);
        List<FriendEvent> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<FriendEvent> findByInvited(Long id) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM user_events e WHERE user_id = " + id);
        List<FriendEvent> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<FriendEvent> findByCategory(String category) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Events e WHERE category = '" + category + "\' AND enabled = true");
        List<FriendEvent> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<FriendEvent> getByApproval(boolean isEnabled) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Events e WHERE enabled = " + isEnabled);
        List<FriendEvent> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<FriendEvent> getByUniversal(boolean isUniversal) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Events e WHERE enabled = true AND is_universal = " + isUniversal);
        List<FriendEvent> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<FriendEvent> findAll() {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Events e");
        List<FriendEvent> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public void save(FriendEvent event) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.saveOrUpdate(event);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    @Override
    public void enable(Long id, boolean enable) {
        // Open session
        Session session = sessionFactory.openSession();

        // Begin translation
        session.beginTransaction();

        // Execute update
        Query query = session.createQuery("UPDATE Events c SET c.enabled = " + enable + " WHERE c.id = " + id);
        query.executeUpdate();

        // Commit the transaction
        session.getTransaction().commit();

        // Close session
        session.close();
    }
}
