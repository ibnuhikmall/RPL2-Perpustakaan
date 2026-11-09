package dao;

import java.util.List;
import model.Buku;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class BukuDaoImpl implements BukuDao {

    private final SessionFactory sessionFactory;

    public BukuDaoImpl(SessionFactory sessionFactory) {   // diisi otomatis oleh Spring
        this.sessionFactory = sessionFactory;
    }

    private Session session() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public List<Buku> cari(String keyword) {
        return session().createQuery("from Buku b where b.kodeBuku like :k or b.judul like :k "
                + "or b.pengarang like :k order by b.kodeBuku", Buku.class)
                .setParameter("k", "%" + keyword + "%")
                .getResultList();
    }

    @Override
    public Buku cariByKode(String kode) {
        return session().find(Buku.class, kode);
    }

    @Override
    public boolean punyaRiwayat(String kode) {
        return session().createQuery("select count(p) from Peminjaman p where p.buku.kodeBuku = :k",
                Long.class).setParameter("k", kode).getSingleResult() > 0;
    }

    @Override
    public void tambah(Buku buku) {
        session().persist(buku);   // INSERT
    }

    @Override
    public void ubah(Buku buku) {
        session().merge(buku);     // UPDATE
    }

    @Override
    public void hapus(String kode) {
        Buku b = session().find(Buku.class, kode);
        if (b != null) {
            session().remove(b);   // DELETE
        }
    }
}