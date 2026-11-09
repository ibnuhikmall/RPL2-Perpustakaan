package dao;

import java.util.List;
import model.User;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository
public class UserDaoImpl implements UserDao {

    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public User cariByLogin(String username, String password) {
        // HQL tidak mengenal SHA2(), jadi memakai native query (SQL biasa)
        List<User> hasil = sessionFactory.getCurrentSession().createNativeQuery(
                "SELECT * FROM users WHERE username = :u AND password = SHA2(:p, 256)", User.class)
                .setParameter("u", username)
                .setParameter("p", password)
                .getResultList();
        return hasil.isEmpty() ? null : hasil.get(0);
    }
}