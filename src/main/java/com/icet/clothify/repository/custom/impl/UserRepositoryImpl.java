package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.hibernateUtil.HibernateUtil;
import com.icet.clothify.model.dao.UserDAO;
import com.icet.clothify.repository.custom.UserRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {


    public UserRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(UserDAO dao) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            session.persist(dao);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(String id) throws SQLException {
        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            session.remove(session.find(UserDAO.class, id));
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(UserDAO dao) throws SQLException {
        Transaction transaction = null;


        UserDAO userDAO = searchById(dao.getId());

        System.out.println(userDAO);

        dao.setPassword(userDAO.getPassword());

        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            session.merge(dao);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<UserDAO> getAll() throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM UserDAO";
            List<UserDAO> list = session.createQuery(hql, UserDAO.class).list();
            transaction.commit();
            return list;
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
    }

    @Override
    public UserDAO searchById(String id) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            //noinspection removal
            UserDAO userDAO = session.get(UserDAO.class, id); // correct
            transaction.commit();
            return userDAO;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public UserDAO searchByEmail(String email) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM UserDAO WHERE email = :email";
            UserDAO userDAO = session.createQuery(hql, UserDAO.class)
                    .setParameter("email", email)
                    .uniqueResult();
            transaction.commit();
            return userDAO;
        } catch (HibernateException e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }
}