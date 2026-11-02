package controller;

import java.util.List;
import model.User;
import org.hibernate.SessionFactory;
import util.HibernateUtil;

public class UserController {

    private final SessionFactory sf = HibernateUtil.getFactory();

    // Mengembalikan true jika username dan password benar
    public boolean login(String username, String password) {
        // HQL tidak mengenal fungsi SHA2(), jadi login memakai native query (SQL biasa)
        List<User> hasil = sf.fromSession(s ->
                s.createNativeQuery(
                        "SELECT * FROM users WHERE username = :u AND password = SHA2(:p, 256)",
                        User.class)
                 .setParameter("u", username)
                 .setParameter("p", password)
                 .getResultList());

        if (hasil.isEmpty()) {
            return false;
        }
        User u = hasil.get(0);
        Sesi.idUser = u.getIdUser();
        Sesi.nama = u.getNamaLengkap();
        Sesi.role = u.getRole();
        return true;
    }
}
