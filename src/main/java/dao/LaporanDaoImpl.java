package dao;

import java.time.LocalDate;
import java.util.List;
import model.Peminjaman;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class LaporanDaoImpl implements LaporanDao {

    private final SessionFactory sessionFactory;

    public LaporanDaoImpl(SessionFactory sessionFactory) {   // diisi otomatis oleh Spring
        this.sessionFactory = sessionFactory;
    }

    @Override
    public List<Peminjaman> cariPeminjaman(LocalDate awal, LocalDate akhir, String status) {
        String hql = "from Peminjaman p where p.tglPinjam between :awal and :akhir"
                + (status == null ? "" : " and p.status = :st")
                + " order by p.tglPinjam, p.idPinjam";

        var query = sessionFactory.getCurrentSession().createQuery(hql, Peminjaman.class)
                .setParameter("awal", awal)
                .setParameter("akhir", akhir);
        if (status != null) {
            query.setParameter("st", status);
        }
        return query.getResultList();
    }
}