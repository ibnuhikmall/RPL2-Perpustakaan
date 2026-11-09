package view;

import controller.AnggotaController;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import util.AppContext;

public class PanelAnggota extends JPanel {

    private static final String[] LABEL = {"No Anggota", "Nama", "Alamat", "No HP", "Status"};

    private final AnggotaController ac = AppContext.getBean(AnggotaController.class);
    private final JTextField txtNo = new JTextField(12), txtNama = new JTextField(20),
                             txtAlamat = new JTextField(20), txtHp = new JTextField(15),
                             txtCari = new JTextField(25);
    private final JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Aktif", "Nonaktif"});
    private final JButton btnSimpan = new JButton("Simpan"), btnUbah = new JButton("Ubah"),
                          btnHapus = new JButton("Hapus"), btnBersih = new JButton("Bersihkan");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"No Anggota", "Nama", "Alamat", "No HP", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabel = new JTable(model);

    public PanelAnggota() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel judul = new JLabel("Data Anggota");
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

        // 3 isian pertama di kolom kiri, 2 sisanya di kolom kanan
        JComponent[] komponen = {txtNo, txtNama, txtAlamat, txtHp, cbStatus};
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(4, 5, 4, 10);
        g.anchor = GridBagConstraints.WEST;
        for (int i = 0; i < komponen.length; i++) {
            int kolom = (i < 3) ? 0 : 1;
            g.gridy = (i < 3) ? i : i - 3;

            g.gridx = kolom * 2;
            g.fill = GridBagConstraints.NONE;
            g.weightx = 0;
            form.add(new JLabel(LABEL[i]), g);

            g.gridx = kolom * 2 + 1;
            g.fill = GridBagConstraints.HORIZONTAL;
            g.weightx = 1.0;
            form.add(komponen[i], g);
        }

        // Tombol
        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        tombol.setOpaque(false);
        for (JButton b : new JButton[]{btnSimpan, btnUbah, btnHapus, btnBersih}) {
            tombol.add(b);
        }
        g.gridx = 0; g.gridy = 3; g.gridwidth = 4;
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
        panelCari.add(new JLabel("Cari (nomor atau nama anggota):"));
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
        for (int[] w : new int[][]{{0, 80}, {1, 180}, {2, 240}, {3, 110}, {4, 80}}) { // {kolom, lebar}
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
            List<Object[]> data = ac.cariAnggota(txtCari.getText().trim());
            model.setRowCount(0);
            for (Object[] baris : data) model.addRow(baris);
        } catch (Exception e) {
            pesan("Gagal memuat data anggota: " + e.getMessage());
        }
    }

    private void isiFormDariTabel() {
        int r = tabel.getSelectedRow();
        if (r < 0) return;
        txtNo.setText(String.valueOf(model.getValueAt(r, 0)));
        txtNama.setText(String.valueOf(model.getValueAt(r, 1)));
        txtAlamat.setText(String.valueOf(model.getValueAt(r, 2)));
        txtHp.setText(String.valueOf(model.getValueAt(r, 3)));
        cbStatus.setSelectedItem(model.getValueAt(r, 4));
        aturMode(true);
    }

    // true = mode ubah (ada baris terpilih), false = mode tambah baru
    private void aturMode(boolean modeUbah) {
        txtNo.setEditable(!modeUbah); // nomor anggota tidak boleh diganti saat mengubah
        btnSimpan.setEnabled(!modeUbah);
        btnUbah.setEnabled(modeUbah);
        btnHapus.setEnabled(modeUbah);
    }

    private void bersihkanForm() {
        for (JTextField f : new JTextField[]{txtNo, txtNama, txtAlamat, txtHp}) f.setText("");
        cbStatus.setSelectedIndex(0);
        tabel.clearSelection();
        aturMode(false);
        txtNo.requestFocus();
    }

    private String isi(JTextField f) { return f.getText().trim(); }

    private void pesan(String teks) { JOptionPane.showMessageDialog(this, teks); }

    // ===== Validasi: mengembalikan pesan error, atau null jika semua benar =====
    private String validasi() {
        if (isi(txtNo).isEmpty() || isi(txtNama).isEmpty()) return "Nomor anggota dan nama wajib diisi.";
        if (isi(txtNo).length() > 10) return "Nomor anggota maksimal 10 karakter.";
        if (isi(txtNama).length() > 100) return "Nama maksimal 100 karakter.";
        if (!isi(txtHp).isEmpty() && !isi(txtHp).matches("\\d{8,15}")) {
            return "Nomor HP harus berupa 8 sampai 15 digit angka.";
        }
        return null;
    }

    // ===== Simpan (tambah = true) atau Ubah (tambah = false) =====
    private void proses(boolean tambah) {
        String error = validasi();
        if (error != null) { pesan(error); return; }
        try {
            String no = isi(txtNo);
            if (tambah && ac.noAda(no)) {
                pesan("Nomor anggota " + no + " sudah dipakai.");
                return;
            }
            String status = (String) cbStatus.getSelectedItem();
            if (tambah) ac.tambah(no, isi(txtNama), isi(txtAlamat), isi(txtHp), status);
            else        ac.ubah(no, isi(txtNama), isi(txtAlamat), isi(txtHp), status);

            pesan(tambah ? "Data anggota berhasil disimpan." : "Data anggota berhasil diubah.");
            bersihkanForm();
            muatData();
        } catch (Exception e) {
            pesan((tambah ? "Gagal menyimpan: " : "Gagal mengubah: ") + e.getMessage());
        }
    }

    private void hapus() {
        String no = isi(txtNo);
        try {
            if (ac.punyaRiwayat(no)) {
                pesan("Anggota ini tidak dapat dihapus karena memiliki riwayat peminjaman.\n"
                    + "Ubah statusnya menjadi Nonaktif jika sudah tidak aktif.");
                return;
            }
            int pilihan = JOptionPane.showConfirmDialog(this, "Hapus anggota \"" + isi(txtNama) + "\"?",
                    "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (pilihan == JOptionPane.YES_OPTION) {
                ac.hapus(no);
                pesan("Data anggota berhasil dihapus.");
                bersihkanForm();
                muatData();
            }
        } catch (Exception e) {
            pesan("Gagal menghapus: " + e.getMessage());
        }
    }
}