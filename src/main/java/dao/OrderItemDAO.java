package dao;

import models.OrderItem;
import tools.DBconnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemDAO {

    // Ajoute un nouvel article à une commande
    public void addOrderItem(OrderItem item) throws SQLException {
        String query = "INSERT INTO orderitems (order_id, product_id, quantity, unit_price, subtotal_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, item.getOrderId());
            statement.setInt(2, item.getProductId());
            statement.setInt(3, item.getQuantity());
            statement.setBigDecimal(4, item.getUnitPrice());
            statement.setBigDecimal(5, item.getSubtotalPrice());
            statement.executeUpdate();
        }
    }

    // Récupère tous les articles associés à une commande
    public List<OrderItem> getOrderItems(int orderId) throws SQLException {
        String query = "SELECT * FROM orderitems WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderId);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                OrderItem item = new OrderItem();
                item.setOrderItemId(resultSet.getInt("order_item_id"));
                item.setOrderId(resultSet.getInt("order_id"));
                item.setProductId(resultSet.getInt("product_id"));
                item.setQuantity(resultSet.getInt("quantity"));
                item.setUnitPrice(resultSet.getBigDecimal("unit_price"));
                item.setSubtotalPrice(resultSet.getBigDecimal("subtotal_price"));

                items.add(item);
            }
        }
        return items;
    }

    // Met à jour un article existant dans une commande
    public void updateOrderItem(OrderItem item) throws SQLException {
        String query = "UPDATE orderitems SET quantity = ?, subtotal_price = ? WHERE order_item_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, item.getQuantity());
            statement.setBigDecimal(2, item.getSubtotalPrice());
            statement.setInt(3, item.getOrderItemId());
            statement.executeUpdate();
        }
    }

    // Supprime un article d'une commande
    public void deleteOrderItem(int orderItemId) throws SQLException {
        String query = "DELETE FROM orderitems WHERE order_item_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderItemId);
            statement.executeUpdate();
        }
    }
}
