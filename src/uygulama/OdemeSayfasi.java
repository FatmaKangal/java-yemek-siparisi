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
import java.time.LocalDate;
import java.util.Map;

public class OdemeSayfasi extends javafx.application.Application {

    private int userId;
    private Map<String, SepetUrun> sepet;

    public OdemeSayfasi() {}

    public OdemeSayfasi(int userId, Map<String, SepetUrun> sepet) {
        this.userId = userId;
        this.sepet = sepet;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(20);
        root.setPadding(new Insets(30));
        root.setStyle("-fx-background-color: #ffffff;");

        // Başlık
        Label titleLabel = new Label("Ödeme Sayfası");
        titleLabel.setFont(Font.font("Arial", 28));
        titleLabel.setTextFill(Color.web("#f57c00"));
        titleLabel.setAlignment(Pos.CENTER);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        root.getChildren().add(titleLabel);

        // Toplam tutar
        double totalAmount = sepet.values().stream()
                .mapToDouble(u -> u.getPrice() * u.getAdet())
                .sum();
        Label totalLabel = new Label("Toplam Tutar: " + String.format("%.2f", totalAmount) + "₺");
        totalLabel.setFont(Font.font("Arial", 18));
        totalLabel.setTextFill(Color.web("#333333"));
        root.getChildren().add(totalLabel);

        // Ödeme yöntemi
        Label paymentLabel = new Label("Ödeme Yöntemi:");
        paymentLabel.setFont(Font.font("Arial", 16));
        paymentLabel.setTextFill(Color.web("#555555"));
        root.getChildren().add(paymentLabel);

        ToggleGroup paymentGroup = new ToggleGroup();

        RadioButton cashRadio = new RadioButton("Kapıda Ödeme");
        cashRadio.setToggleGroup(paymentGroup);
        cashRadio.setSelected(true);
        cashRadio.setFont(Font.font("Arial", 14));
        cashRadio.setTextFill(Color.web("#444444"));

        RadioButton cardRadio = new RadioButton("Kartla Ödeme");
        cardRadio.setToggleGroup(paymentGroup);
        cardRadio.setFont(Font.font("Arial", 14));
        cardRadio.setTextFill(Color.web("#444444"));

        VBox paymentOptions = new VBox(10, cashRadio, cardRadio);
        root.getChildren().add(paymentOptions);

        // Kart bilgileri kutusu
        TextField cardHolderNameField = createStyledTextField("Kart Sahibi Adı");
        TextField cardNumberField = createStyledTextField("Kart Numarası");
        TextField expiryDateField = createStyledTextField("Son Kullanma Tarihi (AA/YY)");
        TextField cvvField = createStyledTextField("CVV");

        VBox cardBox = new VBox(15, cardHolderNameField, cardNumberField, expiryDateField, cvvField);
        cardBox.setPadding(new Insets(15));
        cardBox.setStyle("-fx-background-color: #fff3e0; -fx-border-color: #f57c00; -fx-border-radius: 8; -fx-background-radius: 8;");
        cardBox.setVisible(false);
        root.getChildren().add(cardBox);

        paymentGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            boolean cardSelected = (newVal == cardRadio);
            cardBox.setVisible(cardSelected);
        });

        // Teslimat adresi
        TextField addressField = createStyledTextField("Teslimat Adresi");
        root.getChildren().add(addressField);

        // Ödeme butonu
        Button payButton = new Button("Ödeme Yap");
        payButton.setFont(Font.font("Arial", 16));
        payButton.setStyle(
                "-fx-background-color: #f57c00; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8;"
        );
        payButton.setMaxWidth(Double.MAX_VALUE);

        payButton.setOnMouseEntered(e -> payButton.setStyle(
                "-fx-background-color: #ef6c00; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8;"
        ));
        payButton.setOnMouseExited(e -> payButton.setStyle(
                "-fx-background-color: #f57c00; " +
                "-fx-text-fill: white; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8;"
        ));

        root.getChildren().add(payButton);

        // Bilgilendirme etiketi
        Label infoLabel = new Label();
        infoLabel.setFont(Font.font("Arial", 14));
        infoLabel.setWrapText(true);
        infoLabel.setMaxWidth(Double.MAX_VALUE);
        root.getChildren().add(infoLabel);

        // Buton aksiyonu
        payButton.setOnAction(e -> {
            String paymentMethod = ((RadioButton) paymentGroup.getSelectedToggle()).getText();
            String address = addressField.getText().trim();

            if (address.isEmpty()) {
                infoLabel.setTextFill(Color.web("#d32f2f"));
                infoLabel.setText("Lütfen teslimat adresini giriniz.");
                return;
            }

            if (paymentMethod.equals("Kartla Ödeme")) {
                if (cardHolderNameField.getText().trim().isEmpty() || cardNumberField.getText().trim().isEmpty()
                        || expiryDateField.getText().trim().isEmpty() || cvvField.getText().trim().isEmpty()) {
                    infoLabel.setTextFill(Color.web("#d32f2f"));
                    infoLabel.setText("Lütfen kart bilgilerini eksiksiz giriniz.");
                    return;
                }

                if (!cardNumberField.getText().matches("\\d{16}")) {
                    infoLabel.setTextFill(Color.web("#d32f2f"));
                    infoLabel.setText("Kart numarası 16 haneli olmalıdır.");
                    return;
                }

                if (!cvvField.getText().matches("\\d{3}")) {
                    infoLabel.setTextFill(Color.web("#d32f2f"));
                    infoLabel.setText("CVV 3 haneli olmalıdır.");
                    return;
                }

                if (!expiryDateField.getText().matches("(0[1-9]|1[0-2])/\\d{2}")) {
                    infoLabel.setTextFill(Color.web("#d32f2f"));
                    infoLabel.setText("Son kullanma tarihi geçersiz. (AA/YY)");
                    return;
                }
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/foodordersystem", "root", "")) {
                conn.setAutoCommit(false);

                int orderId;
                try (PreparedStatement orderStmt = conn.prepareStatement(
                        "INSERT INTO orders (user_id, order_date, total_price, address) VALUES (?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    orderStmt.setInt(1, userId);
                    orderStmt.setDate(2, Date.valueOf(LocalDate.now()));
                    orderStmt.setDouble(3, totalAmount);
                    orderStmt.setString(4, address);
                    orderStmt.executeUpdate();

                    ResultSet keys = orderStmt.getGeneratedKeys();
                    if (keys.next()) {
                        orderId = keys.getInt(1);
                    } else {
                        throw new SQLException("Sipariş ID alınamadı.");
                    }
                }

                try (PreparedStatement itemStmt = conn.prepareStatement(
                        "INSERT INTO order_items (order_id, product_name, quantity) VALUES (?, ?, ?)")) {
                    for (SepetUrun urun : sepet.values()) {
                        itemStmt.setInt(1, orderId);
                        itemStmt.setString(2, urun.getName());
                        itemStmt.setInt(3, urun.getAdet());

                        itemStmt.addBatch();
                    }
                    itemStmt.executeBatch();
                }

                try (PreparedStatement paymentStmt = conn.prepareStatement(
                        "INSERT INTO payments (order_id, amount, payment_date, payment_method) VALUES (?, ?, ?, ?)")) {
                    paymentStmt.setInt(1, orderId);
                    paymentStmt.setDouble(2, totalAmount);
                    paymentStmt.setDate(3, Date.valueOf(LocalDate.now()));
                    paymentStmt.setString(4, paymentMethod);
                    paymentStmt.executeUpdate();
                }

                if (paymentMethod.equals("Kartla Ödeme")) {
                    try (PreparedStatement cardStmt = conn.prepareStatement(
                            "INSERT INTO card_payments (order_id, card_number, expiry_date, cvv, card_holder_name) VALUES (?, ?, ?, ?, ?)")) {
                        cardStmt.setInt(1, orderId);
                        cardStmt.setString(2, cardNumberField.getText().trim());
                        cardStmt.setString(3, expiryDateField.getText().trim());
                        cardStmt.setString(4, cvvField.getText().trim());
                        cardStmt.setString(5, cardHolderNameField.getText().trim());
                        cardStmt.executeUpdate();
                    }
                }

                conn.commit();
                infoLabel.setTextFill(Color.web("#388e3c"));
                infoLabel.setText("Ödeme başarılı! Siparişiniz alındı.");
                payButton.setDisable(true);
                sepet.clear();

            } catch (SQLException ex) {
                ex.printStackTrace();
                infoLabel.setTextFill(Color.web("#d32f2f"));
                infoLabel.setText("Bir hata oluştu: " + ex.getMessage());
            }
        });

        Scene scene = new Scene(root, 420, 580);
        primaryStage.setTitle("Ödeme Sayfası");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private TextField createStyledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setFont(Font.font("Arial", 14));
        tf.setPrefWidth(380);
        tf.setStyle(
                "-fx-background-radius: 6; " +
                "-fx-border-radius: 6; " +
                "-fx-border-color: #ccc; " +
                "-fx-padding: 8 12 8 12;"
        );
        tf.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                tf.setStyle(
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6; " +
                        "-fx-border-color: #f57c00; " +
                        "-fx-border-width: 2; " +
                        "-fx-padding: 8 12 8 12;"
                );
            } else {
                tf.setStyle(
                        "-fx-background-radius: 6; " +
                        "-fx-border-radius: 6; " +
                        "-fx-border-color: #ccc; " +
                        "-fx-padding: 8 12 8 12;"
                );
            }
        });
        return tf;
    }
}