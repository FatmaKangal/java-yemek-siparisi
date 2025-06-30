package uygulama;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;

public class LokantaSecimPage {

    private Stage stage;
    private int userId;
    private String userName;
    private String userBolum;

    public LokantaSecimPage(Stage stage, int userId, String userName, String userBolum) {
        this.stage = stage;
        this.userId = userId;
        this.userName = userName;
        this.userBolum = userBolum;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-font-family: 'Segoe UI'; -fx-background-color: #E8F6F3;"); // Açık turkuaz arka plan

        // Üst bar
        HBox topBar = new HBox(15);
        topBar.setAlignment(Pos.CENTER_RIGHT);
        topBar.setPadding(new Insets(12, 20, 12, 20));
        topBar.setStyle("-fx-background-color: #2C7873; -fx-background-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 1);");

        // Burada userBolum null veya boş ise sadece userName göster
        String displayName = userName;
        if (userBolum != null && !userBolum.isBlank()) {
            displayName += " - " + userBolum;
        }
        Label userInfo = new Label("👤 " + displayName);
        userInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: white; -fx-font-weight: 600;");

        Button hesapBtn = new Button("Hesabım");
        Button siparisBtn = new Button("Eski Siparişler");
        Button cikisBtn = new Button("Çıkış Yap");

        styleMenuButton(hesapBtn);
        styleMenuButton(siparisBtn);
        styleMenuButton(cikisBtn);

        hesapBtn.setOnAction(e -> {
            HesapSayfasi hesapSayfasi = new HesapSayfasi(stage, userId, userName, userBolum);
            hesapSayfasi.show();
        });

        cikisBtn.setOnAction(e -> {
            System.out.println("Çıkış yapılıyor...");
            // Giriş sayfasına yönlendirme kodunu buraya ekleyebilirsin
        });

        siparisBtn.setOnAction(e -> new SiparisGecmisiPage(stage, userId, userName, userBolum).show());

        topBar.getChildren().addAll(userInfo, hesapBtn, siparisBtn, cikisBtn);
        root.setTop(topBar);

        // İçerik bölümü
        VBox contentBox = new VBox(25);
        contentBox.setAlignment(Pos.TOP_CENTER);
        contentBox.setPadding(new Insets(25, 0, 0, 0));

        Label title = new Label("Bir Lokanta Seçin");
        title.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #0B525B;");

        HBox lokantaBox = new HBox(30);
        lokantaBox.setAlignment(Pos.CENTER);
        lokantaBox.setPadding(new Insets(10));

        VBox fastFoodBox = createLokantaCard("Çıtır Fast Food", "/images/FastFood.jpeg", () -> 
            new FastFoodPage(stage, userId, userName).show()
        );

        VBox anaYemekBox = createLokantaCard("Yasuka Lokantası", "/images/anayemekci.jpeg", () -> {
            MenuPage menuPage = new MenuPage(stage);
            menuPage.setUserId(userId);
            menuPage.setUserName(userName);
            menuPage.setUserBolum(userBolum);
            menuPage.show();
        });

        VBox tatliBox = createLokantaCard("Ayışığı Tatlı Dükkanı", "/images/tatlici.jpeg", () -> 
            new TatliciPage(stage, userId, userName, userBolum).show()
        );

        lokantaBox.getChildren().addAll(fastFoodBox, anaYemekBox, tatliBox);

        ScrollPane scrollPane = new ScrollPane(lokantaBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-padding: 10 0 10 0;");

        contentBox.getChildren().addAll(title, scrollPane);
        root.setCenter(contentBox);

        Scene scene = new Scene(root, 960, 640);
        stage.setScene(scene);
        stage.setTitle("Lokanta Seçimi");
        stage.show();
    }

    private VBox createLokantaCard(String lokantaAdi, String imagePath, Runnable onClick) {
        VBox box = new VBox(12);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));
        box.setPrefWidth(240);
        box.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-color: #A7C7C5;
            -fx-border-radius: 18;
            -fx-border-width: 1.5;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 4);
            -fx-cursor: hand;
            -fx-transition: all 0.3s ease;
        """);

        URL imageUrl = getClass().getResource(imagePath);
        Image image = (imageUrl != null) ? new Image(imageUrl.toExternalForm()) : null;
        ImageView imageView = (image != null) ? new ImageView(image) : new ImageView();
        imageView.setFitWidth(200);
        imageView.setFitHeight(130);
        imageView.setPreserveRatio(true);
        imageView.setSmooth(true);
        imageView.setCache(true);
        imageView.setStyle("-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 5, 0, 0, 3);");

        Label nameLabel = new Label(lokantaAdi);
        nameLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: 700; -fx-text-fill: #0B525B;");

        Button menuButton = new Button("Menüye Git");
        menuButton.setStyle("""
            -fx-background-color: #1F8A70;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 14;
            -fx-padding: 10 22;
            -fx-font-size: 14px;
            -fx-cursor: hand;
        """);

        menuButton.setOnMouseEntered(e -> menuButton.setStyle("""
            -fx-background-color: #14745F;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 14;
            -fx-padding: 10 22;
            -fx-font-size: 14px;
            -fx-cursor: hand;
        """));

        menuButton.setOnMouseExited(e -> menuButton.setStyle("""
            -fx-background-color: #1F8A70;
            -fx-text-fill: white;
            -fx-font-weight: bold;
            -fx-background-radius: 14;
            -fx-padding: 10 22;
            -fx-font-size: 14px;
            -fx-cursor: hand;
        """));

        menuButton.setOnAction(e -> onClick.run());

        // Kartın hover efekti: kutu gölgesi büyür ve renk hafif değişir
        box.setOnMouseEntered(e -> box.setStyle("""
            -fx-background-color: #F0FAF9;
            -fx-background-radius: 18;
            -fx-border-color: #14977E;
            -fx-border-radius: 18;
            -fx-border-width: 1.8;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.22), 16, 0, 0, 7);
            -fx-cursor: hand;
        """));
        box.setOnMouseExited(e -> box.setStyle("""
            -fx-background-color: white;
            -fx-background-radius: 18;
            -fx-border-color: #A7C7C5;
            -fx-border-radius: 18;
            -fx-border-width: 1.5;
            -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 12, 0, 0, 4);
            -fx-cursor: hand;
        """));

        box.getChildren().addAll(imageView, nameLabel, menuButton);
        return box;
    }

    private void styleMenuButton(Button button) {
        button.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: 600;
            -fx-underline: false;
            -fx-cursor: hand;
            -fx-padding: 6 10;
        """);
        button.setOnMouseEntered(e -> button.setStyle("""
            -fx-background-color: #1EAEA3;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: 600;
            -fx-underline: false;
            -fx-cursor: hand;
            -fx-padding: 6 10;
        """));
        button.setOnMouseExited(e -> button.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: white;
            -fx-font-size: 13px;
            -fx-font-weight: 600;
            -fx-underline: false;
            -fx-cursor: hand;
            -fx-padding: 6 10;
        """));
    }
}