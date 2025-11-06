package com.pharmacy.service;

import com.pharmacy.dao.UserDao;
import com.pharmacy.model.User;
import org.mindrot.jbcrypt.BCrypt;

public class AuthService {
    private final UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public void signup(String username, String email, String password) {
        if (username == null || username.length() < 3) {
            throw new IllegalArgumentException("Username must be at least 3 characters");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Invalid email");
        }
        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException("Password must be at least 6 characters");
        }
        if (userDao.usernameOrEmailExists(username, email)) {
            throw new IllegalArgumentException("Username or email already exists");
        }
        String hash = BCrypt.hashpw(password, BCrypt.gensalt(12));
        userDao.createUser(username, email, hash);
    }

    public User login(String login, String password) {
        if (login == null || password == null) {
            return null;
        }
        User u = userDao.findByUsernameOrEmail(login);
        if (u == null) return null;
        boolean ok = BCrypt.checkpw(password, u.getPasswordHash());
        return ok ? u : null;
    }
}
