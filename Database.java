import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    // Sesuaikan username dan password database Anda di sini
    private static final String URL = "jdbc:mysql://localhost:3306/manajemenperpus";
    private static final String USER = "root";
    private static final String PASS = "1234";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Koneksi Error: " + e.getMessage());
            return null;
        }
    }
}