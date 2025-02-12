package tools;

import models.Order;
import models.OrderItem;
import models.Product;
import sqlbdd.OrderSQL;
import sqlbdd.OrderItemSQL;
import sqlbdd.ProductSQL;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

/**
 * Provides utility methods for managing a cart, including adding items to the
 * cart and checking product availability.
 */
public class CartUtils {

    /**
     * Adds a product to the cart for the specified user. If there is no
     * "in progress" order, this method creates one. If the product is
     * already in the order, it updates the quantity; otherwise, it creates a new
     * order item. The total order price and cart badge are updated accordingly.
     *
     * @param userId  The ID of the user whose cart is being updated.
     * @param product The product to add to the cart.
     * @param quantity The quantity of the product to add.
     */
    public static void addToCart(int userId, Product product, int quantity) {
        OrderSQL orderDAO = new OrderSQL();
        OrderItemSQL orderItemDAO = new OrderItemSQL();

        try {
            // Vérifier si une commande "in progress" existe
            Order inProgressOrder = orderDAO.getInProgressOrder(userId);
            if (inProgressOrder == null) {
                // Créer une nouvelle commande si elle n'existe pas
                int newOrderId = orderDAO.createNewOrder(userId);
                inProgressOrder = orderDAO.getInProgressOrder(userId);
                if (inProgressOrder == null) {
                    throw new SQLException("Failed to create or retrieve a new order for the user.");
                }
            }

            // Vérifier si le produit existe déjà dans la commande
            OrderItem existingItem = orderItemDAO.getOrderItems(inProgressOrder.getOrderId())
                    .stream()
                    .filter(item -> item.getProductId() == product.getProductId())
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                // Vérifiez que le prix unitaire est correctement initialisé
                if (existingItem.getUnitPrice() == null) {
                    existingItem.setUnitPrice(product.getPrice());
                }
                existingItem.setQuantity(quantity);
                orderItemDAO.updateOrderItem(existingItem);
            } else {
                // Ajouter un nouvel article à la commande
                OrderItem newItem = new OrderItem();
                newItem.setOrderId(inProgressOrder.getOrderId());
                newItem.setProductId(product.getProductId());
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(product.getPrice());
                newItem.setSubtotalPrice(product.getPrice().multiply(new BigDecimal(quantity))); // Initialise directement le sous-total
                orderItemDAO.addOrderItem(newItem);
            }

            // Mettre à jour le prix total de la commande
            BigDecimal updatedTotal = orderItemDAO.getOrderItems(inProgressOrder.getOrderId())
                    .stream()
                    .map(OrderItem::getSubtotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderDAO.updateOrderTotal(inProgressOrder.getOrderId(), updatedTotal);

            // Mettre à jour le nombre d'articles dans le panier
            int totalItems = orderItemDAO.getOrderItems(inProgressOrder.getOrderId())
                    .stream()
                    .mapToInt(OrderItem::getQuantity)
                    .sum();

            SessionManager.getMainLayout().updateCartBadge(totalItems);

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Checks the availability of products in a given order. If a product does not
     * have enough stock to fulfill the requested quantity, the quantity is adjusted
     * or the item is removed. Returns a boolean indicating whether the cart
     * remains valid after any adjustments.
     *
     * @param order The order to check.
     * @return True if all items are valid with sufficient stock; false if items
     *         were adjusted or removed.
     * @throws SQLException If a database access error occurs.
     */
    public static boolean checkProductAvailability(Order order) throws SQLException {
        OrderItemSQL orderItemDAO = new OrderItemSQL();
        ProductSQL productDAO = new ProductSQL();

        List<OrderItem> orderItems = orderItemDAO.getOrderItems(order.getOrderId());
        boolean isCartValid = true;

        for (OrderItem item : orderItems) {
            Product product = productDAO.getProductById(item.getProductId());

            if (product.getStockQuantity() < item.getQuantity()) {
                if (product.getStockQuantity() > 0) {
                    item.setQuantity(product.getStockQuantity());
                    item.setSubtotalPrice(product.getPrice().multiply(new BigDecimal(product.getStockQuantity())));
                    orderItemDAO.updateOrderItem(item);
                } else {
                    orderItemDAO.deleteOrderItem(item.getOrderItemId());
                }
                isCartValid = false;
            }
        }

        return isCartValid;
    }

}
