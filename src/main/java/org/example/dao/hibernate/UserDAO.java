package org.example.dao.hibernate;

import org.example.authenticate.Authenticator;
import org.example.dao.IUserRepository;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.Collection;

public class UserDAO implements IUserRepository {
    private static UserDAO instance;
    SessionFactory sessionFactory;
    @Override
    public User getUser(String login) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.get(User.class, login);
            transaction.commit();
            return user;
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            return null;
        } finally {
            session.close();
        }
    }

    @Override
    public void addUser(User user) {
            Session session = sessionFactory.openSession();
            Transaction transaction = null;
            try {
                transaction = session.beginTransaction();
                user.setPassword(Authenticator.hashPassword(user.getPassword()));// test
                session.persist(user);
                transaction.commit();
            } catch (RuntimeException e) {
                if (transaction != null) {
                    transaction.rollback();
                }
                e.printStackTrace();
            } finally {
                session.close();
            }
    }

    @Override
    public void removeUser(String login) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            User user = session.get(User.class, login);
            if (user != null && user.getVehicle()==null) {
                session.remove(user);
            } else {
                return;
            }
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }

    @Override
    public Collection<User> getUsers() {
        Collection<User> users;
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            users = session.createQuery("FROM User", User.class).getResultList();
            transaction.commit();
        } catch (RuntimeException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        } finally {
            session.close();
        }
        return users;
    }

    private UserDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    public static UserDAO getInstance(SessionFactory sessionFactory) {
        if (instance == null) {
            instance = new UserDAO(sessionFactory);
        }
        return instance;
    }
}
