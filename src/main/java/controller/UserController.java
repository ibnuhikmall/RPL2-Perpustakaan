package controller;

import dao.UserDao;
import model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserController {

    private final UserDao dao;

    public UserController(UserDao dao) {
        this.dao = dao;
    }

    // Mengembalikan true jika username dan password benar
    @Transactional(readOnly = true)
    public boolean login(String username, String password) {
        User u = dao.cariByLogin(username, password);
        if (u == null) {
            return false;
        }
        Sesi.idUser = u.getIdUser();
        Sesi.nama = u.getNamaLengkap();
        Sesi.role = u.getRole();
        return true;
    }
}