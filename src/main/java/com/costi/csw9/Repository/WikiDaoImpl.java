package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Model.WikiCategory;
import com.costi.csw9.Model.WikiPage;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
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

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WikiPage> query = builder.createQuery(WikiPage.class);
        Root<WikiPage> rootEntry = query.from(WikiPage.class);
        CriteriaQuery<WikiPage> all = query.select(rootEntry);
        TypedQuery<WikiPage> allQuery = session.createQuery(all);

        session.close();
        List<WikiPage> pages = allQuery.getResultList();

        //TODO: this might not work!
        for(WikiPage page : pages){
            if(!page.getCategory().equals(category)){
                pages.remove(page);
            }
        }
        return pages;
    }

    @Override
    public List<WikiPage> findAll() {
        Session session = sessionFactory.openSession();

        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<WikiPage> query = builder.createQuery(WikiPage.class);
        Root<WikiPage> rootEntry = query.from(WikiPage.class);
        CriteriaQuery<WikiPage> all = query.select(rootEntry);
        TypedQuery<WikiPage> allQuery = session.createQuery(all);

        session.close();
        return allQuery.getResultList();
    }

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
    public void delete(WikiPage wikiPage) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.delete(wikiPage);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();

        System.out.println("Wiki Page was deleted!");
    }
}
