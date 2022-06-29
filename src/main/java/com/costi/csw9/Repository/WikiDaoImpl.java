package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public class WikiDaoImpl implements WikiRepository{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public WikiPage findById(Long id) {
        Session session = sessionFactory.openSession();
        WikiPage wikiPage = session.get(WikiPage.class, id);
        session.close();
        return wikiPage;
    }

    @Override
    public List<WikiPage> findByCategory(WikiCategory category) {
        Session session = sessionFactory.openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<WikiPage> cr = cb.createQuery(WikiPage.class);
        Root<WikiPage> root = cr.from(WikiPage.class);
        cr.select(root);

        cr.select(root).where(cb.like(root.get("category"), category.name()));

        Query<WikiPage> query = session.createQuery(cr);
        List<WikiPage> results = query.getResultList();
        
        return results;
    }

    @Override
    public List<WikiPage> getByApproval(boolean enabled) {
        Session session = sessionFactory.openSession();

        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<WikiPage> cr = cb.createQuery(WikiPage.class);
        Root<WikiPage> root = cr.from(WikiPage.class);
        cr.select(root);

        if(enabled){
            cr.select(root).where(cb.isTrue(root.get("enabled")));
        }else{
            cr.select(root).where(cb.isFalse(root.get("enabled")));
        }

        Query<WikiPage> query = session.createQuery(cr);
        List<WikiPage> results = query.getResultList();

        return results;
    }

    @Override
    public List<WikiPage> findAll() {
        // Open a session
        Session session = sessionFactory.openSession();

        // Get all people with a Hibernate criteria
        List<WikiPage> all = session.createCriteria(WikiPage.class).list();

        // Close session
        session.close();

        return all;
    }

    @Override
    public void save(WikiPage wikiPage) {
        //Add in last edited
        wikiPage.setLastEdited(LocalDateTime.now());

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
    public void delete(WikiPage wikiPage) {
        // Open the session
        Session session = sessionFactory.openSession();

        // Not completley sure why I have to do this, but I need to find the page via id in this function
        WikiPage page = session.get(WikiPage.class, wikiPage.getId());

        // Begin translation
        session.beginTransaction();

        // Delete Page
        session.delete(page);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
}
