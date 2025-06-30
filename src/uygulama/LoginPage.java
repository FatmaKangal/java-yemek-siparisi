package uygulama;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.sql.*;

public class LoginPage extends Application {

    public static class KullaniciBilgileri {
        public int userId;
        public String username;
        public String bolum;

        public KullaniciBilgileri(int userId, String username, String bolum) {
            this.userId = userId;
            this.username = username;
            this.bolum = bolum;
        }
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(15);
        grid.setVgap(15);
        grid.setPadding(new Insets(40));
        grid.setStyle("-fx-background-color: linear-gradient(to bottom right, #ffcc80, #fb8c00);"
                + "-fx-background-radius: 20;");

        // Container box ile gölge ve arka plan koyuyoruz
        VBox container = new VBox(20);
        container.setPadding(new Insets(30));
        container.setAlignment(Pos.CENTER);
        container.setMaxWidth(350);
        container.setStyle("-fx-background-color: white; -fx-background-radius: 20;");

        DropShadow shadow = new DropShadow();
        shadow.setRadius(10);
        shadow.setOffsetX(0);
        shadow.setOffsetY(5);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        container.setEffect(shadow);

        Label title = new Label("Yemek Sipariş Sistemi");
        title.setFont(Font.font("Segoe UI Semibold", 30));
        title.setTextFill(Color.web("#f57c00"));
        title.setStyle("-fx-effect: dropshadow(gaussian, rgba(245,124,0,0.7), 4, 0, 0, 0);");

        Label usernameLabel = new Label("Kullanıcı Adı:");
        usernameLabel.setFont(Font.font("Segoe UI", 14));
        TextField usernameField = new TextField();
        usernameField.setPromptText("Kullanıcı adınızı girin");
        usernameField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #f57c00; -fx-padding: 8 12;");

        Label passwordLabel = new Label("Şifre:");
        passwordLabel.setFont(Font.font("Segoe UI", 14));
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Şifrenizi girin");
        passwordField.setStyle("-fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #f57c00; -fx-padding: 8 12;");

        Button loginButton = new Button("Giriş Yap");
        loginButton.setStyle(buttonStyle("#f57c00"));

        Button registerButton = new Button("Kayıt Ol");
        registerButton.setStyle(buttonStyle("#0288d1"));

        Button forgotPasswordButton = new Button("Şifremi Unuttum");
        forgotPasswordButton.setStyle(buttonStyle("#c2185b"));

        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_RIGHT);
        buttonBox.getChildren().addAll(loginButton, registerButton);

        VBox vboxButtons = new VBox(12);
        vboxButtons.getChildren().addAll(buttonBox, forgotPasswordButton);
        vboxButtons.setAlignment(Pos.CENTER_RIGHT);

        Label infoLabel = new Label();
        infoLabel.setFont(Font.font("Segoe UI", 13));
        infoLabel.setTextFill(Color.RED);

        // Elemanları container'a ekle
        container.getChildren().addAll(title, usernameLabel, usernameField, passwordLabel, passwordField, vboxButtons, infoLabel);

        // grid içine container'ı yerleştir
        grid.add(container, 0, 0);

        // Giriş butonu işlemi
        loginButton.setOnAction(e -> {
            String username = usernameField.getText().trim();
            String password = passwordField.getText().trim();

            if (username.isEmpty() || password.isEmpty()) {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Lütfen kullanıcı adı ve şifre giriniz.");
                return;
            }

            KullaniciBilgileri kullanici = getKullaniciBilgileri(username, password);
            if (kullanici != null) {
                infoLabel.setTextFill(Color.GREEN);
                infoLabel.setText("Giriş başarılı! Hoş geldiniz.");

                LokantaSecimPage lokantaSecimPage = new LokantaSecimPage(primaryStage,
                        kullanici.userId, kullanici.username, kullanici.bolum);
                lokantaSecimPage.show();

            } else {
                infoLabel.setTextFill(Color.RED);
                infoLabel.setText("Hatalı kullanıcı adı veya şifre. Tekrar deneyin.");
            }
        });

        registerButton.setOnAction(e -> {
            RegisterPage registerApp = new RegisterPage();
            Stage registerStage = new Stage();
            try {
                registerApp.start(registerStage);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        forgotPasswordButton.setOnAction(e -> {
            PasswordResetPage resetPage = new PasswordResetPage();
            Stage resetStage = new Stage();
            resetPage.start(resetStage);
        });

        Scene scene = new Scene(grid, 460, 480);
        primaryStage.setTitle("Giriş Sayfası");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Butonlar için ortak stil fonksiyonu
    private String buttonStyle(String baseColor) {
        return "-fx-background-color: " + baseColor + ";"
                + "-fx-text-fill: white;"
                + "-fx-font-weight: bold;"
                + "-fx-background-radius: 12;"
                + "-fx-padding: 10 20 10 20;"
                + "-fx-cursor: hand;"
                + "-fx-effect: dropshadow( gaussian , rgba(0,0,0,0.2) , 3,0,0,1);"
                + " -fx-font-size: 14px;"
                + "-fx-transition: background-color 0.3s ease-in-out;"
                + " ";

        // Hover efektini scene içinde ekleyebilirsin (daha gelişmiş)
    }

    private KullaniciBilgileri getKullaniciBilgileri(String username, String password) {
        String url = "jdbc:mysql://localhost:3306/foodordersystem";
        String dbUsername = "root";
        String dbPassword = "";

        String query = "SELECT user_id, username, bolum FROM users WHERE username = ? AND password = ?";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword);
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int userId = rs.getInt("user_id");
                String userNameFromDb = rs.getString("username");
                String bolum = rs.getString("bolum");
                return new KullaniciBilgileri(userId, userNameFromDb, bolum);
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}