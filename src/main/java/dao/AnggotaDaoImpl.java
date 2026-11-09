package dao;

import java.util.List;
import model.Anggota;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class AnggotaDaoImpl implements AnggotaDao {

    private final SessionFactory sessionFactory;

    public AnggotaDaoImpl(SessionFactory sessionFactory) {   // diisi otomatis oleh Spring
        this.sessionFactory = sessionFactory;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Anggota> cari(String keyword) {
        return session().createQuery("from Anggota a where a.noAnggota like :k or a.nama like :k "
                + "order by a.noAnggota", Anggota.class)
                .setParameter("k", "%" + keyword + "%")
                .getResultList();
    }

    @Override
    public Anggota cariByNo(String noAnggota) {
        return session().find(Anggota.class, noAnggota);
    }

    @Override
    public boolean punyaRiwayat(String noAnggota) {
        return session().createQuery("select count(p) from Peminjaman p where p.anggota.noAnggota = :k",
                Long.class).setParameter("k", noAnggota).getSingleResult() > 0;
    }

    @Override
    public void tambah(Anggota anggota) {
        session().persist(anggota);   // INSERT
    }

    @Override
    public void ubah(Anggota anggota) {
        session().merge(anggota);     // UPDATE
    }

    @Override
    public void hapus(String noAnggota) {
        Anggota a = session().find(Anggota.class, noAnggota);
        if (a != null) {
            session().remove(a);      // DELETE
        }
    }
}