package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBconnection {
    private static Connection connection;

    static {
        try {
            String pilote = "com.mysql.cj.jdbc.Driver";
            String nameBDD = "h2online";
            String connetion = "jdbc:mysql://localhost:3307/" + nameBDD + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            String DBlogin = "root"; // Utilise "root" comme utilisateur
            String DBpassword = "root"; // Utilise "root" comme mot de passe
            Class.forName(pilote);
            System.out.println("Tentative de connexion à la base de données...");
            connection = DriverManager.getConnection(connetion, DBlogin, DBpassword);
            System.out.println("Connexion réussie : " + connection);
        } catch (ClassNotFoundException ex) {
            System.err.println("Erreur : Pilote JDBC non trouvé.");
            ex.printStackTrace();
        } catch (SQLException ex) {
            System.err.println("Erreur de connexion à la base de données.");
            ex.printStackTrace();
        }
    }

    public static Connection getConnection() {
        return connection;
    }
}
