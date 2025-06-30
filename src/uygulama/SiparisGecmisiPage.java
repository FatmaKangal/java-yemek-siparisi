package uygulama;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.sql.*;

public class SiparisGecmisiPage {

    private Stage stage;
    private int userId;
    private String userName;
    private String userBolum;

    public SiparisGecmisiPage(Stage stage, int userId, String userName, String userBolum) {
        this.stage = stage;
        this.userId = userId;
        this.userName = userName;
        this.userBolum = userBolum;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));

        Label title = new Label("Eski Siparişler");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        BorderPane.setAlignment(title, Pos.CENTER);
        root.setTop(title);

        TableView<Order> table = new TableView<>();

        TableColumn<Order, Integer> idCol = new TableColumn<>("Sipariş No");
        idCol.setCellValueFactory(data -> data.getValue().orderIdProperty().asObject());

        TableColumn<Order, String> dateCol = new TableColumn<>("Tarih");
        dateCol.setCellValueFactory(data -> data.getValue().orderDateProperty());

        TableColumn<Order, Double> priceCol = new TableColumn<>("Toplam Fiyat");
        priceCol.setCellValueFactory(data -> data.getValue().totalPriceProperty().asObject());

        TableColumn<Order, String> addressCol = new TableColumn<>("Adres");
        addressCol.setCellValueFactory(data -> data.getValue().addressProperty());

        table.getColumns().addAll(idCol, dateCol, priceCol, addressCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // 🔄 MYSQL veritabanı bağlantısı
        String url = "jdbc:mysql://localhost:3306/foodordersystem";
        String dbUsername = "root";
        String dbPassword = "";

        try (Connection conn = DriverManager.getConnection(url, dbUsername, dbPassword)) {
            String sql = "SELECT order_id, order_date, total_price, address FROM orders WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                String orderDate = rs.getString("order_date");
                double totalPrice = rs.getDouble("total_price");
                String address = rs.getString("address");

                table.getItems().add(new Order(orderId, orderDate, totalPrice, address));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Siparişler yüklenirken hata oluştu.");
            alert.showAndWait();
        }

        Button geriButton = new Button("⬅ Geri Dön");
        geriButton.setOnAction(e -> new LokantaSecimPage(stage, userId, userName, userBolum).show());

        VBox centerBox = new VBox(15, table, geriButton);
        centerBox.setPadding(new Insets(10));
        centerBox.setAlignment(Pos.CENTER);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 700, 500);
        stage.setScene(scene);
        stage.setTitle("Eski Siparişler");
        stage.show();
    }

    // Sipariş modeli (JavaFX Property kullanarak)
    public static class Order {
        private final javafx.beans.property.IntegerProperty orderId;
        private final javafx.beans.property.StringProperty orderDate;
        private final javafx.beans.property.DoubleProperty totalPrice;
        private final javafx.beans.property.StringProperty address;

        public Order(int orderId, String orderDate, double totalPrice, String address) {
            this.orderId = new javafx.beans.property.SimpleIntegerProperty(orderId);
            this.orderDate = new javafx.beans.property.SimpleStringProperty(orderDate);
            this.totalPrice = new javafx.beans.property.SimpleDoubleProperty(totalPrice);
            this.address = new javafx.beans.property.SimpleStringProperty(address);
        }

        public javafx.beans.property.IntegerProperty orderIdProperty() { return orderId; }
        public javafx.beans.property.StringProperty orderDateProperty() { return orderDate; }
        public javafx.beans.property.DoubleProperty totalPriceProperty() { return totalPrice; }
        public javafx.beans.property.StringProperty addressProperty() { return address; }
    }
}