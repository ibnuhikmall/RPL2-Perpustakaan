package controller;

import java.util.ArrayList;
import java.util.List;
import model.Buku;
import org.hibernate.SessionFactory;
import util.HibernateUtil;

public class BukuController {

    private final SessionFactory sf = HibernateUtil.getFactory();

    // Mengambil daftar buku. Jika keyword kosong, semua buku ditampilkan.
    // Hasilnya tetap List<Object[]> agar PanelBuku tidak perlu diubah.
    public List<Object[]> cariBuku(String keyword) {
        List<Buku> daftar = sf.fromSession(s ->
                s.createQuery("from Buku b where b.kodeBuku like :k or b.judul like :k "
                            + "or b.pengarang like :k order by b.kodeBuku", Buku.class)
                 .setParameter("k", "%" + keyword + "%")
                 .getResultList());

        List<Object[]> hasil = new ArrayList<>();
        for (Buku b : daftar) {
            hasil.add(new Object[]{b.getKodeBuku(), b.getJudul(), b.getPengarang(),
                b.getPenerbit(), b.getTahunTerbit(), b.getKategori(), b.getStok()});
        }
        return hasil;
    }

    // Cek apakah kode buku sudah dipakai
    public boolean kodeAda(String kode) {
        return sf.fromSession(s -> s.find(Buku.class, kode) != null);
    }

    // Cek apakah buku pernah dipinjam (punya riwayat transaksi)
    public boolean punyaRiwayat(String kode) {
        return sf.fromSession(s ->
                s.createQuery("select count(p) from Peminjaman p where p.buku.kodeBuku = :k",
                              Long.class)
                 .setParameter("k", kode)
                 .getSingleResult() > 0);
    }

    public void tambah(String kode, String judul, String pengarang, String penerbit,
                       int tahun, String kategori, int stok) {
        Buku b = new Buku();
        b.setKodeBuku(kode);
        b.setJudul(judul);
        b.setPengarang(pengarang);
        b.setPenerbit(penerbit);
        b.setTahunTerbit(tahun);
        b.setKategori(kategori);
        b.setStok(stok);
        sf.inTransaction(s -> s.persist(b)); // INSERT
    }

    public void ubah(String kode, String judul, String pengarang, String penerbit,
                     int tahun, String kategori, int stok) {
        sf.inTransaction(s -> {
            Buku b = s.find(Buku.class, kode); // ambil dari database
            b.setJudul(judul);                 // cukup ubah objeknya,
            b.setPengarang(pengarang);         // UPDATE dijalankan otomatis
            b.setPenerbit(penerbit);           // saat transaksi selesai
            b.setTahunTerbit(tahun);
            b.setKategori(kategori);
            b.setStok(stok);
        });
    }

    public void hapus(String kode) {
        sf.inTransaction(s -> s.remove(s.find(Buku.class, kode))); // DELETE
    }
}
