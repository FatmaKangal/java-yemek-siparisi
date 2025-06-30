package veritabani;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Scanner;

public class VeritabaniEkleme {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/foodordersystem";
        String dbUsername = "root";
        String dbPassword = "";

        Scanner scanner = new Scanner(System.in);

        System.out.print("Kullanıcı adınızı girin: ");
        String username = scanner.nextLine();

        System.out.print("Şifrenizi girin: ");
        String password = scanner.nextLine();

        System.out.print("Telefon numaranızı girin: ");
        String phone = scanner.nextLine(); // ← BU SATIR EKLİ OLMALI

        try {
            Connection connection = DriverManager.getConnection(url, dbUsername, dbPassword);

            String sql = "INSERT INTO users(username, password, phone) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, username);
            statement.setString(2, password);
            statement.setString(3, phone);

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Kullanıcı başarıyla eklendi!");
            }

            connection.close();
        } catch (SQLException e) {
            System.out.println("Veritabanı hatası: " + e.getMessage());
        }

        scanner.close();
    }
}