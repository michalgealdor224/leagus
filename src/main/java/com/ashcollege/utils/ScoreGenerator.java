package com.ashcollege.utils;

import com.ashcollege.entities.Game;
import com.ashcollege.entities.Team;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static com.ashcollege.utils.Constants.SKILL_POWER_HOME;
@Component
public class ScoreGenerator {

    @Autowired
    private DbUtils dbUtils;
    public Game calculate(Game game){
        this.calculateResults(game);
        //this.randomResults(game);
        this.dynamicSkills(game);
        return game;
    }

    private Game calculateResults(Game game) {
        int scoreA = (int) Math.sqrt(game.getTeamA().getSkillLevel() *
                SKILL_POWER_HOME * Weather.powerFrom(game.getWeather()));
        int scoreB = (int) Math.sqrt(game.getTeamB().getSkillLevel()); // 10 6
        System.out.println("scoreA:"+scoreA + ", scoreB:"+ scoreB);
        int secondBetweenGoals= 30000 /(scoreA+scoreB+1);
        boolean aTorn=true;
        int scoreALive=0;
        int scoreBLive=0;
        if(scoreA+scoreB==0){
            try {
                Thread.sleep(secondBetweenGoals);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        for(int i=0;i< scoreA+scoreB;i++){
            if(aTorn ){
                if(scoreA>scoreALive){
                    game.setScoreA(scoreALive++);
                    dbUtils.updateGame(game);
                    System.out.println("scoreA:"+scoreALive + ", scoreB:"+ scoreBLive);
                    try {
                        Thread.sleep(secondBetweenGoals);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    aTorn =false;
                }
                else{
                    aTorn =false;
                }
            }
            else if(!aTorn ) {
                if (scoreB > scoreBLive) {
                    game.setScoreB(scoreBLive++);
                    dbUtils.updateGame(game);
                    System.out.println("scorea:"+scoreALive + ", scoreB:"+ scoreBLive);
                    try {
                        Thread.sleep(secondBetweenGoals);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    aTorn = true;
                } else {
                    aTorn = true;
                }
            }
        }
        return game;
    }

    private void dynamicSkills(Game game) {
        Team winner = game.whoWin();
        if(winner !=null){
            if(winner.getSkillLevel()<10) {
                winner.setSkillLevel(winner.getSkillLevel() + 1);
                dbUtils.updateTeam(winner);
            }
        }
        Team loser =game.whoLose();
        if(loser !=null){
            if(loser.getSkillLevel()>0){
                loser.setSkillLevel(loser.getSkillLevel()-1);
                dbUtils.updateTeam(loser);
            }

        }
    }
}
