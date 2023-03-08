package com.costi.csw9.Repository;

import com.costi.csw9.Model.Post;
import com.costi.csw9.Model.WikiPage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

public class PostDaoImpl implements PostRepository{

    @Autowired
    private SessionFactory sessionFactory;
    @Override
    public Post findById(Long id) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("SELECT e FROM Post e WHERE id = " + id);
        Post res = (Post) query.getSingleResult();
        // Close session
        session.close();

        return res;
    }

    @Override
    public List<Post> findByCategory(String category) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Post e WHERE category = '" + category + "\' AND enabled = true");
        List<Post> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<Post> getByApproval(boolean enabled) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM Post e WHERE enabled = " + enabled);
        List<Post> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public void save(Post post) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.saveOrUpdate(post);

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
        Query query = session.createQuery("DELETE Post c WHERE c.id = " + id);
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
        Query query = session.createQuery("UPDATE Post c SET c.enabled = " + enable + " WHERE c.id = " + id);
        query.executeUpdate();

        // Commit the transaction
        session.getTransaction().commit();

        // Close session
        session.close();
    }
}
