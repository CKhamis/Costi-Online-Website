package com.costi.csw9.Repository;

import com.costi.csw9.Model.AccountLog;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AccountLogDaoImpl implements AccountLogRepository {
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public AccountLog findById(Long id) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("SELECT e FROM Account_Logs e WHERE id = " + id);
        AccountLog res = (AccountLog) query.getSingleResult();
        // Close session
        session.close();

        return res;
    }

    @Override
    public List<AccountLog> findByUser(Long id) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM AccountLog e WHERE user = " + id);
        List<AccountLog> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public void save(AccountLog log) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.saveOrUpdate(log);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
}
