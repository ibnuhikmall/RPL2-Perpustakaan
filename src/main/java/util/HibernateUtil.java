package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public class HibernateUtil {

    // Dibuat satu kali saat aplikasi pertama kali memakai database.
    // configure() otomatis membaca file hibernate.cfg.xml.
    private static final SessionFactory factory =
            new Configuration().configure().buildSessionFactory();

    public static SessionFactory getFactory() {
        return factory;
    }
}
