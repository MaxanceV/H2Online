package dao;

import models.User;
import tools.DBconnection;
import tools.PasswordManager;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    private Connection connection;

    public UserDAO() {
        this.connection = DBconnection.getConnection();
    }

    // Ajouter un utilisateur
    public void addUser(User user) throws SQLException {
        String query = "INSERT INTO Users (first_name, last_name, email, phone_number, address, city, postal_code, country, password, role) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getCity());
            stmt.setString(7, user.getPostalCode());
            stmt.setString(8, user.getCountry());
            stmt.setString(9, user.getPassword());
            stmt.setString(10, user.getRole());
            stmt.executeUpdate();
        }
    }

    // Récupérer tous les utilisateurs
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM Users";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                User user = mapUser(rs);
                users.add(user);
            }
        }
        return users;
    }

    // Supprimer un utilisateur
    public void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    // Récupérer un utilisateur par son ID
    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                	return mapUser(rs);
                }
            }
        }
        return null;
    }

    public void updateUser(User user) throws SQLException {
        // Validation des champs essentiels
        if (user.getFirstName() == null || user.getFirstName().isEmpty()) {
            throw new IllegalArgumentException("Le prénom ne peut pas être nul ou vide.");
        }
        if (user.getLastName() == null || user.getLastName().isEmpty()) {
            throw new IllegalArgumentException("Le nom ne peut pas être nul ou vide.");
        }
        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("L'email ne peut pas être nul ou vide.");
        }

        String query = "UPDATE Users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, address = ?, city = ?, postal_code = ?, country = ?, password = ?, role = ? WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            // Champs obligatoires
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());

            // Champs optionnels avec gestion conditionnelle
            stmt.setString(4, user.getPhoneNumber() != null ? user.getPhoneNumber() : getCurrentValue(user.getId(), "phone_number"));
            stmt.setString(5, user.getAddress() != null ? user.getAddress() : getCurrentValue(user.getId(), "address"));
            stmt.setString(6, user.getCity() != null ? user.getCity() : getCurrentValue(user.getId(), "city"));
            stmt.setString(7, user.getPostalCode() != null ? user.getPostalCode() : getCurrentValue(user.getId(), "postal_code"));
            stmt.setString(8, user.getCountry() != null ? user.getCountry() : getCurrentValue(user.getId(), "country"));

            // Gestion conditionnelle du mot de passe
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                stmt.setString(9, PasswordManager.hashPassword(user.getPassword()));
            } else {
                stmt.setString(9, getCurrentValue(user.getId(), "password"));
            }

            // Gestion du rôle
            stmt.setString(10, user.getRole() != null ? user.getRole() : getCurrentValue(user.getId(), "role"));

            // ID de l'utilisateur
            stmt.setInt(11, user.getId());

            stmt.executeUpdate();
        }
    }

    
    private String getCurrentValue(int userId, String column) throws SQLException {
        String query = "SELECT " + column + " FROM Users WHERE user_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString(column); // Retourne la valeur du champ demandé
                }
            }
        }
        return null;
    }


    public boolean emailExists(String email) throws SQLException {
        // Vérification locale
        if (email == null || email.isEmpty() || !email.contains("@")) {
            return false; // Considérer les emails invalides comme inexistants
        }

        // Vérification dans la base de données
        String query = "SELECT COUNT(*) FROM Users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Retourne true si l'email existe dans la base
                }
            }
        }
        return false; // Retourne false si aucun résultat
    }


    public User validateUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM Users WHERE email = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            stmt.setString(2, PasswordManager.hashPassword(password)); // Comparer avec le mot de passe haché
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null; // Retourne null si l'utilisateur n'existe pas
    }
    
    private User mapUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setPhoneNumber(rs.getString("phone_number"));
        user.setAddress(rs.getString("address"));
        user.setCity(rs.getString("city"));
        user.setPostalCode(rs.getString("postal_code"));
        user.setCountry(rs.getString("country"));
        user.setRole(rs.getString("role"));
        return user;
    }


    
}

