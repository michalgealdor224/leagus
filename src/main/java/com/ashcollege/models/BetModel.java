package com.ashcollege.models;

import com.ashcollege.entities.Bet;
import com.ashcollege.entities.Game;
import com.ashcollege.entities.Team;
import lombok.Data;

import javax.persistence.Column;
@Data
public class BetModel {
    private Game game;
    private int sum;
    private Team teamIsWin;
    private int teamAScore;
    private int teamBScore;
    private boolean success;

    public BetModel(Bet bet) {
        this.game = bet.getGame();
        this.sum = bet.getSum();
        this.teamIsWin = bet.getTeamIsWin();
        this.teamAScore = bet.getTeamAScore();
        this.teamBScore = bet.getTeamBScore();
        this.success=bet.isSuccess();
    }


}
