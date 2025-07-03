package com.icet.clothify;

import com.icet.clothify.util.EmailUtil;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EmailTestApp extends Application {

    public static void main(String[] args) {
        // This launches the JavaFX application
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("EmailUtil Test");

        Label statusLabel = new Label("Click the button to send a test email.");
        Button sendButton = new Button("Send Test Email");

        sendButton.setOnAction(e -> {
            statusLabel.setText("Preparing to send email...");

            // 1. Define the email details
            String recipient = "himashaprageeth2007@gmail.com";
            String subject = "Test Email from JavaFX App";
            String body = "<h1>Hello!</h1><p>This is a <b>test</b> email sent from the test application.</p>";

            // 2. Create the task using your utility class
            Task<Void> sendEmailTask = EmailUtil.createSendEmailTask(recipient, subject, body);

            // 3. Define what happens on success and failure (this runs on the FX thread)
            sendEmailTask.setOnSucceeded(event -> {
                statusLabel.setText("Task Succeeded: Email sent!");
                System.out.println("Email sent successfully!");
            });

            sendEmailTask.setOnFailed(event -> {
                Throwable exception = sendEmailTask.getException();
                statusLabel.setText("Task Failed: " + exception.getMessage());
                System.err.println("Failed to send email. See stack trace below.");
                exception.printStackTrace();
            });

            // 4. Run the task in a background thread
            new Thread(sendEmailTask).start();
        });

        VBox root = new VBox(20, statusLabel, sendButton);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 400, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}