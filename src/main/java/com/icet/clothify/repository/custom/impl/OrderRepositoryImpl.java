package com.icet.clothify.repository.custom.impl;

import com.icet.clothify.hibernateUtil.HibernateUtil;
import com.icet.clothify.model.dao.OrderDAO;
import com.icet.clothify.repository.custom.OrderRepository;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public List<OrderDAO> getAll() throws SQLException {
        Session session;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();
            // FIX: Re-introduced 'LEFT JOIN FETCH' to solve LazyInitializationException.
            // The 'DISTINCT' keyword prevents duplicate Order results.
            String hql = "SELECT DISTINCT o FROM OrderDAO o LEFT JOIN FETCH o.orderItems";
            Query<OrderDAO> query = session.createQuery(hql, OrderDAO.class);
            List<OrderDAO> orders = query.list();
            tx.commit();
            return orders;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new SQLException("Could not retrieve all orders from the database.", e);
        }
        // NOTE: A session retrieved via getCurrentSession() is automatically closed
        // when the transaction is committed or rolled back. No 'finally' block is needed.
    }


    /**
     * UPDATED METHOD
     * Retrieves sales data grouped by category using the getCurrentSession() pattern.
     */
    @Override
    public Map<String, Double> getSalesByCategory() throws SQLException {
        Session session;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();

            // This HQL query joins OrderItemDAO with an assumed ItemDAO entity.
            String hql = "SELECT i.category, SUM(oi.total) " +
                    "FROM OrderItemDAO oi JOIN ItemDAO i ON oi.itemId = i.id " +
                    "GROUP BY i.category";

            List<Object[]> results = session.createQuery(hql, Object[].class).list();
            tx.commit();

            Map<String, Double> salesByCategory = new HashMap<>();
            for (Object[] result : results) {
                String category = (String) result[0];
                Double totalSales = (Double) result[1];
                if (category != null && totalSales != null) {
                    salesByCategory.put(category, totalSales);
                }
            }
            return salesByCategory;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new SQLException("Could not retrieve sales by category from the database.", e);
        }
    }

    @Override
    public Map<String, Double> getMonthlySales(int monthLimit) throws SQLException {
        Session session;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();

            // 1. Calculate the start date for the query.
            LocalDateTime startDate = LocalDateTime.now().minusMonths(monthLimit - 1).withDayOfMonth(1);

            // 2. Write the HQL query to get sales data.
            String hql = "SELECT YEAR(o.orderDate), MONTH(o.orderDate), SUM(o.total) " +
                    "FROM OrderDAO o " +
                    "WHERE o.orderDate >= :startDate " +
                    "GROUP BY YEAR(o.orderDate), MONTH(o.orderDate) " +
                    "ORDER BY YEAR(o.orderDate), MONTH(o.orderDate)";

            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameter("startDate", startDate);

            List<Object[]> results = query.list();
            tx.commit();

            // 3. Create a map of sales results for easy lookup.
            Map<LocalDate, Double> salesDataMap = results.stream()
                    .collect(Collectors.toMap(
                            result -> LocalDate.of((Integer) result[0], (Integer) result[1], 1), // Key: First day of the month
                            result -> (Double) result[2]  // Value: Total sales
                    ));

            // 4. Generate a complete list of months for the specified range.
            // This ensures months with zero sales are included in the final result.
            Map<String, Double> finalMonthlySales = new LinkedHashMap<>();
            LocalDate today = LocalDate.now();

            IntStream.range(0, monthLimit)
                    .mapToObj(i -> today.minusMonths(i).withDayOfMonth(1)) // Get the first day of each month in the range
                    .sorted() // Sort the dates chronologically
                    .forEach(monthStartDate -> {
                        // Get the sales total for this month from the map, or 0.0 if no sales.
                        Double total = salesDataMap.getOrDefault(monthStartDate, 0.0);

                        // Format the month name to full text (e.g., "July")
                        String monthName = monthStartDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                        String key = monthName + " '" + String.valueOf(monthStartDate.getYear()).substring(2);

                        finalMonthlySales.put(key, total);
                    });

            return finalMonthlySales;

        } catch (Exception e) {
            // If any error occurs, roll back the transaction to prevent an inconsistent state.
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            // Wrap the original exception in an SQLException to notify the service layer.
            throw new SQLException("Could not retrieve monthly sales data.", e);
        }
    }

    @Override
    public List<OrderDAO> getRecentOrders(int limit) throws SQLException {
        Session session;
        Transaction tx = null;
        try {
            session = HibernateUtil.getSessionFactory().getCurrentSession();
            tx = session.beginTransaction();

            String hql = "SELECT DISTINCT o FROM OrderDAO o LEFT JOIN FETCH o.orderItems ORDER BY o.orderDate DESC";

            Query<OrderDAO> query = session.createQuery(hql, OrderDAO.class);
            query.setMaxResults(limit); // Limit the result set to the specified number

            List<OrderDAO> recentOrders = query.list();
            tx.commit();
            return recentOrders;
        } catch (Exception e) {
            if (tx != null && tx.isActive()) {
                tx.rollback();
            }
            throw new SQLException("Could not retrieve recent orders.", e);
        }
    }

}
