package view;

import util.AppContext;
import controller.BukuController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;

public class PanelBuku extends JPanel {

    // Urutan kolom: 0 Kode, 1 Judul, 2 Pengarang, 3 Penerbit, 4 Tahun, 5 Kategori, 6 Stok
    private static final String[] LABEL = {"Kode Buku", "Judul", "Pengarang", "Penerbit",
                                           "Tahun Terbit", "Kategori", "Stok"};
    private static final int[] LEBAR = {12, 20, 20, 20, 8, 15, 8};

    private final BukuController bc = AppContext.getBean(BukuController.class);
    private final JTextField[] isian = new JTextField[7]; // 7 kolom isian pada form
    private final JTextField txtCari = new JTextField(25);
    private final JButton btnSimpan = new JButton("Simpan"), btnUbah = new JButton("Ubah"),
                          btnHapus = new JButton("Hapus"), btnBersih = new JButton("Bersihkan");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Kode", "Judul", "Pengarang", "Penerbit", "Tahun", "Kategori", "Stok"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabel = new JTable(model);

    public PanelBuku() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel judul = new JLabel("Data Buku");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JPanel atas = new JPanel(new BorderLayout(0, 10));
        atas.setOpaque(false);
        atas.add(judul, BorderLayout.NORTH);
        atas.add(buatForm(), BorderLayout.CENTER);

        add(atas, BorderLayout.NORTH);
        add(buatBagianTabel(), BorderLayout.CENTER);
        aturMode(false);
        muatData();
    }

    // ===== Tampilan form isian =====
    private JPanel buatForm() {
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(210, 215, 222)),
                BorderFactory.createEmptyBorder(12, 15, 12, 15)));

        // 4 isian pertama di kolom kiri, 3 sisanya di kolom kanan
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 5, 4, 10);
        g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < isian.length; i++) {
            isian[i] = new JTextField(LEBAR[i]);
            int kolom = (i < 4) ? 0 : 1;
            g.gridy = (i < 4) ? i : i - 4;

            g.gridx = kolom * 2;
            g.fill = GridBagConstraints.NONE;
            g.weightx = 0;
            form.add(new JLabel(LABEL[i]), g);

            g.gridx = kolom * 2 + 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            g.weightx = 1.0;
            form.add(isian[i], g);
        }

        // Tombol
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tombol.setOpaque(false);
        for (JButton b : new JButton[]{btnSimpan, btnUbah, btnHapus, btnBersih}) {
            tombol.add(b);
        }
        g.gridx = 0; g.gridy = 4; g.gridwidth = 4;
        g.fill = GridBagConstraints.NONE;
        g.weightx = 0;
        g.insets = new Insets(12, 5, 0, 5);
        form.add(tombol, g);

        btnSimpan.addActionListener(e -> proses(true));
        btnUbah.addActionListener(e -> proses(false));
        btnHapus.addActionListener(e -> hapus());
        btnBersih.addActionListener(e -> bersihkanForm());
        return form;
    }

    // ===== Tampilan pencarian + tabel =====
    private JPanel buatBagianTabel() {
        JPanel panelCari = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panelCari.setOpaque(false);
        panelCari.add(new JLabel("Cari (kode, judul, atau pengarang):"));
        panelCari.add(txtCari);

        // Pencarian berjalan otomatis saat mengetik
        txtCari.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { muatData(); }
            @Override public void removeUpdate(DocumentEvent e) { muatData(); }
            @Override public void changedUpdate(DocumentEvent e) { muatData(); }
        });

        tabel.setRowHeight(24);
        tabel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabel.getTableHeader().setReorderingAllowed(false);
        for (int[] w : new int[][]{{0, 60}, {1, 220}, {4, 60}, {6, 50}}) { // {kolom, lebar}
            tabel.getColumnModel().getColumn(w[0]).setPreferredWidth(w[1]);
        }
        // Klik baris -> isi form
        tabel.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) isiFormDariTabel();
        });

        JScrollPane scroll = new JScrollPane(tabel);
        scroll.setPreferredSize(new Dimension(0, 200));

        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.add(panelCari, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    // ===== Memuat data ke tabel (dipanggil juga oleh MenuUtama) =====
    public void muatData() {
        try {
            List<Object[]> data = bc.cariBuku(txtCari.getText().trim());
            model.setRowCount(0);
            for (Object[] baris : data) model.addRow(baris);
        } catch (Exception e) {
            pesan("Gagal memuat data buku: " + e.getMessage());
        }
    }

    private void isiFormDariTabel() {
        int r = tabel.getSelectedRow();
        if (r < 0) return;
        for (int i = 0; i < isian.length; i++) {
            isian[i].setText(String.valueOf(model.getValueAt(r, i)));
        }
        aturMode(true);
    }

    // true = mode ubah (ada baris terpilih), false = mode tambah baru
    private void aturMode(boolean modeUbah) {
        isian[0].setEditable(!modeUbah); // kode tidak boleh diganti saat mengubah
        btnSimpan.setEnabled(!modeUbah);
        btnUbah.setEnabled(modeUbah);
        btnHapus.setEnabled(modeUbah);
    }

    private void bersihkanForm() {
        for (JTextField f : isian) f.setText("");
        tabel.clearSelection();
        aturMode(false);
        isian[0].requestFocus();
    }

    private String isi(int i) { return isian[i].getText().trim(); }

    private void pesan(String teks) { JOptionPane.showMessageDialog(this, teks); }

    // ===== Validasi: mengembalikan pesan error, atau null jika semua benar =====
    private String validasi() {
        for (JTextField f : isian) {
            if (f.getText().trim().isEmpty()) return "Semua kolom wajib diisi.";
        }
        if (isi(0).length() > 10) return "Kode buku maksimal 10 karakter.";
        if (!isi(4).matches("\\d{4}")) return "Tahun terbit harus berupa 4 digit angka, misalnya 2020.";
        if (!isi(6).matches("\\d{1,6}")) return "Stok harus berupa angka 0 atau lebih.";
        return null;
    }

    // ===== Simpan (tambah = true) atau Ubah (tambah = false) =====
    private void proses(boolean tambah) {
        String error = validasi();
        if (error != null) { pesan(error); return; }
        try {
            String kode = isi(0);
            if (tambah && bc.kodeAda(kode)) {
                pesan("Kode buku " + kode + " sudah dipakai.");
                return;
            }
            int tahun = Integer.parseInt(isi(4)), stok = Integer.parseInt(isi(6));
            if (tambah) bc.tambah(kode, isi(1), isi(2), isi(3), tahun, isi(5), stok);
            else        bc.ubah(kode, isi(1), isi(2), isi(3), tahun, isi(5), stok);

            pesan(tambah ? "Data buku berhasil disimpan." : "Data buku berhasil diubah.");
            bersihkanForm();
            muatData();
        } catch (Exception e) {
            pesan((tambah ? "Gagal menyimpan: " : "Gagal mengubah: ") + e.getMessage());
        }
    }

    private void hapus() {
        String kode = isi(0);
        try {
            if (bc.punyaRiwayat(kode)) {
                pesan("Buku ini tidak dapat dihapus karena memiliki riwayat peminjaman.");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(this, "Hapus buku \"" + isi(1) + "\"?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (pilihan == JOptionPane.YES_OPTION) {
                bc.hapus(kode);
                pesan("Data buku berhasil dihapus.");
                bersihkanForm();
                muatData();
            }
        } catch (Exception e) {
            pesan("Gagal menghapus: " + e.getMessage());
        }
    }
}
