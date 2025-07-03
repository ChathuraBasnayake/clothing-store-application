package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.hibernateUtil.HibernateUtil;
import com.icet.clothify.model.dao.SupplierDAO;
import com.icet.clothify.repository.custom.SupplierRepository;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.sql.SQLException;
import java.util.List;

public class SupplierRepositoryImpl implements SupplierRepository {

    public SupplierRepositoryImpl() throws SQLException {
    }

    @Override
    public boolean add(SupplierDAO dao) throws SQLException {
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

            //noinspection removal
            SupplierDAO supplier = session.get(SupplierDAO.class, id); // load entity

            if (supplier != null) {
                session.remove(supplier); // remove the entity
            } else {
                System.out.println("No supplier found with ID: " + id);
                return false;
            }

            transaction.commit();
            return true;

        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return false;
        }

    }

    @Override
    public boolean update(SupplierDAO dao) throws SQLException {
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
    public SupplierDAO searchById(String id) throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            SupplierDAO supplierDAO = (SupplierDAO) session.byId(id);
            transaction.commit();
            return supplierDAO;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw e;
        }
    }

    @Override
    public List<SupplierDAO> getAll() throws SQLException {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            String hql = "FROM SupplierDAO";
            List<SupplierDAO> list = session.createQuery(hql, SupplierDAO.class).list();
            transaction.commit();
            return list;
        } catch (HibernateException e) {
            transaction.rollback();
            throw e;
        }
    }


}