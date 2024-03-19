package com.ashcollege.utils;

import com.ashcollege.entities.User;
import com.ashcollege.models.UserModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class DbUtils {

    private Connection connection;
    private final SessionFactory sessionFactory;

    @Autowired
    public DbUtils(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    @PostConstruct
    public void init() {
        createDbConnection(Constants.DB_USERNAME, Constants.DB_PASSWORD);

    }

    public void saveOrUpdateUserDetails(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(user);
        transaction.commit();
        session.close();
    }

    public User getUserBySecret(String secret) {
        User found;
        Session session = sessionFactory.openSession();
        found = (User) session.createQuery("FROM User WHERE secret = :secret")
                .setParameter("secret", secret)
                .uniqueResult();
        session.close();
        return found;
    }

    public User getUserById(int userId) {
        User user = null;
        String query = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user = new User();
                user.setId(resultSet.getInt("id"));
                user.setUsername(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setEmail(resultSet.getString("email"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return user;
    }


    private void createDbConnection(String username, String password) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/ash2024", username, password);
            System.out.println("Connection successful!");
            System.out.println();
        } catch (Exception e) {
            System.out.println("Cannot create DB connection!");
        }
    }

    public boolean checkIfUsernameAvailable(String username) {
        boolean available = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM users WHERE username = ?");
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (!resultSet.next()) {
                available = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return available;
    }


    public boolean isUsernameExistsForOtherUser(int userId, String username) {
        String query = "SELECT COUNT(*) FROM users WHERE username = ? AND id <> ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, username);
            statement.setInt(2, userId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean addUser(User user) {
        boolean success = false;
        if (checkIfUsernameAvailable(user.getUsername())) {
            Session session = sessionFactory.openSession();
            session.save(user);
            session.close();
            success = true;
        }
        return success;
    }
    public List<User> getAllUsers() {
        Session session = sessionFactory.openSession();
        List<User> allUsers = session.createQuery("FROM User").list();
        session.close();
        return allUsers;
    }

    /*public boolean checkCredentials (String username, String password) {
        boolean ok = false;
        if (checkIfUsernameAvailable(username)) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE password = ? and username = ?");
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,password);
        }
    }*/

    public User login(String username, String password) {
        User user = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, secret FROM users WHERE username = ? AND password = ?");
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                String secret = resultSet.getString("secret");
                user = new User();
                user.setId(id);
                user.setSecret(secret);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

//    public List<Product> getProductsByUserSecret (String secret) {
//        List<Product> products = new ArrayList<>();
//        try {
//            PreparedStatement preparedStatement = connection.prepareStatement(
//                    "SELECT p.description, p.price " +
//                            "FROM users u INNER JOIN users_products_map upm ON u.id = upm.user_id " +
//                            "INNER JOIN products p ON upm.product_id = p.id " +
//                            "WHERE u.secret = ?"
//            );
//            preparedStatement.setString(1, secret);
//            ResultSet resultSet = preparedStatement.executeQuery();
//            while (resultSet.next()) {
//                String description = resultSet.getString(1);
//                float price = resultSet.getFloat(2);
//                Product product = new Product(description, price, 0);
//                products.add(product);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return products;
//    }

    public Integer getUserIdBySecret(String secret) {
        Integer userId = null;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM users WHERE secret = ?");
            preparedStatement.setString(1, secret);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                userId = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userId;
    }


}
