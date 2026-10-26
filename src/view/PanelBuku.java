package view;

import controller.BukuController;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.sql.SQLException;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class PanelBuku extends JPanel {

    private final BukuController bc = new BukuController();

    private JTextField txtKode, txtJudul, txtPengarang, txtPenerbit;
    private JTextField txtTahun, txtKategori, txtStok, txtCari;
    private JButton btnSimpan, btnUbah, btnHapus, btnBersih;
    private JTable tabel;
    private DefaultTableModel model;

    public PanelBuku() {
        initUI();
        aturMode(false);
        muatData();
    }

    // ===== Membuat tampilan =====
    private void initUI() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Bagian atas: judul + form isian
        JPanel atas = new JPanel(new BorderLayout(0, 10));
        atas.setOpaque(false);

        JLabel judul = new JLabel("Data Buku");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        atas.add(judul, BorderLayout.NORTH);
        atas.add(buatForm(), BorderLayout.CENTER);
        add(atas, BorderLayout.NORTH);

        // Bagian tengah: pencarian + tabel
        add(buatBagianTabel(), BorderLayout.CENTER);
    }

    private JPanel buatForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 222)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        txtKode = new JTextField(12);
        txtJudul = new JTextField(20);
        txtPengarang = new JTextField(20);
        txtPenerbit = new JTextField(20);
        txtTahun = new JTextField(8);
        txtKategori = new JTextField(15);
        txtStok = new JTextField(8);

        // Kolom kiri
        tambahBaris(form, 0, 0, "Kode Buku", txtKode);
        tambahBaris(form, 0, 1, "Judul", txtJudul);
        tambahBaris(form, 0, 2, "Pengarang", txtPengarang);
        tambahBaris(form, 0, 3, "Penerbit", txtPenerbit);

        // Kolom kanan
        tambahBaris(form, 1, 0, "Tahun Terbit", txtTahun);
        tambahBaris(form, 1, 1, "Kategori", txtKategori);
        tambahBaris(form, 1, 2, "Stok", txtStok);

        // Tombol
        btnSimpan = new JButton("Simpan");
        btnUbah = new JButton("Ubah");
        btnHapus = new JButton("Hapus");
        btnBersih = new JButton("Bersihkan");

        JPanel panelTombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelTombol.setOpaque(false);
        panelTombol.add(btnSimpan);
        panelTombol.add(btnUbah);
        panelTombol.add(btnHapus);
        panelTombol.add(btnBersih);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(12, 5, 0, 5);
        form.add(panelTombol, gbc);

        btnSimpan.addActionListener(e -> simpan());
        btnUbah.addActionListener(e -> ubah());
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkanForm());

        return form;
    }

    // Menaruh satu pasang label + isian pada grid
    private void tambahBaris(JPanel p, int kolom, int baris, String label, JComponent isian) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 5, 4, 10);
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridx = kolom * 2;
        gbc.gridy = baris;
        p.add(new JLabel(label), gbc);

        gbc.gridx = kolom * 2 + 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        p.add(isian, gbc);
    }

    private JPanel buatBagianTabel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);

        // Kotak pencarian
        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelCari.setOpaque(false);
        panelCari.add(new JLabel("Cari (kode, judul, atau pengarang):"));
        txtCari = new JTextField(25);
        panelCari.add(txtCari);
        panel.add(panelCari, BorderLayout.NORTH);

        // Pencarian berjalan otomatis saat mengetik
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { muatData(); }
            @Override public void removeUpdate(DocumentEvent e) { muatData(); }
            @Override public void changedUpdate(DocumentEvent e) { muatData(); }
        });

        // Tabel
        model = new DefaultTableModel(
                new String[]{"Kode", "Judul", "Pengarang", "Penerbit", "Tahun", "Kategori", "Stok"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // tabel hanya untuk dibaca
            }
        };
        tabel = new JTable(model);
        tabel.setRowHeight(24);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getTableHeader().setReorderingAllowed(false);
        tabel.getColumnModel().getColumn(0).setPreferredWidth(60);
        tabel.getColumnModel().getColumn(1).setPreferredWidth(220);
        tabel.getColumnModel().getColumn(4).setPreferredWidth(60);
        tabel.getColumnModel().getColumn(6).setPreferredWidth(50);

        // Klik baris -> isi form
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                isiFormDariTabel();
            }
        });

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setPreferredSize(new Dimension(0, 200));
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ===== Memuat data ke tabel (dipanggil juga oleh MenuUtama) =====
    public void muatData() {
        try {
            String keyword = txtCari.getText().trim();
            List<Object[]> data = bc.cariBuku(keyword);
            model.setRowCount(0);
            for (Object[] baris : data) {
                model.addRow(baris);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data buku: " + e.getMessage());
        }
    }

    private void isiFormDariTabel() {
        int r = tabel.getSelectedRow();
        if (r < 0) {
            return;
        }
        txtKode.setText(String.valueOf(model.getValueAt(r, 0)));
        txtJudul.setText(String.valueOf(model.getValueAt(r, 1)));
        txtPengarang.setText(String.valueOf(model.getValueAt(r, 2)));
        txtPenerbit.setText(String.valueOf(model.getValueAt(r, 3)));
        txtTahun.setText(String.valueOf(model.getValueAt(r, 4)));
        txtKategori.setText(String.valueOf(model.getValueAt(r, 5)));
        txtStok.setText(String.valueOf(model.getValueAt(r, 6)));
        aturMode(true);
    }

    // true = mode ubah (ada baris terpilih), false = mode tambah baru
    private void aturMode(boolean modeUbah) {
        txtKode.setEditable(!modeUbah); // kode tidak boleh diganti saat mengubah
        btnSimpan.setEnabled(!modeUbah);
        btnUbah.setEnabled(modeUbah);
        btnHapus.setEnabled(modeUbah);
    }

    private void bersihkanForm() {
        txtKode.setText("");
        txtJudul.setText("");
        txtPengarang.setText("");
        txtPenerbit.setText("");
        txtTahun.setText("");
        txtKategori.setText("");
        txtStok.setText("");
        tabel.clearSelection();
        aturMode(false);
        txtKode.requestFocus();
    }

    // ===== Validasi: mengembalikan pesan error, atau null jika semua benar =====
    private String validasi() {
        if (txtKode.getText().trim().isEmpty()
                || txtJudul.getText().trim().isEmpty()
                || txtPengarang.getText().trim().isEmpty()
                || txtPenerbit.getText().trim().isEmpty()
                || txtTahun.getText().trim().isEmpty()
                || txtKategori.getText().trim().isEmpty()
                || txtStok.getText().trim().isEmpty()) {
            return "Semua kolom wajib diisi.";
        }
        if (txtKode.getText().trim().length() > 10) {
            return "Kode buku maksimal 10 karakter.";
        }
        if (!txtTahun.getText().trim().matches("\\d{4}")) {
            return "Tahun terbit harus berupa 4 digit angka, misalnya 2020.";
        }
        if (!txtStok.getText().trim().matches("\\d{1,6}")) {
            return "Stok harus berupa angka 0 atau lebih.";
        }
        return null;
    }

    // ===== Aksi tombol =====
    private void simpan() {
        String error = validasi();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error);
            return;
        }
        try {
            String kode = txtKode.getText().trim();
            if (bc.kodeAda(kode)) {
                JOptionPane.showMessageDialog(this, "Kode buku " + kode + " sudah dipakai.");
                return;
            }
            bc.tambah(kode,
                    txtJudul.getText().trim(),
                    txtPengarang.getText().trim(),
                    txtPenerbit.getText().trim(),
                    Integer.parseInt(txtTahun.getText().trim()),
                    txtKategori.getText().trim(),
                    Integer.parseInt(txtStok.getText().trim()));
            JOptionPane.showMessageDialog(this, "Data buku berhasil disimpan.");
            bersihkanForm();
            muatData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + e.getMessage());
        }
    }

    private void ubah() {
        String error = validasi();
        if (error != null) {
            JOptionPane.showMessageDialog(this, error);
            return;
        }
        try {
            bc.ubah(txtKode.getText().trim(),
                    txtJudul.getText().trim(),
                    txtPengarang.getText().trim(),
                    txtPenerbit.getText().trim(),
                    Integer.parseInt(txtTahun.getText().trim()),
                    txtKategori.getText().trim(),
                    Integer.parseInt(txtStok.getText().trim()));
            JOptionPane.showMessageDialog(this, "Data buku berhasil diubah.");
            bersihkanForm();
            muatData();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal mengubah: " + e.getMessage());
        }
    }

    private void hapus() {
        String kode = txtKode.getText().trim();
        try {
            if (bc.punyaRiwayat(kode)) {
                JOptionPane.showMessageDialog(this,
                        "Buku ini tidak dapat dihapus karena memiliki riwayat peminjaman.");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(this,
                    "Hapus buku \"" + txtJudul.getText().trim() + "\"?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (pilihan == JOptionPane.YES_OPTION) {
                bc.hapus(kode);
                JOptionPane.showMessageDialog(this, "Data buku berhasil dihapus.");
                bersihkanForm();
                muatData();
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus: " + e.getMessage());
        }
    }
}