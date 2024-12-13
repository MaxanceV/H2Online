package tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBconnection {
    private static Connection connection;
    
    static {
        try {
            String pilote = "com.mysql.cj.jdbc.Driver";
            String nameBDD = "h2online";
            String connetion = "jdbc:mysql://localhost:3306/" + nameBDD + "?useSSL=false&serverTimezone=UTC";
            String DBlogin = "root";
            String DBpassword = "";
            Class.forName(pilote);
            connection = DriverManager.getConnection(connetion, DBlogin, DBpassword);
            System.out.println("Connection établie : " + connection);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DBconnection.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(DBconnection.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static Connection getConnection() {
        return connection;
    }
}
