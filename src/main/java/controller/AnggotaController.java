package controller;

import dao.AnggotaDao;
import java.util.ArrayList;
import java.util.List;
import model.Anggota;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AnggotaController {

    private final AnggotaDao dao;

    public AnggotaController(AnggotaDao dao) {
        this.dao = dao;
    }

    // Hasil berupa baris tabel: No Anggota, Nama, Alamat, No HP, Status
    @Transactional(readOnly = true)
    public List<Object[]> cariAnggota(String keyword) {
        List<Object[]> hasil = new ArrayList<>();
        for (Anggota a : dao.cari(keyword)) {
            hasil.add(new Object[]{a.getNoAnggota(), a.getNama(),
                a.getAlamat() == null ? "" : a.getAlamat(),
                a.getNoHp() == null ? "" : a.getNoHp(),
                a.getStatus()});
        }
        return hasil;
    }

    @Transactional(readOnly = true)
    public boolean noAda(String noAnggota) {
        return dao.cariByNo(noAnggota) != null;
    }

    @Transactional(readOnly = true)
    public boolean punyaRiwayat(String noAnggota) {
        return dao.punyaRiwayat(noAnggota);
    }

    @Transactional
    public void tambah(String no, String nama, String alamat, String noHp, String status) {
        dao.tambah(buatAnggota(no, nama, alamat, noHp, status));
    }

    @Transactional
    public void ubah(String no, String nama, String alamat, String noHp, String status) {
        dao.ubah(buatAnggota(no, nama, alamat, noHp, status));
    }

    @Transactional
    public void hapus(String noAnggota) {
        dao.hapus(noAnggota);
    }

    private Anggota buatAnggota(String no, String nama, String alamat, String noHp, String status) {
        Anggota a = new Anggota();
        a.setNoAnggota(no);
        a.setNama(nama);
        a.setAlamat(alamat);
        a.setNoHp(noHp);
        a.setStatus(status);
        return a;
    }
}