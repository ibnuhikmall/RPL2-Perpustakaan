package view;

import controller.LaporanController;
import controller.Sesi;
import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import model.LaporanRow;
import util.AppContext;
import util.LaporanPrinter;

public class PanelLaporan extends JPanel {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final LaporanController lc = AppContext.getBean(LaporanController.class);
    private final JSpinner spAwal = buatSpinner(LocalDate.now().minusDays(30));
    private final JSpinner spAkhir = buatSpinner(LocalDate.now());
    private final JComboBox<String> cbStatus = new JComboBox<>(new String[]{"Semua", "Dipinjam", "Dikembalikan"});
    private final JButton btnTampil = new JButton("Tampilkan"),
                          btnLihat = new JButton("Lihat / Cetak Laporan"),
                          btnPdf = new JButton("Simpan PDF");
    private final JLabel lblRingkas = new JLabel(" ");
    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"No", "Anggota", "Judul Buku", "Tgl Pinjam", "Jatuh Tempo", "Tgl Kembali", "Denda", "Status"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable tabel = new JTable(model);

    private List<LaporanRow> data = new ArrayList<>(); // data yang sedang tampil di tabel
    private String periode = "";                       // contoh: 01-11-2026 s/d 16-11-2026

    public PanelLaporan() {
        setLayout(new BorderLayout(0, 10));
        setBackground(new Color(245, 247, 250));
        setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        JLabel judul = new JLabel("Laporan Peminjaman");
        judul.setFont(new Font("Segoe UI", Font.BOLD, 24));

        JPanel filter = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 6));
        filter.setBackground(Color.WHITE);
        filter.setBorder(BorderFactory.createLineBorder(new Color(210, 215, 222)));
        filter.add(new JLabel("Tanggal pinjam dari"));
        filter.add(spAwal);
        filter.add(new JLabel("sampai"));
        filter.add(spAkhir);
        filter.add(new JLabel("Status"));
        filter.add(cbStatus);
        filter.add(btnTampil);

        JPanel atas = new JPanel(new BorderLayout(0, 10));
        atas.setOpaque(false);
        atas.add(judul, BorderLayout.NORTH);
        atas.add(filter, BorderLayout.CENTER);

        tabel.setRowHeight(24);
        tabel.getTableHeader().setReorderingAllowed(false);
        for (int[] w : new int[][]{{0, 40}, {1, 150}, {2, 220}, {3, 90}, {4, 90}, {5, 90}, {6, 70}, {7, 90}}) { // {kolom, lebar}
            tabel.getColumnModel().getColumn(w[0]).setPreferredWidth(w[1]);
        }

        JPanel tombol = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        tombol.setOpaque(false);
        tombol.add(btnLihat);
        tombol.add(btnPdf);
        JPanel bawah = new JPanel(new BorderLayout());
        bawah.setOpaque(false);
        bawah.add(lblRingkas, BorderLayout.WEST);
        bawah.add(tombol, BorderLayout.EAST);

        add(atas, BorderLayout.NORTH);
        add(new JScrollPane(tabel), BorderLayout.CENTER);
        add(bawah, BorderLayout.SOUTH);

        btnTampil.addActionListener(e -> tampilkan());
        btnLihat.addActionListener(e -> lihat());
        btnPdf.addActionListener(e -> simpanPdf());
    }

    // Kotak tanggal dengan tombol naik-turun (bawaan Swing, tanpa library tambahan)
    private static JSpinner buatSpinner(LocalDate tanggal) {
        Date d = Date.from(tanggal.atStartOfDay(ZoneId.systemDefault()).toInstant());
        JSpinner sp = new JSpinner(new SpinnerDateModel(d, null, null, Calendar.DAY_OF_MONTH));
        sp.setEditor(new JSpinner.DateEditor(sp, "dd-MM-yyyy"));
        return sp;
    }

    private static LocalDate ambilTanggal(JSpinner sp) {
        return ((Date) sp.getValue()).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void pesan(String teks) { JOptionPane.showMessageDialog(this, teks); }

    // ===== Tombol Tampilkan: ambil data sesuai filter, isi tabel =====
    private void tampilkan() {
        LocalDate awal = ambilTanggal(spAwal), akhir = ambilTanggal(spAkhir);
        if (awal.isAfter(akhir)) {
            pesan("Tanggal awal tidak boleh lebih besar dari tanggal akhir.");
            return;
        }
        try {
            String status = (String) cbStatus.getSelectedItem();
            data = lc.peminjaman(awal, akhir, "Semua".equals(status) ? null : status);
            periode = awal.format(FORMAT) + " s/d " + akhir.format(FORMAT);

            model.setRowCount(0);
            int no = 1, totalDenda = 0;
            for (LaporanRow r : data) {
                model.addRow(new Object[]{no++, r.getNamaAnggota(), r.getJudulBuku(), r.getTglPinjam(),
                    r.getTglJatuhTempo(), r.getTglKembali(), r.getDenda(), r.getStatus()});
                totalDenda += r.getDenda();
            }
            lblRingkas.setText(data.size() + " transaksi  |  Total denda: Rp" + String.format("%,d", totalDenda));
            if (data.isEmpty()) pesan("Tidak ada data pada periode tersebut.");
        } catch (Exception e) {
            pesan("Gagal memuat laporan: " + e.getMessage());
        }
    }

    // ===== Tombol Lihat / Cetak: buka pratinjau JasperReports =====
    private void lihat() {
        if (data.isEmpty()) {
            pesan("Tidak ada data untuk dicetak. Pilih periode lalu klik Tampilkan.");
            return;
        }
        try {
            LaporanPrinter.lihat(data, periode, Sesi.nama);
        } catch (Exception | LinkageError e) {
            pesan("Gagal membuka laporan: " + e.getMessage());
        }
    }

    // ===== Tombol Simpan PDF =====
    private void simpanPdf() {
        if (data.isEmpty()) {
            pesan("Tidak ada data untuk disimpan. Pilih periode lalu klik Tampilkan.");
            return;
        }
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new File("laporan_peminjaman.pdf"));
        if (fc.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = fc.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".pdf")) {
            file = new File(file.getParentFile(), file.getName() + ".pdf");
        }
        if (file.exists() && JOptionPane.showConfirmDialog(this, "File sudah ada. Timpa?",
                "Konfirmasi", JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            LaporanPrinter.simpanPdf(data, periode, Sesi.nama, file.getAbsolutePath());
            pesan("Laporan tersimpan di:\n" + file.getAbsolutePath());
        } catch (Exception | LinkageError e) {
            pesan("Gagal menyimpan PDF: " + e.getMessage());
        }
    }
}