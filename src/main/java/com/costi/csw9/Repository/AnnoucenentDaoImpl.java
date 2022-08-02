package com.costi.csw9.Repository;

import com.costi.csw9.Model.Announcement;
import com.costi.csw9.Model.WikiPage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class AnnoucenentDaoImpl implements AnnouncementRepository{

    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public Announcement findById(Long id) {
        // Open session
        Session session = sessionFactory.openSession();

        // Generate JPA
        Query query = session.createQuery("SELECT e FROM Announcement e WHERE id = " + id);

        // Execute JPA
        Announcement res = (Announcement) query.getSingleResult();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<Announcement> getByApproval(boolean enabled) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Announcement e WHERE enabled = " + enabled);

        // Execute JPA
        List<Announcement> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<Announcement> findAll() {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Announcement e");

        // Execute JPA
        List<Announcement> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public void save(Announcement announcement) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.saveOrUpdate(announcement);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    @Override
    public void delete(Long id) {
        // Open session
        Session session = sessionFactory.openSession();

        // Begin translation
        session.beginTransaction();

        // Execute update
        Query query = session.createQuery("DELETE Announcement c WHERE c.id = " + id);
        query.executeUpdate();

        // Commit the transaction
        session.getTransaction().commit();

        // Close session
        session.close();
    }

    @Override
    public void enable(Long id, boolean enable) {
        // Open session
        Session session = sessionFactory.openSession();

        // Begin translation
        session.beginTransaction();

        // Execute update
        Query query = session.createQuery("UPDATE Announcement c SET c.enabled = " + enable + " WHERE c.id = " + id);
        query.executeUpdate();

        // Commit the transaction
        session.getTransaction().commit();

        // Close session
        session.close();
    }
}
