package com.ashcollege.models;

import com.ashcollege.entities.Team;
import com.ashcollege.entities.Game;
import lombok.Data;

@Data
public class GameModel {
    private int id;
    private Team teamA;
    private Team teamB;
    private int ScoreTeamA;
    private int ScoreTeamB;
    private int round;

    public GameModel(Game game) {
        this.id=game.getId();
        this.teamA = game.getTeamA();
        this.teamB = game.getTeamB();
        this.ScoreTeamA = game.getScoreA();
        this.ScoreTeamB = game.getScoreB();
        this.round = game.getRound();
    }


}
