package controller;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import koneksi.Koneksi;

public class DashboardController {

    public int jumlahBuku() throws SQLException {
        return hitung("SELECT COUNT(*) FROM buku");
    }

    public int jumlahAnggota() throws SQLException {
        return hitung("SELECT COUNT(*) FROM anggota");
    }

    public int jumlahDipinjam() throws SQLException {
        return hitung("SELECT COUNT(*) FROM peminjaman WHERE status = 'Dipinjam'");
    }

    public int jumlahTerlambat() throws SQLException {
        return hitung("SELECT COUNT(*) FROM peminjaman "
                    + "WHERE status = 'Dipinjam' AND tgl_jatuh_tempo < CURDATE()");
    }

    // Menjalankan query COUNT(*) dan mengembalikan angkanya
    private int hitung(String sql) throws SQLException {
        Connection c = Koneksi.getKoneksi();
        try (Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }
}