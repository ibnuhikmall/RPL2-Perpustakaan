package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import koneksi.Koneksi;

public class UserController {

    // Mengembalikan true jika username dan password benar
    public boolean login(String username, String password) throws SQLException {
        Connection c = Koneksi.getKoneksi();

        String sql = "SELECT id_user, nama_lengkap, role FROM users "
                   + "WHERE username = ? AND password = SHA2(?, 256)";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Sesi.idUser = rs.getInt("id_user");
                    Sesi.nama = rs.getString("nama_lengkap");
                    Sesi.role = rs.getString("role");
                    return true;
                }
            }
        }
        return false;
    }
}