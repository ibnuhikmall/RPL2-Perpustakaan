package view;

import controller.DashboardController;
import controller.Sesi;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class MenuUtama extends JFrame {

    // Nama-nama halaman (dipakai oleh CardLayout)
    private static final String HAL_DASHBOARD = "dashboard";
    private static final String HAL_BUKU = "buku";
    private static final String HAL_ANGGOTA = "anggota";
    private static final String HAL_TRANSAKSI = "transaksi";
    private static final String HAL_LAPORAN = "laporan";
    private static final String HAL_PENGGUNA = "pengguna";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel panelIsi = new JPanel(cardLayout);

    // Angka pada kotak ringkasan dashboard
    private final JLabel lblJmlBuku = new JLabel("0");
    private final JLabel lblJmlAnggota = new JLabel("0");
    private final JLabel lblJmlDipinjam = new JLabel("0");
    private final JLabel lblJmlTerlambat = new JLabel("0");

    private JButton btnKelolaPengguna;
    private PanelBuku panelBuku;

    public MenuUtama() {
        initUI();
        pasangHalaman();
        aturHakAkses();
        muatDashboard();
        setLocationRelativeTo(null);
    }

    // ===== Membuat kerangka tampilan =====
    private void initUI() {
        setTitle("Sistem Manajemen Perpustakaan");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setMinimumSize(new Dimension(900, 520));
        setLayout(new BorderLayout());

        add(buatSidebar(), BorderLayout.WEST);
        panelIsi.setBackground(new Color(245, 247, 250));
        add(panelIsi, BorderLayout.CENTER);
    }

    // ===== Menu di sisi kiri =====
    private JPanel buatSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(230, 0));
        sidebar.setBackground(new Color(33, 47, 61));

        // Bagian atas: judul dan info pengguna
        JPanel atas = new JPanel(new GridLayout(3, 1));
        atas.setOpaque(false);
        atas.setBorder(BorderFactory.createEmptyBorder(20, 15, 15, 15));

        JLabel lblJudul = new JLabel("PERPUSTAKAAN");
        lblJudul.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblJudul.setForeground(Color.WHITE);

        JLabel lblNama = new JLabel(Sesi.nama);
        lblNama.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblNama.setForeground(new Color(200, 210, 220));

        JLabel lblRole = new JLabel("Role: " + Sesi.role);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(150, 165, 180));

        atas.add(lblJudul);
        atas.add(lblNama);
        atas.add(lblRole);
        sidebar.add(atas, BorderLayout.NORTH);

        // Bagian tengah: tombol menu
        JPanel menu = new JPanel(new GridLayout(0, 1, 0, 4));
        menu.setOpaque(false);
        menu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        JButton btnDashboard = buatTombolMenu("Dashboard");
        JButton btnBuku = buatTombolMenu("Data Buku");
        JButton btnAnggota = buatTombolMenu("Data Anggota");
        JButton btnTransaksi = buatTombolMenu("Peminjaman & Pengembalian");
        JButton btnLaporan = buatTombolMenu("Laporan");
        btnKelolaPengguna = buatTombolMenu("Kelola Pengguna");

        btnDashboard.addActionListener(e -> {
            muatDashboard(); // angka diperbarui setiap kali dibuka
            cardLayout.show(panelIsi, HAL_DASHBOARD);
        });
        btnBuku.addActionListener(e -> {
            panelBuku.muatData(); // data diperbarui setiap kali dibuka
            cardLayout.show(panelIsi, HAL_BUKU);
        });
        btnAnggota.addActionListener(e -> cardLayout.show(panelIsi, HAL_ANGGOTA));
        btnTransaksi.addActionListener(e -> cardLayout.show(panelIsi, HAL_TRANSAKSI));
        btnLaporan.addActionListener(e -> cardLayout.show(panelIsi, HAL_LAPORAN));
        btnKelolaPengguna.addActionListener(e -> cardLayout.show(panelIsi, HAL_PENGGUNA));

        menu.add(btnDashboard);
        menu.add(btnBuku);
        menu.add(btnAnggota);
        menu.add(btnTransaksi);
        menu.add(btnLaporan);
        menu.add(btnKelolaPengguna);

        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setOpaque(false);
        pembungkus.add(menu, BorderLayout.NORTH);
        sidebar.add(pembungkus, BorderLayout.CENTER);

        // Bagian bawah: tombol logout
        JButton btnLogout = buatTombolMenu("Logout");
        btnLogout.setBackground(new Color(192, 57, 43));
        btnLogout.addActionListener(e -> logout());

        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.setBorder(BorderFactory.createEmptyBorder(10, 10, 15, 10));
        bawah.add(btnLogout, BorderLayout.CENTER);
        sidebar.add(bawah, BorderLayout.SOUTH);

        return sidebar;
    }

    private JButton buatTombolMenu(String teks) {
        JButton b = new JButton(teks);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        b.setForeground(Color.WHITE);
        b.setBackground(new Color(52, 73, 94));
        b.setFocusPainted(false);
        b.setBorderPainted(false);
        b.setOpaque(true);
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return b;
    }

    // ===== Halaman-halaman di area isi =====
    private void pasangHalaman() {
        panelIsi.add(buatPanelDashboard(), HAL_DASHBOARD);

        panelBuku = new PanelBuku();
        panelIsi.add(panelBuku, HAL_BUKU);

        // Halaman sementara. Nanti diganti dengan panel asli.
        panelIsi.add(buatPanelSementara("Data Anggota"), HAL_ANGGOTA);
        panelIsi.add(buatPanelSementara("Peminjaman dan Pengembalian"), HAL_TRANSAKSI);
        panelIsi.add(buatPanelSementara("Laporan"), HAL_LAPORAN);
        panelIsi.add(buatPanelSementara("Kelola Pengguna"), HAL_PENGGUNA);

        cardLayout.show(panelIsi, HAL_DASHBOARD);
    }

    private JPanel buatPanelDashboard() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        panel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 25));

        JLabel judul = new JLabel("Dashboard");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        judul.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        panel.add(judul, BorderLayout.NORTH);

        JPanel kartu = new JPanel(new GridLayout(1, 4, 15, 15));
        kartu.setOpaque(false);
        kartu.add(buatKartu("Jumlah Buku", lblJmlBuku, new Color(41, 128, 185)));
        kartu.add(buatKartu("Jumlah Anggota", lblJmlAnggota, new Color(39, 174, 96)));
        kartu.add(buatKartu("Sedang Dipinjam", lblJmlDipinjam, new Color(230, 126, 34)));
        kartu.add(buatKartu("Terlambat", lblJmlTerlambat, new Color(192, 57, 43)));

        // Dibungkus agar kotak tidak melar memenuhi seluruh tinggi layar
        JPanel pembungkus = new JPanel(new BorderLayout());
        pembungkus.setOpaque(false);
        pembungkus.add(kartu, BorderLayout.NORTH);
        panel.add(pembungkus, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buatKartu(String judul, JLabel lblNilai, Color warna) {
        JPanel kartu = new JPanel(new GridLayout(2, 1));
        kartu.setBackground(warna);
        kartu.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        kartu.setPreferredSize(new Dimension(0, 130));

        JLabel lblJudul = new JLabel(judul);
        lblJudul.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblJudul.setForeground(Color.WHITE);

        lblNilai.setFont(new Font("Segoe UI", Font.BOLD, 40));
        lblNilai.setForeground(Color.WHITE);

        kartu.add(lblJudul);
        kartu.add(lblNilai);
        return kartu;
    }

    private JPanel buatPanelSementara(String namaHalaman) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 247, 250));
        JLabel lbl = new JLabel("Halaman " + namaHalaman + " (belum dibuat)",
                SwingConstants.CENTER);
        lbl.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        lbl.setForeground(Color.GRAY);
        panel.add(lbl, BorderLayout.CENTER);
        return panel;
    }

    // ===== Pembatasan menu sesuai role =====
    private void aturHakAkses() {
        if (!"Admin".equals(Sesi.role)) {
            btnKelolaPengguna.setVisible(false);
        }
    }

    // ===== Mengisi angka dashboard dari database =====
    private void muatDashboard() {
        try {
            DashboardController dc = new DashboardController();
            lblJmlBuku.setText(String.valueOf(dc.jumlahBuku()));
            lblJmlAnggota.setText(String.valueOf(dc.jumlahAnggota()));
            lblJmlDipinjam.setText(String.valueOf(dc.jumlahDipinjam()));
            lblJmlTerlambat.setText(String.valueOf(dc.jumlahTerlambat()));
        } catch (SQLException e) {
            lblJmlBuku.setText("-");
            lblJmlAnggota.setText("-");
            lblJmlDipinjam.setText("-");
            lblJmlTerlambat.setText("-");
            JOptionPane.showMessageDialog(this,
                    "Gagal memuat data dashboard: " + e.getMessage());
        }
    }

    // ===== Logout =====
    private void logout() {
        int pilihan = JOptionPane.showConfirmDialog(this,
                "Yakin ingin logout?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
        if (pilihan == JOptionPane.YES_OPTION) {
            Sesi.idUser = 0;
            Sesi.nama = null;
            Sesi.role = null;
            new FormLogin().setVisible(true);
            this.dispose();
        }
    }
}