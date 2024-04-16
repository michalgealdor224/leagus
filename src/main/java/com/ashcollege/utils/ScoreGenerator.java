package com.ashcollege.utils;

import com.ashcollege.entities.Game;
import com.ashcollege.entities.Team;

import static com.ashcollege.utils.Constants.SKILL_POWER_HOME;

public class ScoreGenerator {


    public Game calculate(Game game){
        this.calculateResults(game);
        this.randomResults(game);
        this.dynamicSkills(game);
        return game;
    }

    private Game calculateResults(Game game) {
        double skillA = game.getTeamA().getSkillLevel() * SKILL_POWER_HOME * Weather.powerFrom(game.getWeather());
        double skillB = game.getTeamB().getSkillLevel(); // 10 6
        game.setScoreA((int)skillA);
        game.setScoreB((int)skillB);
        return game;
    }

    private void randomResults(Game game) {
        // rand int 0-9 TODO
        // if 1-2 return 0-0 1-1 2-2
        // if 3 replace results
        // else return
    }

    private void dynamicSkills(Game game) {
        Team winner = game.whoWin();
        // increment skill with x for the winner
    }
}
