package koneksi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;

public class Koneksi {

    private static Connection conn;

    public static Connection getKoneksi() throws SQLException {
        if (conn == null || conn.isClosed()) {
            String url = "jdbc:mysql://localhost:3306/db_perpustakaan";
            conn = DriverManager.getConnection(url, "root", "");
        }
        return conn;
    }

    // Hanya untuk uji coba koneksi
    public static void main(String[] args) {
        try {
            getKoneksi();
            JOptionPane.showMessageDialog(null, "Koneksi berhasil!");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Koneksi gagal: " + e.getMessage());
        }
    }
}