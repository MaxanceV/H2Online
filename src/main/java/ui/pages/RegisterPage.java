package ui.pages;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import models.User;
import sqlbdd.UserSQL;
import tools.PasswordManager;
import ui.elements.MainLayout;

public class RegisterPage {
    private MainLayout mainLayout;

    public RegisterPage(MainLayout mainLayout) {
        this.mainLayout = mainLayout;
    }

    public VBox getView() {
        Label title = new Label("Registration");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Name");
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm password");

        Button registerButton = new Button("Register");
        registerButton.setDisable(true);
        Button cancelButton = new Button("Cancel");

        Label errorLabel = new Label();
        errorLabel.setStyle("-fx-text-fill: red;");

        Runnable validateFields = () -> {
            boolean allFieldsFilled = !firstNameField.getText().isEmpty() &&
                                      !lastNameField.getText().isEmpty() &&
                                      !emailField.getText().isEmpty() &&
                                      !passwordField.getText().isEmpty() &&
                                      !confirmPasswordField.getText().isEmpty();

            boolean passwordsMatch = passwordField.getText().equals(confirmPasswordField.getText());

            if (allFieldsFilled && passwordsMatch) {
                registerButton.setDisable(false);
                errorLabel.setText(""); 
            } else {
                registerButton.setDisable(true);
                if (!passwordsMatch) {
                    errorLabel.setText("Passwords do not match");
                }
            }
        };

        firstNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        lastNameField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        emailField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());
        confirmPasswordField.textProperty().addListener((observable, oldValue, newValue) -> validateFields.run());

        registerButton.setOnAction(e -> {
            UserSQL userDAO = new UserSQL();
            try {
                if (!userDAO.emailExists(emailField.getText())) {

                    User newUser = new User(
                        0, 
                        firstNameField.getText(),
                        lastNameField.getText(),
                        emailField.getText(),
                        PasswordManager.hashPassword(passwordField.getText()),
                        "customer" 
                    );
                    System.out.println("Password after ashing register in java : " + PasswordManager.hashPassword(passwordField.getText()));

                    userDAO.addUser(newUser);
                    System.out.println("User register sucessfully !");
                    mainLayout.setContent(new LoginPage(mainLayout).getView()); 
                } else {
                    errorLabel.setText("Email already used");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorLabel.setText("Error during the registration, please do it again.");
            }
        });


        cancelButton.setOnAction(e -> {
            mainLayout.setContent(new LoginPage(mainLayout).getView());
        });

        // Disposition des éléments
        GridPane form = new GridPane();
        form.setAlignment(Pos.CENTER);
        form.setHgap(10);
        form.setVgap(10);

        form.add(new Label("First Name :"), 0, 0);
        form.add(firstNameField, 1, 0);
        form.add(new Label("Name :"), 0, 1);
        form.add(lastNameField, 1, 1);
        form.add(new Label("Email :"), 0, 2);
        form.add(emailField, 1, 2);
        form.add(new Label("Password :"), 0, 3);
        form.add(passwordField, 1, 3);
        form.add(new Label("Confirm password :"), 0, 4);
        form.add(confirmPasswordField, 1, 4);

        VBox layout = new VBox(15, title, form, errorLabel, registerButton, cancelButton);
        layout.setAlignment(Pos.CENTER);

        return layout;
    }
}
