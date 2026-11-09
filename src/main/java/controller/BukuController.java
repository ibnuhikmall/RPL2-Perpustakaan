package controller;

import dao.BukuDao;
import java.util.ArrayList;
import java.util.List;
import model.Buku;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BukuController {

    private final BukuDao dao;

    public BukuController(BukuDao dao) {
        this.dao = dao;
    }

    @Transactional(readOnly = true)
    public List<Object[]> cariBuku(String keyword) {
        List<Object[]> hasil = new ArrayList<>();
        for (Buku b : dao.cari(keyword)) {
            hasil.add(new Object[]{b.getKodeBuku(), b.getJudul(), b.getPengarang(),
                b.getPenerbit(), b.getTahunTerbit(), b.getKategori(), b.getStok()});
        }
        return hasil;
    }

    @Transactional(readOnly = true)
    public boolean kodeAda(String kode) {
        return dao.cariByKode(kode) != null;
    }

    @Transactional(readOnly = true)
    public boolean punyaRiwayat(String kode) {
        return dao.punyaRiwayat(kode);
    }

    @Transactional
    public void tambah(String kode, String judul, String pengarang, String penerbit,
                       int tahun, String kategori, int stok) {
        dao.tambah(buatBuku(kode, judul, pengarang, penerbit, tahun, kategori, stok));
    }

    @Transactional
    public void ubah(String kode, String judul, String pengarang, String penerbit,
                     int tahun, String kategori, int stok) {
        dao.ubah(buatBuku(kode, judul, pengarang, penerbit, tahun, kategori, stok));
    }

    @Transactional
    public void hapus(String kode) {
        dao.hapus(kode);
    }

    private Buku buatBuku(String kode, String judul, String pengarang, String penerbit,
                          int tahun, String kategori, int stok) {
        Buku b = new Buku();
        b.setKodeBuku(kode);
        b.setJudul(judul);
        b.setPengarang(pengarang);
        b.setPenerbit(penerbit);
        b.setTahunTerbit(tahun);
        b.setKategori(kategori);
        b.setStok(stok);
        return b;
    }
}