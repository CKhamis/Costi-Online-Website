package com.costi.csw9.Repository;
import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class WikiDaoImpl implements WikiRepository{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public WikiPage findById(Long id) {
        Session session = sessionFactory.openSession();
        Query query = session.createQuery("SELECT e FROM WikiPage e WHERE id = " + id);
        WikiPage res = (WikiPage) query.getSingleResult();
        // Close session
        session.close();

        return res;
    }

    @Override
    public List<WikiPage> findByCategory(WikiCategory category) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM WikiPage e WHERE category = '" + category.name() + '\'');
        List<WikiPage> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<WikiPage> getByApproval(boolean enabled) {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM WikiPage e WHERE enabled = " + enabled);
        List<WikiPage> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public List<WikiPage> findAll() {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        Query query = session.createQuery("SELECT e FROM WikiPage e");
        List<WikiPage> res = query.getResultList();

        // Close session
        session.close();

        return res;
    }

    // TODO: Make this into a JPA Query, put queries into the interface, then delete this entire class
    @Override
    public void save(WikiPage wikiPage) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.saveOrUpdate(wikiPage);

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
        Query query = session.createQuery("DELETE WikiPage c WHERE c.id = " + id);
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
        Query query = session.createQuery("UPDATE WikiPage c SET c.enabled = " + enable + " WHERE c.id = " + id);
        query.executeUpdate();

        // Commit the transaction
        session.getTransaction().commit();

        // Close session
        session.close();
    }


}
