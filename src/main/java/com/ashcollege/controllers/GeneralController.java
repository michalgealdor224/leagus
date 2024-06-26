package com.ashcollege.controllers;

import com.ashcollege.Persist;
import com.ashcollege.entities.Bet;
import com.ashcollege.entities.Game;
import com.ashcollege.entities.Team;
import com.ashcollege.entities.User;
import com.ashcollege.models.BetModel;
import com.ashcollege.models.GameModel;
import com.ashcollege.models.TeamModel;
import com.ashcollege.responses.BasicResponse;
import com.ashcollege.responses.LoginResponse;
import com.ashcollege.responses.UserDetailsResponse;
import com.ashcollege.utils.DbUtils;
import com.ashcollege.utils.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.ashcollege.utils.Errors.*;

@RestController
public class GeneralController {


    @Autowired
    private DbUtils dbUtils;

    @Autowired
    private Persist persist;


    @RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.POST})
    public Object test() {
        return "Hello From Server";
    }


    @RequestMapping(value = "get-data", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse getData(String secret) {
        Integer userId = dbUtils.getUserIdBySecret(secret);
        return userId != null ? new UserDetailsResponse(true, null, dbUtils.getUserById(userId)) : new BasicResponse(false, ERROR_SECRET_NOT_FOUND);
    }

    @RequestMapping(value = "get-table", method = {RequestMethod.GET, RequestMethod.POST})
    public List<TeamModel> getData() {
        var teams = dbUtils.getTeams();
        var games = dbUtils.getGames();
        return TeamModel.getTable(games, teams);
    }

    public void generateTeams() {
        Team[] teams = new Team[8];
        String[] spanishTeams = {"Barcelona", "Real Madrid", "Atletico Madrid", "Real Sociedad", "Villarreal", "Real Betis", "Athletic Bilbao", "Celta Vigo"};
        Random random = new Random();
        for (int i = 0; i < 8; i++) {
            String teamName = spanishTeams[i];
            int randomNumber = random.nextInt(20) + 1; //TODO
            teams[i] = new Team(teamName, randomNumber);
            System.out.println(teamName + " " + randomNumber);
            dbUtils.saveTeam(teams[i]);
        }
    }




/*    @RequestMapping(value = "set-data", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse setData(int id, String username, String password, String email) {
        if (Validator.validChanges("password", password) && (Validator.validChanges("email", email))){
            User last = dbUtils.getUserById(id);
            last.updateDetails(id, username, password, email);
            dbUtils.updateUserDetails(last);
            return new BasicResponse(true, null);
            //return dbUtils.updateUserData(id, username, password, email);
        }
        return new BasicResponse(false, ERROR_USER_UPDATE);
    }*/

    @RequestMapping(value = "set-data", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse setData(int id, String username, String password, String email) {
        User user = dbUtils.getUserById(id);
        if (user == null) {
            return new BasicResponse(false, ERROR_LOGIN_USER_NOT_FOUND);
        }
        user.updateDetails(username, password, email);
        if (Validator.validateUser(user)) {
            dbUtils.saveOrUpdateUserDetails(user);
            return new BasicResponse(true, null);
        }
        return new BasicResponse(false, ERROR_USER_UPDATE);
    }


    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse login(String username, String password) {
        BasicResponse basicResponse = null;
        boolean success = false;
        String errorCode = null;
        if (username != null && username.length() > 0) {
            if (password != null && password.length() > 0) {
                User user = dbUtils.login(username, password);
                if (user != null) {
                    basicResponse = new LoginResponse(true, errorCode, user.getId(), user.getSecret());
                } else {
                    errorCode = ERROR_LOGIN_USER_NOT_FOUND;
                }
            } else {
                errorCode = ERROR_SIGN_UP_NO_PASSWORD;
            }
        } else {
            errorCode = ERROR_SIGN_UP_NO_USERNAME;
        }
        if (errorCode != null) {
            basicResponse = new BasicResponse(success, errorCode);
        }
        System.out.println(basicResponse.isSuccess());
        return basicResponse;
    }

    @RequestMapping(value = "add-user", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse addUser(String username, String password, String confirmPassword, String email) {
        BasicResponse response;
        User userToAdd = new User(username, password, email);
        if (Validator.validateUser(userToAdd, confirmPassword)) {
            boolean success = dbUtils.addUser(userToAdd);
            response = new BasicResponse(success, success ? null : ERROR_SIGN_UP_USERNAME_TAKEN);
        } else {
            response = new BasicResponse(false, Validator.getValidateError(userToAdd, confirmPassword));
        }
        return response;
    }


    @RequestMapping(value = "get-users")
    public List<User> getUsers() {
        return dbUtils.getAllUsers();
    }

    @RequestMapping(value = "get-my-bets")
    public List<BetModel> getMyBets(String secret) {
        List<BetModel> betModelList = new ArrayList<>();
        User user = new User();
        user = dbUtils.getUserBySecret(secret);
        if (user != null) {
            for (Bet bet:dbUtils.getBetsByUserId(user))
            {
                betModelList.add(new BetModel(bet));
            }

        }
        return betModelList;
    }

    @RequestMapping(value = "get-live-games", method = {RequestMethod.GET, RequestMethod.POST})
    public List<GameModel> getGamesIsLive() {
        List<GameModel> gameModelList = new ArrayList<>();
        for (Game game : dbUtils.getGamesIsLive()) {
            gameModelList.add(new GameModel(game));
        }

        return gameModelList;
    }

    @RequestMapping(value = "get-games-played", method = {RequestMethod.GET, RequestMethod.POST})
    public List<GameModel> getGamesPlayed() {

        List<GameModel> gameModelList = new ArrayList<>();
        for (Game game : dbUtils.getGamesPlayed()) {
            gameModelList.add(new GameModel(game));
        }
        return gameModelList;
    }

    @RequestMapping(value = "get-future-games", method = {RequestMethod.GET, RequestMethod.POST})
    public List<GameModel> getFutureGames() {
        List<GameModel> gameModelList = new ArrayList<>();
        for (Game game : dbUtils.getFutureGames()) {
            gameModelList.add(new GameModel(game));
        }
        return gameModelList;
    }

    @RequestMapping(value = "get-game-by-id", method = {RequestMethod.GET, RequestMethod.POST})
    public GameModel getGameById(int gameId) {
        GameModel gameModel = new GameModel(dbUtils.getGameById(gameId));
        return gameModel;
    }

    @RequestMapping(value = "send-bet", method = {RequestMethod.GET, RequestMethod.POST})
    public BasicResponse sendBet(String secret, int gameId,boolean teamAWin ,boolean teamBWin,boolean isDouble, int teamAScore, int teamBScore, int sum, double ratio)
    {
        BasicResponse basicResponse =new BasicResponse(false,null);
        User user = dbUtils.getUserBySecret(secret);
        if (user != null) {
            Game game = dbUtils.getGameById(gameId);
            if(game!=null){
                Bet bet =new Bet(user, game,isDouble, sum, ratio);
                bet.calculateBet( teamAWin , teamBWin, teamAScore,  teamBScore);
                dbUtils.saveBet(bet);
                user.setBalance(user.getBalance()-sum);
                dbUtils.saveOrUpdateUserDetails(user);
                basicResponse.setSuccess(true);
            }
            else{
                basicResponse.setErrorCode(ERROR_GAME_NOT_FOUND);
            }
        }
        else{
            basicResponse.setErrorCode(ERROR_SECRET_NOT_FOUND);
        }

        return basicResponse;
    }




}
