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

public class CartUtils {

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
