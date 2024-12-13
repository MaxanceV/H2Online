package Control;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import Main.MainLaunched;

public class CtrlUser {
	private Connection myConnection;
    private PreparedStatement ps;
    private ResultSet rs;
    
    public CtrlUser(Connection myConnection) {
    	this.myConnection = myConnection;
    }
	
	public void recupUser() {
        try {
            ps = myConnection.prepareStatement("SELECT Nom, Prenom, id, num FROM user");
            rs = ps.executeQuery();
            
            while (rs.next()) {
            	System.out.println(rs.getString(1));
            }
            
            rs.close();
            ps.close();
            
        } catch (SQLException ex) {
            Logger.getLogger(MainLaunched.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
	
	public void modifierMdp(String nouveauNom, int num) {
        try {
            ps = myConnection.prepareStatement("UPDATE user SET Nom = ? WHERE num = ?");
            ps.setString(1, nouveauNom);
            ps.setInt(2, num);
            ps.executeUpdate();
            ps.close();
       } catch (SQLException ex) {
           Logger.getLogger(MainLaunched.class.getName()).log(Level.SEVERE, null, ex);
       }
    }

}
