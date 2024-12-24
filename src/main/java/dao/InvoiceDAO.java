package dao;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javafx.stage.FileChooser;
import models.Order;
import models.OrderItem;
import models.User;
import tools.DBconnection;
import tools.NotificationUtils;
import tools.SessionManager;

    public class InvoiceDAO {

    	public void downloadInvoice(int orderId) {
    	    try {
    	        // Récupérer les informations de la commande
    	        OrderDAO orderDAO = new OrderDAO();
    	        Order order = orderDAO.getOrderById(orderId);

    	        // Récupérer les informations des articles de la commande
    	        OrderItemDAO orderItemDAO = new OrderItemDAO();
    	        List<OrderItem> orderItems = orderItemDAO.getOrderItems(orderId);

    	        // Récupérer les informations de l'utilisateur
    	        UserDAO userDAO = new UserDAO();
    	        User user = userDAO.getUserById(order.getUserId());

    	        // Utiliser FileChooser pour demander à l'utilisateur où enregistrer le fichier
    	        FileChooser fileChooser = new FileChooser();
    	        fileChooser.setTitle("Save Invoice");
    	        fileChooser.setInitialFileName("Invoice_" + orderId + ".pdf");
    	        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files", "*.pdf"));

    	        // Afficher le dialogue pour choisir l'emplacement de sauvegarde
    	        File selectedFile = fileChooser.showSaveDialog(null);
    	        if (selectedFile == null) {
    	            System.out.println("Invoice download canceled by the user.");
    	            return;
    	        }

    	        // Générer la facture PDF
    	        Document document = new Document();
    	        PdfWriter.getInstance(document, new FileOutputStream(selectedFile));

    	        document.open();

    	        // Ajouter le titre
    	        Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    	        Paragraph title = new Paragraph("Invoice #" + orderId, titleFont);
    	        title.setAlignment(Element.ALIGN_CENTER);
    	        document.add(title);

    	        document.add(new Paragraph("\n"));

    	        // Ajouter les informations du client
    	        Font infoFont = new Font(Font.FontFamily.HELVETICA, 12);
    	        document.add(new Paragraph("Client Information:", titleFont));
    	        document.add(new Paragraph("Name: " + user.getFirstName() + " " + user.getLastName(), infoFont));
    	        document.add(new Paragraph("Address: " + user.getAddress() + ", " + user.getCity() + ", " + user.getPostalCode() + ", " + user.getCountry(), infoFont));
    	        document.add(new Paragraph("Phone: " + (user.getPhoneNumber() != null ? user.getPhoneNumber() : "N/A"), infoFont));
    	        document.add(new Paragraph("\n"));

    	        // Ajouter les informations de la commande
    	        document.add(new Paragraph("Order Date: " + order.getOrderDate(), infoFont));
    	        document.add(new Paragraph("Invoice Date: " + java.time.LocalDate.now(), infoFont));
    	        document.add(new Paragraph("\n"));

    	        // Ajouter les articles commandés
    	        document.add(new Paragraph("Order Items:", titleFont));
    	        PdfPTable table = new PdfPTable(4); // Table avec 4 colonnes : Nom, Quantité, Prix unitaire, Sous-total
    	        table.setWidthPercentage(100);
    	        table.setSpacingBefore(10f);

    	        // En-tête du tableau
    	        table.addCell("Product Name");
    	        table.addCell("Quantity");
    	        table.addCell("Unit Price (€)");
    	        table.addCell("Subtotal (€)");

    	        BigDecimal totalAmount = BigDecimal.ZERO;

    	        for (OrderItem item : orderItems) {
    	            table.addCell(item.getProductName()); // Assurez-vous que `getProductName()` est implémentée dans OrderItem
    	            table.addCell(String.valueOf(item.getQuantity()));
    	            table.addCell(item.getUnitPrice().toString());
    	            table.addCell(item.getSubtotalPrice().toString());
    	            totalAmount = totalAmount.add(item.getSubtotalPrice());
    	        }

    	        document.add(table);

    	        // Ajouter le total général
    	        document.add(new Paragraph("\n"));
    	        document.add(new Paragraph("Total: " + totalAmount + " €", titleFont));

    	        document.close();

    	        // Afficher une notification ou un message de confirmation
    	        NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(), 
    	                "Invoice downloaded successfully!", true);

    	        System.out.println("Invoice generated successfully: " + selectedFile.getAbsolutePath());
    	    } catch (Exception e) {
    	        e.printStackTrace();
    	        NotificationUtils.showNotification(SessionManager.getMainLayout().getRootPane(), 
    	                "Failed to generate the invoice.", false);
    	    }
    	}

        
        public int generateInvoice(int orderId, BigDecimal totalAmount) throws SQLException {
            String query = "INSERT INTO invoices (order_id, total_amount, payment_status) VALUES (?, ?, ?)";
            try (Connection connection = DBconnection.getConnection();
                 PreparedStatement stmt = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setInt(1, orderId);
                stmt.setBigDecimal(2, totalAmount);
                stmt.setString(3, "paid"); // Statut par défaut de la facture
                stmt.executeUpdate();

                // Récupérer l'ID généré pour la facture
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Failed to generate invoice, no ID obtained.");
                    }
                }
            }
        }

}
