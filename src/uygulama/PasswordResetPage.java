package uygulama;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class PasswordResetPage {

    public void start(Stage stage) {
        // Ana grid
        GridPane grid = new GridPane();
        grid.setPadding(new Insets(30, 40, 30, 40));
        grid.setVgap(15);
        grid.setHgap(15);
        grid.setAlignment(Pos.CENTER);
        grid.setStyle("-fx-background-color: #f9f9f9; -fx-border-radius: 10; -fx-background-radius: 10;");

        // Başlık
        Label titleLabel = new Label("Şifre Sıfırlama");
        titleLabel.setFont(Font.font("Arial", 24));
        titleLabel.setTextFill(Color.web("#333333"));
        grid.add(titleLabel, 0, 0, 2, 1);
        GridPane.setHalignment(titleLabel, javafx.geometry.HPos.CENTER);

        // Kullanıcı Adı
        Label usernameLabel = new Label("Kullanıcı Adı:");
        usernameLabel.setFont(Font.font("Arial", 14));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı adınızı girin");
        usernameField.setPrefWidth(250);

        // E-posta
        Label emailLabel = new Label("E-posta:");
        emailLabel.setFont(Font.font("Arial", 14));
        TextField emailField = new TextField();
        emailField.setPromptText("Kayıtlı e-posta adresinizi girin");
        emailField.setPrefWidth(250);

        // Yeni Şifre
        Label passwordLabel = new Label("Yeni Şifre:");
        passwordLabel.setFont(Font.font("Arial", 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Yeni şifre girin");
        passwordField.setPrefWidth(250);

        // Şifre güncelle butonu
        Button resetButton = new Button("Şifreyi Güncelle");
        resetButton.setPrefWidth(250);
        resetButton.setStyle(
            "-fx-background-color: #4CAF50; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold; " +
            "-fx-font-size: 14;"
        );

        // Buton hover efekti
        resetButton.setOnMouseEntered(e -> resetButton.setStyle(
                "-fx-background-color: #45a049; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14;"
        ));
        resetButton.setOnMouseExited(e -> resetButton.setStyle(
                "-fx-background-color: #4CAF50; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-font-size: 14;"
        ));

        // Durum mesajı
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font("Arial", 13));
        statusLabel.setWrapText(true);
        statusLabel.setPrefWidth(300);

        // Elemanları grid'e ekle
        grid.add(usernameLabel, 0, 1);
        grid.add(usernameField, 1, 1);
        grid.add(emailLabel, 0, 2);
        grid.add(emailField, 1, 2);
        grid.add(passwordLabel, 0, 3);
        grid.add(passwordField, 1, 3);
        grid.add(resetButton, 1, 4);
        grid.add(statusLabel, 0, 5, 2, 1);

        // Buton aksiyonu
        resetButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String email = emailField.getText().trim();
            String newPassword = passwordField.getText().trim();

            if (username.isEmpty() || email.isEmpty() || newPassword.isEmpty()) {
                statusLabel.setTextFill(Color.web("#d32f2f")); // kırmızı
                statusLabel.setText("Lütfen tüm alanları doldurunuz.");
                return;
            }

            boolean result = resetPassword(username, email, newPassword);
            if (result) {
                statusLabel.setTextFill(Color.web("#388e3c")); // yeşil
                statusLabel.setText("✅ Şifre başarıyla güncellendi.");
            } else {
                statusLabel.setTextFill(Color.web("#d32f2f")); // kırmızı
                statusLabel.setText("❌ Kullanıcı adı ve e-posta eşleşmiyor.");
            }
        });

        // Sahne ayarları
        Scene scene = new Scene(grid, 450, 320);
        stage.setTitle("Şifre Sıfırlama");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    // Bu metodu senin verdiğin gibi bırakıyorum (şifreyi hashleme ekleyebilirsin)
    private boolean resetPassword(String username, String email, String newPassword) {
        String url = "jdbc:mysql://localhost:3306/foodordersystem";
        String dbUser = "root";
        String dbPass = "";

        String checkQuery = "SELECT * FROM users WHERE username = ? AND email = ?";
        String updateQuery = "UPDATE users SET password = ? WHERE username = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUser, dbPass)) {

            // Kullanıcı adı ve e-posta eşleşiyor mu?
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, username);
                checkStmt.setString(2, email);
                ResultSet rs = checkStmt.executeQuery();

                if (rs.next()) {
                    // Eşleşme varsa şifreyi güncelle
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newPassword);
                        updateStmt.setString(2, username);
                        int affectedRows = updateStmt.executeUpdate();
                        return affectedRows > 0;
                    }
                } else {
                    return false; // kullanıcı adı ve e-posta eşleşmedi
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}