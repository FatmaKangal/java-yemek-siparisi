package uygulama;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Map;

public class SepetPage extends Application {
    private int userId;
    private Map<String, SepetUrun> sepet;

    public SepetPage() {}

    public SepetPage(int userId, Map<String, SepetUrun> sepet) {
        this.userId = userId;
        this.sepet = sepet;
    }

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #fafafa;");

        Label title = new Label("Sepetiniz");
        title.setStyle("-fx-font-size: 28px; -fx-text-fill: #f57c00; -fx-font-weight: bold;");
        root.getChildren().add(title);

        if (sepet == null || sepet.isEmpty()) {
            Label emptyLabel = new Label("Sepetiniz boş.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #888;");
            root.setAlignment(Pos.CENTER);
            root.getChildren().add(emptyLabel);
        } else {
            VBox urunListesi = new VBox(10);
            urunListesi.setPadding(new Insets(10, 0, 10, 0));

            for (SepetUrun urun : sepet.values()) {
                HBox hbox = new HBox(15);
                hbox.setPadding(new Insets(10));
                hbox.setStyle("-fx-background-color: white; -fx-border-color: #ddd; -fx-border-radius: 8; -fx-background-radius: 8;");
                hbox.setAlignment(Pos.CENTER_LEFT);

                Label nameLabel = new Label(urun.getName());
                nameLabel.setPrefWidth(150);
                nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: 600;");

                Label adetLabel = new Label("Adet: " + urun.getAdet());
                adetLabel.setPrefWidth(70);
                adetLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #555;");

                Label priceLabel = new Label(String.format("Fiyat: %.2f ₺", urun.getPrice() * urun.getAdet()));
                priceLabel.setPrefWidth(120);
                priceLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #333;");

                Button increaseBtn = new Button("+");
                increaseBtn.setStyle(buttonStyle());
                increaseBtn.setOnAction(e -> {
                    urun.setAdet(urun.getAdet() + 1);
                    start(primaryStage);
                });

                Button decreaseBtn = new Button("-");
                decreaseBtn.setStyle(buttonStyle());
                decreaseBtn.setOnAction(e -> {
                    if (urun.getAdet() > 1) {
                        urun.setAdet(urun.getAdet() - 1);
                    } else {
                        sepet.remove(urun.getName());
                    }
                    start(primaryStage);
                });

                hbox.getChildren().addAll(nameLabel, adetLabel, priceLabel, increaseBtn, decreaseBtn);
                urunListesi.getChildren().add(hbox);
            }

            root.getChildren().add(urunListesi);

            double total = sepet.values().stream()
                    .mapToDouble(u -> u.getPrice() * u.getAdet())
                    .sum();

            Label totalLabel = new Label(String.format("Toplam Tutar: %.2f ₺", total));
            totalLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #444;");
            totalLabel.setPadding(new Insets(10, 0, 10, 0));

            Button payButton = new Button("Ödeme Sayfasına Git");
            payButton.setStyle("-fx-background-color: #f57c00; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-padding: 10 20 10 20; -fx-background-radius: 8;");
            payButton.setOnAction(e -> {
                OdemeSayfasi odeme = new OdemeSayfasi(userId, sepet);
                Stage odemeStage = new Stage();
                try {
                    odeme.start(odemeStage);
                    primaryStage.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });

            HBox bottomBox = new HBox(20, totalLabel, payButton);
            bottomBox.setAlignment(Pos.CENTER_RIGHT);
            root.getChildren().add(bottomBox);
        }

        Scene scene = new Scene(root, 450, 500);
        primaryStage.setTitle("Sepet Sayfası");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String buttonStyle() {
        return "-fx-background-color: #f57c00; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;";
    }
}