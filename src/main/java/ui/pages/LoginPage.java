package ui.pages;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import models.User;
import sqlbdd.UserSQL;
import tools.SessionManager;
import ui.elements.MainLayout;

/**
 * Represents the login page where users can enter their credentials
 * to log into the H2Online application.
 */
public class LoginPage {
    private MainLayout mainLayout;

    /**
     * Constructs a {@code LoginPage} with the specified {@link MainLayout}.
     *
     * @param mainLayout The main layout of the application.
     */
    public LoginPage(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
    }

    /**
     * Creates and returns a {@link VBox} containing login fields (email and password)
     * and buttons for login and registration.
     *
     * @return A {@link VBox} layout containing all UI elements for user login.
     */
    public VBox getView() {
        Text title = new Text("Log in");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Log in");
        Button registerButton = new Button("Create an account");

        loginButton.setOnAction(e -> {
            try {
                UserSQL userDAO = new UserSQL();

                User user = userDAO.validateUser(emailField.getText(), passwordField.getText());
                if (user != null) {
                    System.out.println("Login successful : " + user.getFirstName());
                    SessionManager.setCurrentUser(user);
                    mainLayout.enableMenu();
                    mainLayout.setContent(new CatalogPage().getView());
                } else {
                    System.out.println("Incorrect credentials !");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        registerButton.setOnAction(e -> {
            mainLayout.setContent(new RegisterPage(mainLayout).getView());
        });

        VBox layout = new VBox(10, title, emailField, passwordField, loginButton, registerButton);
        layout.setAlignment(Pos.CENTER);
        return layout;
    }
}
