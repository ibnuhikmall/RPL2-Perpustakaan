package dao;

import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class DashboardDaoImpl implements DashboardDao {

    private final SessionFactory sessionFactory;

    public DashboardDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public long jumlahBuku() {
        return hitung("select count(b) from Buku b");
    }

    @Override
    public long jumlahAnggota() {
        return hitung("select count(a) from Anggota a");
    }

    @Override
    public long jumlahDipinjam() {
        return hitung("select count(p) from Peminjaman p where p.status = 'Dipinjam'");
    }

    @Override
    public long jumlahTerlambat() {
        return hitung("select count(p) from Peminjaman p "
                + "where p.status = 'Dipinjam' and p.tglJatuhTempo < current_date");
    }

    private long hitung(String hql) {
        return sessionFactory.getCurrentSession().createQuery(hql, Long.class).getSingleResult();
    }
}