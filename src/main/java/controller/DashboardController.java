package controller;

import org.hibernate.SessionFactory;
import util.HibernateUtil;

public class DashboardController {

    private final SessionFactory sf = HibernateUtil.getFactory();

    public int jumlahBuku() {
        return hitung("select count(b) from Buku b");
    }

    public int jumlahAnggota() {
        return hitung("select count(a) from Anggota a");
    }

    public int jumlahDipinjam() {
        return hitung("select count(p) from Peminjaman p where p.status = 'Dipinjam'");
    }

    public int jumlahTerlambat() {
        return hitung("select count(p) from Peminjaman p "
                    + "where p.status = 'Dipinjam' and p.tglJatuhTempo < current_date");
    }

    // Menjalankan query count dan mengembalikan angkanya
    private int hitung(String hql) {
        return sf.fromSession(s -> s.createQuery(hql, Long.class).getSingleResult().intValue());
    }
}
