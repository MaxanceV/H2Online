/**
 * Data Access Object for managing order items in the database.
 * This class handles all CRUD (Create, Read, Update, Delete) operations
 * for order items, which represent individual products within an order.
 */
package sqlbdd;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.OrderItem;
import tools.DBconnection;

public class OrderItemSQL {

    /**
     * Adds a new order item to the database.
     * Creates a new record in the orderitems table with the specified item details.
     *
     * @param item The OrderItem object containing the details to be added
     * @throws SQLException If a database access error occurs
     */
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

    /**
     * Retrieves all order items associated with a specific order.
     *
     * @param orderId The ID of the order to retrieve items for
     * @return List of OrderItem objects associated with the specified order
     * @throws SQLException If a database access error occurs
     */
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

    /**
     * Updates an existing order item in the database.
     * Only updates the quantity and subtotal price fields.
     *
     * @param item The OrderItem object containing the updated details
     * @throws SQLException If a database access error occurs
     */
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

    /**
     * Deletes an order item from the database.
     *
     * @param orderItemId The ID of the order item to delete
     * @throws SQLException If a database access error occurs
     */
    public void deleteOrderItem(int orderItemId) throws SQLException {
        String query = "DELETE FROM orderitems WHERE order_item_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, orderItemId);
            statement.executeUpdate();
        }
    }
}