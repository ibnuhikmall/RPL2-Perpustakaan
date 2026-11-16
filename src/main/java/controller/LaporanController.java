package controller;

import dao.LaporanDao;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import model.LaporanRow;
import model.Peminjaman;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LaporanController {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    private final LaporanDao dao;

    public LaporanController(LaporanDao dao) {
        this.dao = dao;
    }

    // Mengubah data Peminjaman (entity) menjadi baris laporan yang siap dicetak
    @Transactional(readOnly = true)
    public List<LaporanRow> peminjaman(LocalDate awal, LocalDate akhir, String status) {
        List<LaporanRow> hasil = new ArrayList<>();
        for (Peminjaman p : dao.cariPeminjaman(awal, akhir, status)) {
            LaporanRow r = new LaporanRow();
            r.setIdPinjam(p.getIdPinjam());
            r.setNoAnggota(p.getAnggota().getNoAnggota());
            r.setNamaAnggota(p.getAnggota().getNama());
            r.setKodeBuku(p.getBuku().getKodeBuku());
            r.setJudulBuku(p.getBuku().getJudul());
            r.setTglPinjam(p.getTglPinjam().format(FORMAT));
            r.setTglJatuhTempo(p.getTglJatuhTempo().format(FORMAT));
            r.setTglKembali(p.getTglKembali() == null ? "-" : p.getTglKembali().format(FORMAT));
            r.setDenda(p.getDenda());
            r.setStatus(p.getStatus());
            hasil.add(r);
        }
        return hasil;
    }
}