package dao;

import java.util.List;
import model.Buku;

public interface BukuDao {
    List<Buku> cari(String keyword);
    Buku cariByKode(String kode);
    boolean punyaRiwayat(String kode);
    void tambah(Buku buku);
    void ubah(Buku buku);
    void hapus(String kode);
}