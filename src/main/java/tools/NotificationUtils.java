package tools;

import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

/**
 * Provides utility methods to display transient notifications in a JavaFX application.
 */
public class NotificationUtils {

    /**
     * Displays a notification message inside a specified {@link StackPane}.
     * The notification is styled according to whether it represents success or failure,
     * and automatically fades out after a short duration.
     *
     * @param parent    The {@link StackPane} where the notification will be added.
     * @param message   The message to display in the notification.
     * @param isSuccess If true, the notification is styled as a success message;
     *                  otherwise, it is styled as an error message.
     */
    public static void showNotification(StackPane parent, String message, boolean isSuccess) {
        Label notification = new Label(message);
        notification.setFont(new Font(14));
        notification.setTextFill(Color.WHITE);
        notification.setStyle("-fx-background-color: " + (isSuccess ? "#28a745" : "#dc3545")
                + "; -fx-padding: 10; -fx-border-radius: 5; -fx-background-radius: 5;");
        notification.setOpacity(0);

        parent.getChildren().add(notification);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.5), notification);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.5), notification);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setDelay(Duration.seconds(2)); // The duration the notification remains visible

        fadeIn.setOnFinished(e -> fadeOut.play());
        fadeOut.setOnFinished(e -> parent.getChildren().remove(notification));

        fadeIn.play();
    }
}
