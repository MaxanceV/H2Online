/**
 * Data Access Object for managing orders in the database.
 * This class handles all database operations related to orders including creation,
 * retrieval, status updates, and total price calculations.
 */
package sqlbdd;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Order;
import tools.DBconnection;

public class OrderSQL {

    /**
     * Creates a new order with 'in progress' status for a specific user.
     * Initializes the order with a total price of 0.00.
     *
     * @param userId The ID of the user creating the order
     * @return The ID of the newly created order
     * @throws SQLException If a database access error occurs
     */
    public int createNewOrder(int userId) throws SQLException {
        String query = "INSERT INTO orders (user_id, status, total_price) VALUES (?, 'in progress', 0.00)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, userId);
            statement.executeUpdate();

            ResultSet keys = statement.getGeneratedKeys();
            if (keys.next()) {
                return keys.getInt(1); 
            } else {
                throw new SQLException("Failed to retrieve the generated order ID.");
            }
        }
    }

    /**
     * Retrieves the current 'in progress' order for a specific user.
     * Includes all associated order items.
     *
     * @param userId The ID of the user
     * @return The Order object if found, null otherwise
     * @throws SQLException If a database access error occurs
     */
    public Order getInProgressOrder(int userId) throws SQLException {
        String query = "SELECT * FROM orders WHERE user_id = ? AND status = 'in progress'";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Order order = new Order();
                order.setOrderId(resultSet.getInt("order_id"));
                order.setUserId(resultSet.getInt("user_id"));
                order.setOrderDate(resultSet.getTimestamp("order_date").toLocalDateTime());
                order.setStatus(resultSet.getString("status"));
                order.setTotalPrice(resultSet.getBigDecimal("total_price"));
                order.setPaymentMethod(resultSet.getString("payment_method"));

                OrderItemSQL orderItemDAO = new OrderItemSQL();
                order.setOrderItems(orderItemDAO.getOrderItems(order.getOrderId()));

                return order;
            }
        }
        return null;
    }

    /**
     * Updates the total price of an order.
     *
     * @param orderId The ID of the order to update
     * @param totalPrice The new total price
     * @throws SQLException If a database access error occurs
     */
    public void updateOrderTotal(int orderId, BigDecimal totalPrice) throws SQLException {
        String query = "UPDATE orders SET total_price = ? WHERE order_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, totalPrice);
            statement.setInt(2, orderId);
            statement.executeUpdate();
        }
    }

    /**
     * Updates the status of an order.
     *
     * @param orderId The ID of the order to update
     * @param status The new status
     * @throws SQLException If a database access error occurs
     */
    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String query = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, orderId);
            statement.executeUpdate();
        }
    }
    
    /**
     * Retrieves an order by its ID.
     *
     * @param orderId The ID of the order to retrieve
     * @return The Order object if found, null otherwise
     * @throws SQLException If a database access error occurs
     */
    public Order getOrderById(int orderId) throws SQLException {
        String query = "SELECT * FROM orders WHERE order_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    order.setStatus(rs.getString("status"));
                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    return order;
                }
            }
        }
        return null;
    }
    
    /**
     * Retrieves all orders for a specific user, ordered by date descending.
     *
     * @param userId The ID of the user
     * @return List of orders associated with the user
     * @throws SQLException If a database access error occurs
     */
    public List<Order> getOrdersByUser(int userId) throws SQLException {
        String query = "SELECT * FROM orders WHERE user_id = ? ORDER BY order_date DESC";
        List<Order> orders = new ArrayList<>();
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Order order = new Order();
                    order.setOrderId(rs.getInt("order_id"));
                    order.setUserId(rs.getInt("user_id"));
                    order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    order.setStatus(rs.getString("status"));
                    order.setTotalPrice(rs.getBigDecimal("total_price"));
                    order.setPaymentMethod(rs.getString("payment_method"));
                    orders.add(order);
                }
            }
        }
        return orders;
    }

    /**
     * Retrieves all orders from the database.
     *
     * @return List of all orders in the system
     * @throws SQLException If a database access error occurs
     */
    public List<Order> getAllOrders() throws SQLException {
        String query = "SELECT * FROM orders";
        List<Order> orders = new ArrayList<>();

        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                order.setStatus(rs.getString("status"));
                order.setTotalPrice(rs.getBigDecimal("total_price"));
                order.setPaymentMethod(rs.getString("payment_method"));

                orders.add(order);
            }
        }

        return orders;
    }
}