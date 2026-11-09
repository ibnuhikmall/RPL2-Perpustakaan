package dao;

import java.util.List;
import model.Anggota;

public interface AnggotaDao {
    List<Anggota> cari(String keyword);
    Anggota cariByNo(String noAnggota);
    boolean punyaRiwayat(String noAnggota);
    void tambah(Anggota anggota);
    void ubah(Anggota anggota);
    void hapus(String noAnggota);
}