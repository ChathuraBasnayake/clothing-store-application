package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.hibernateUtil.HibernateUtil;
import com.icet.clothify.model.dao.ItemDAO;
import com.icet.clothify.repository.custom.ItemRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;

public class ItemRepositoryImpl implements ItemRepository {

    public ItemRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(ItemDAO dao) {
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
    public boolean delete(String id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            session.remove(session.byId(id));
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
    public boolean update(ItemDAO dao) {
        Transaction transaction = null;
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
    public ItemDAO searchById(String id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            ItemDAO itemDAO = (ItemDAO) session.byId(id);
            transaction.commit();
            return itemDAO;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<ItemDAO> getAll() {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM ItemDAO";
            List<ItemDAO> list = session.createQuery(hql, ItemDAO.class).list();
            transaction.commit();
            return list;
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
    }

}