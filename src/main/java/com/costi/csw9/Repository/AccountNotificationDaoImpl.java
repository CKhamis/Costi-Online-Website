package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountNotification;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountNotificationDaoImpl implements AccountNotificationRepository{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public AccountNotification findById(Long id) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("SELECT e FROM Account_notifications e WHERE id = " + id);
        AccountNotification res = (AccountNotification) query.getSingleResult();
        // Close session
        session.close();

        return res;
    }

    @Override
    public List<AccountNotification> findByUser(Long id) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Account_notifications e WHERE user_id = " + id);
        List<AccountNotification> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public void delete(Long id) {
        // Open session
        Session session = sessionFactory.openSession();

        // Begin translation
        session.beginTransaction();

        // Execute update
        Query query = session.createQuery("DELETE Account_notifications c WHERE c.id = " + id);
        query.executeUpdate();

        // Commit the transaction
        session.getTransaction().commit();

        // Close session
        session.close();
    }

    @Override
    public void save(AccountNotification notification) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.saveOrUpdate(notification);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
}
