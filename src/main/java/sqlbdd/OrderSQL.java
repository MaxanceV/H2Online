package sqlbdd;

import models.Order;
import models.OrderItem;
import tools.DBconnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderSQL {

    // Méthode pour créer une nouvelle commande "in progress"
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

    // Méthode pour récupérer une commande "in progress" d'un utilisateur
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

                // Charger les articles associés
                OrderItemSQL orderItemDAO = new OrderItemSQL();
                order.setOrderItems(orderItemDAO.getOrderItems(order.getOrderId()));

                return order;
            }
        }
        return null; // Aucune commande "in progress" trouvée
    }

    // Met à jour le total_price d'une commande
    public void updateOrderTotal(int orderId, BigDecimal totalPrice) throws SQLException {
        String query = "UPDATE orders SET total_price = ? WHERE order_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setBigDecimal(1, totalPrice);
            statement.setInt(2, orderId);
            statement.executeUpdate();
        }
    }

    // Change le statut de la commande
    public void updateOrderStatus(int orderId, String status) throws SQLException {
        String query = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, status);
            statement.setInt(2, orderId);
            statement.executeUpdate();
        }
    }
    
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
        return null; // Si aucune commande trouvée
    }
    
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
