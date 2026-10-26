package controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import koneksi.Koneksi;

public class BukuController {

    // Mengambil daftar buku. Jika keyword kosong, semua buku ditampilkan.
    public List<Object[]> cariBuku(String keyword) throws SQLException {
        Connection c = Koneksi.getKoneksi();
        String sql = "SELECT kode_buku, judul, pengarang, penerbit, tahun_terbit, kategori, stok "
                   + "FROM buku "
                   + "WHERE kode_buku LIKE ? OR judul LIKE ? OR pengarang LIKE ? "
                   + "ORDER BY kode_buku";

        List<Object[]> hasil = new ArrayList<>();
        String kata = "%" + keyword + "%";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kata);
            ps.setString(2, kata);
            ps.setString(3, kata);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    hasil.add(new Object[]{
                        rs.getString("kode_buku"),
                        rs.getString("judul"),
                        rs.getString("pengarang"),
                        rs.getString("penerbit"),
                        rs.getInt("tahun_terbit"),
                        rs.getString("kategori"),
                        rs.getInt("stok")
                    });
                }
            }
        }
        return hasil;
    }

    // Cek apakah kode buku sudah dipakai
    public boolean kodeAda(String kode) throws SQLException {
        Connection c = Koneksi.getKoneksi();
        String sql = "SELECT COUNT(*) FROM buku WHERE kode_buku = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    // Cek apakah buku pernah dipinjam (punya riwayat transaksi)
    public boolean punyaRiwayat(String kode) throws SQLException {
        Connection c = Koneksi.getKoneksi();
        String sql = "SELECT COUNT(*) FROM peminjaman WHERE kode_buku = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public void tambah(String kode, String judul, String pengarang, String penerbit,
                       int tahun, String kategori, int stok) throws SQLException {
        Connection c = Koneksi.getKoneksi();
        String sql = "INSERT INTO buku (kode_buku, judul, pengarang, penerbit, "
                   + "tahun_terbit, kategori, stok) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.setString(2, judul);
            ps.setString(3, pengarang);
            ps.setString(4, penerbit);
            ps.setInt(5, tahun);
            ps.setString(6, kategori);
            ps.setInt(7, stok);
            ps.executeUpdate();
        }
    }

    public void ubah(String kode, String judul, String pengarang, String penerbit,
                     int tahun, String kategori, int stok) throws SQLException {
        Connection c = Koneksi.getKoneksi();
        String sql = "UPDATE buku SET judul = ?, pengarang = ?, penerbit = ?, "
                   + "tahun_terbit = ?, kategori = ?, stok = ? WHERE kode_buku = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, judul);
            ps.setString(2, pengarang);
            ps.setString(3, penerbit);
            ps.setInt(4, tahun);
            ps.setString(5, kategori);
            ps.setInt(6, stok);
            ps.setString(7, kode);
            ps.executeUpdate();
        }
    }

    public void hapus(String kode) throws SQLException {
        Connection c = Koneksi.getKoneksi();
        String sql = "DELETE FROM buku WHERE kode_buku = ?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, kode);
            ps.executeUpdate();
        }
    }
}