package com.ashcollege.utils;

import com.ashcollege.controllers.LiveUpdateController;
import com.ashcollege.entities.Bet;
import com.ashcollege.entities.Game;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.models.GameModel;
import com.ashcollege.models.TeamModel;
import com.ashcollege.models.UserModel;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

@Component
public class DbUtils {

    private Connection connection;
    private final SessionFactory sessionFactory;
    @Autowired
    private LiveUpdateController liveUpdateController;
    @Autowired
    private ScoreGenerator scoreGenerator;
    @Autowired
    public DbUtils(SessionFactory sf) {
        this.sessionFactory = sf;
    }

    @PostConstruct
    public void init() {
        createDbConnection(Constants.DB_USERNAME, Constants.DB_PASSWORD);
        generateTeams();
        initGames();
        startLeague();
    }

    private void startLeague(){
        List<Game> gamesFromDB = getGames();
        new Thread(()->{
            while(true){
                for (int i =0; i<7;i++) {
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    int roundNumber=i;
                    CountDownLatch round = new CountDownLatch(4);
                    for (Game game : gamesFromDB.stream().filter(game -> game.getRound() == roundNumber&& game.getLive()==null).toList()) {
                        new Thread(new RunGame(round,game,this,scoreGenerator)).start();
                    }
                    try {
                        round.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    Thread.sleep(120000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                initGames();
            }

        }).start();
    }
    private void initGames() {
        List<Team> teams = getTeams();
        var games = Game.scheduleGames(teams);
        for (Game game: games){
            saveGame(game);
        }
        System.out.println("Reached end of init games() function.");

    }

    public void generateTeams() {
        List<Team> teamsFromDB = getAllTeams();
        if (!teamsFromDB.isEmpty()){
            return;
        }
        Team[] teams = new Team[8];
        String[] spanishTeams = {"Barcelona", "Real Madrid", "Atletico Madrid", "Real Sociedad", "Villarreal", "Real Betis", "Athletic Bilbao", "Celta Vigo"};
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            String teamName = spanishTeams[i];
            int randomNumber = random.nextInt(10);
            teams[i] = new Team(teamName, randomNumber);
            System.out.println(teamName + " " + randomNumber);
            this.saveTeam(teams[i]);
        }
        System.out.println("Reached end of generateTeams() function.");
    }

    private List<Team> getAllTeams() {
        Session session = sessionFactory.openSession();
        List<Team> allTeams = session.createQuery("FROM Team").list();
        session.close();
        return allTeams;
    }

    public void saveOrUpdateUserDetails(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(user);
        transaction.commit();
        session.close();
    }

    public void saveTeam(Team team) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(team);
        transaction.commit();
        session.close();
    }

    private void saveGame(Game game) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(game);
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
                user.setBalance(resultSet.getInt("balance"));
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
            Transaction transaction = session.beginTransaction();
            session.saveOrUpdate(user);
            transaction.commit();
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

    public List<Team> getTeams() {
        Session session = sessionFactory.openSession();
        List<Team> allTeams = session.createQuery("FROM Team").list();
        session.close();
        return allTeams;
    }

    public List<Game> getGames() {
        Session session = sessionFactory.openSession();
        List<Game> allGames = session.createQuery("FROM Game").list();
        session.close();
        return allGames;
    }

    public List<Game> getGamesIsLive() {
        Session session = sessionFactory.openSession();
        List<Game> gamesIsLive = session.createQuery("FROM Game where isLive =true ").list();
        session.close();
        return gamesIsLive;
    }
    public List<Game> getGamesPlayed() {
        Session session = sessionFactory.openSession();
        List<Game> gamesPlayed = session.createQuery("FROM Game where isLive =false").list();
        session.close();
        return gamesPlayed;
    }
    public List<Game> getFutureGames() {
        Session session = sessionFactory.openSession();
        List<Game> futureGames = session.createQuery("FROM Game where isLive =null").list();
        session.close();
        return futureGames;
    }

    public void updateGame(Game game) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(game);
        updateBetByGame(game);
        transaction.commit();
        session.close();
        Thread thread =new Thread();
        thread.start();
        liveUpdateController.sendGamesIsUpdate();
        try {
            thread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }


    }
    public synchronized void updateBetByGame(Game game){
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        List<Bet> bets = session.createQuery("FROM Bet where game =:game ")
                .setParameter("game",game).list();
        for (Bet bet:bets)
        {
            bet.calculateSuccess();
            session.saveOrUpdate(bet);
            session.saveOrUpdate(bet.getUser());
        }
        transaction.commit();
        session.close();
    }
    public void updateTeam(Team team) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(team);
        transaction.commit();
        session.close();
    }
    public Game getGameById(int gameId){
        Session session = sessionFactory.openSession();
        Game game =(Game) session.createQuery("FROM Game where id=:gameId")
                .setParameter("gameId",gameId).uniqueResult();
        session.close();
        return game;
    }
    public Team getTeamById(int teamId){
        Session session = sessionFactory.openSession();
        Team team =(Team) session.createQuery("FROM Game where id=:teamId")
                .setParameter("teamId",teamId).uniqueResult();
        session.close();
        return team;
    }
    public void saveBet(Bet bet){
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        session.saveOrUpdate(bet);
        transaction.commit();
        session.close();
    }

    public List<Bet> getBetsByUserId(User user){
        Session session = sessionFactory.openSession();
        List<Bet> bets = session.createQuery("FROM Bet where user=:user")
                .setParameter("user",user).list();
        session.close();
        return bets;
    }
}
