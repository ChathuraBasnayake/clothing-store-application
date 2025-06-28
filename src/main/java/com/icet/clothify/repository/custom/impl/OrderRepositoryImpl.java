package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.hibernateUtil.HibernateUtil;
import com.icet.clothify.model.dao.OrderDAO;
import com.icet.clothify.repository.custom.OrderRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;

public class OrderRepositoryImpl implements OrderRepository {

    public OrderRepositoryImpl() {
    }

    @Override
    public boolean delete(String id) {
        return false;
    }

    @Override
    public boolean add(OrderDAO dao) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().getCurrentSession()) {
            transaction = session.beginTransaction();
            session.merge(dao);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    rollbackEx.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(OrderDAO dao) {

        return false;
    }

    @Override
    public OrderDAO searchById(String id) {
        return null;
    }

    @Override
    public List<OrderDAO> getAll() {
        return null;
    }
}
