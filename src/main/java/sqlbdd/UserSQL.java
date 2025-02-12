package sqlbdd;

import models.User;
import tools.DBconnection;
import tools.PasswordManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages database operations related to users, including creation, retrieval,
 * updating, and deletion of user records.
 */
public class UserSQL {

    /**
     * Adds a new user to the database.
     *
     * @param user The user object containing all necessary user details.
     * @throws SQLException If a database access error occurs.
     */
    // Ajouter un utilisateur
    public void addUser(User user) throws SQLException {
        String hashedPassword = user.getPassword();

        String query = "INSERT INTO users (first_name, last_name, email, phone_number, address, city, postal_code, country, password, role) " +
                       "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getCity());
            stmt.setString(7, user.getPostalCode());
            stmt.setString(8, user.getCountry());
            stmt.setString(9, hashedPassword); // Utiliser le hash du mot de passe
            stmt.setString(10, user.getRole());
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves all users from the database.
     *
     * @return A list of all users.
     * @throws SQLException If a database access error occurs.
     */
    // Récupérer tous les utilisateurs
    public List<User> getAllUsers() throws SQLException {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";
        try (Connection connection = DBconnection.getConnection();
             Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                users.add(mapUser(rs));
            }
        }
        return users;
    }

    /**
     * Deletes a user from the database by user ID.
     *
     * @param userId The ID of the user to delete.
     * @throws SQLException If a database access error occurs.
     */
    // Supprimer un utilisateur
    public void deleteUser(int userId) throws SQLException {
        String query = "DELETE FROM users WHERE user_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        }
    }

    /**
     * Retrieves a user from the database by user ID.
     *
     * @param userId The ID of the user to retrieve.
     * @return The User if found, or null if not found.
     * @throws SQLException If a database access error occurs.
     */
    // Récupérer un utilisateur par son ID
    public User getUserById(int userId) throws SQLException {
        String query = "SELECT * FROM users WHERE user_id = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }
        return null;
    }

    /**
     * Updates an existing user in the database. If a password is provided, it is
     * updated separately.
     *
     * @param user The User object with updated information.
     * @throws SQLException If a database access error occurs.
     */
    // Mettre à jour un utilisateur
    public void updateUser(User user) throws SQLException {
        String query = "UPDATE users SET first_name = ?, last_name = ?, email = ?, phone_number = ?, address = ?, city = ?, postal_code = ?, country = ?, role = ? WHERE user_id = ?";
        
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
             
            stmt.setString(1, user.getFirstName());
            stmt.setString(2, user.getLastName());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getPhoneNumber());
            stmt.setString(5, user.getAddress());
            stmt.setString(6, user.getCity());
            stmt.setString(7, user.getPostalCode());
            stmt.setString(8, user.getCountry());
            stmt.setString(9, user.getRole());
            stmt.setInt(10, user.getId());

            stmt.executeUpdate();
        }

        // Si un mot de passe a été fourni, mettez-le à jour séparément
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            String passwordQuery = "UPDATE users SET password = ? WHERE user_id = ?";
            try (PreparedStatement stmt = DBconnection.getConnection().prepareStatement(passwordQuery)) {
                stmt.setString(1, user.getPassword());
                stmt.setInt(2, user.getId());
                stmt.executeUpdate();
            }
        }
    }

    /**
     * Checks if an email address already exists in the database.
     *
     * @param email The email address to check.
     * @return True if the email exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    // Vérifier si un email existe
    public boolean emailExists(String email) throws SQLException {
        String query = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (Connection connection = DBconnection.getConnection();
             PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    /**
     * Validates a user by comparing the provided password with the hashed password
     * stored in the database.
     *
     * @param email    The user's email.
     * @param password The user's plain text password (to be hashed and compared).
     * @return The User object if validation is successful, or null if invalid credentials.
     * @throws SQLException If a database access error occurs.
     */
    public User validateUser(String email, String password) throws SQLException {
        String query = "SELECT * FROM users WHERE email = ?";
        try (Connection connection = DBconnection.getConnection();
            PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Récupérer le mot de passe haché depuis la base de données
                    String hashedPassword = rs.getString("password");

                    // Comparer le mot de passe saisi avec le hash stocké
                    if (PasswordManager.hashPassword(password).equals(hashedPassword)) {
                        return mapUser(rs); // Retourne l'utilisateur si les mots de passe correspondent
                    }
                }
            }
        }
        return null; // Retourne null si les identifiants sont incorrects
    }

    /**
     * Maps a {@link ResultSet} row to a {@link User} object.
     *
     * @param rs The result set containing user information.
     * @return A populated User object.
     * @throws SQLException If a database access error occurs.
     */
    // Mapper un utilisateur depuis un ResultSet
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
        user.setPassword(rs.getString("password"));
        user.setRole(rs.getString("role"));
        return user;
    }
}
