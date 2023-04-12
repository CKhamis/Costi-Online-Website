package com.costi.csw9.Repository;

import com.costi.csw9.Model.User;
import com.costi.csw9.Model.UserRole;
import com.costi.csw9.Model.WikiPage;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.provider.HibernateUtils;
import org.springframework.stereotype.Repository;

import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UserDaoImpl implements UserRepository{
    @Autowired
    private SessionFactory sessionFactory;

    @Override
    public User findById(Long id) {
        Session session = sessionFactory.openSession();
        User user = session.get(User.class, id);
        session.close();
        return user;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        Session session = sessionFactory.openSession();
        Criteria criteria = session.createCriteria(User.class);
        User user = (User) criteria.add(Restrictions.eq("email", email)).uniqueResult();
        session.close();

        if(user == null){
            return Optional.empty();
        }
        return Optional.of(user);
    }

    @Override
    public List<User> findAll() {
        // Open session
        Session session = sessionFactory.openSession();

        // Get Results
        List<User> res = session.createQuery("from User", User.class).getResultList();

        // Close session
        session.close();

        return res;
    }

    @Override
    public void save(User user) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Block change of role
        try{
            if(!findById(user.getId()).getRole().equals(user.getRole())){
                System.out.println(findById(user.getId()).getRole() + " versus " + user.getRole());
                user.setRole(UserRole.USER);
            }
        }catch (Exception e){

        }

        // Save the person
        session.saveOrUpdate(user);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }

    @Override
    public void delete(User user) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.delete(user);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();

        System.out.println("User was deleted!");
    }

    @Override
    public void enable(Long id, boolean enable) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Get user
        User user = findById(id);

        // Enable
        user.setEnabled(enable);

        //Save
        save(user);

        // Close the session
        session.close();
    }

    @Override
    public void lock(Long id, boolean lock) {
        // Open a session
        Session session = sessionFactory.openSession();

        // Get user
        User user = findById(id);

        // Lock
        user.setIsLocked(lock);

        //Save
        save(user);

        // Close the session
        session.close();
    }

    @Override
    public void demote(User user) {
        // Demote user
        user.setRole(UserRole.USER);

        // Open a session
        Session session = sessionFactory.openSession();

        // Begin a transaction
        session.beginTransaction();

        // Save the person
        session.saveOrUpdate(user);

        // Commit the transaction
        session.getTransaction().commit();

        // Close the session
        session.close();
    }
}
