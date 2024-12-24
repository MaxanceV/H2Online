package tools;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

public class NotificationUtils {

    public static void showNotification(StackPane parent, String message, boolean isSuccess) {
        Label notification = new Label(message);
        notification.setFont(new Font(14));
        notification.setTextFill(Color.WHITE);
        notification.setStyle("-fx-background-color: " + (isSuccess ? "#28a745" : "#dc3545") + "; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        notification.setOpacity(0);

        parent.getChildren().add(notification);

        // Animation pour faire apparaître et disparaître la notification
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2)); // Durée d'affichage de la notification

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> parent.getChildren().remove(notification));

        fadeIn.play();
    }
}
