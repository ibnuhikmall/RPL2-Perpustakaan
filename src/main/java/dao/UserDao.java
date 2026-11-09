package dao;

import model.User;

public interface UserDao {
    // Mengembalikan user jika username dan password benar, selain itu null
    User cariByLogin(String username, String password);
}