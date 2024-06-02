package com.ashcollege.models;

import com.ashcollege.entities.Game;
import com.ashcollege.entities.Team;
import com.ashcollege.utils.DbUtils;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
public class TeamModel implements Comparable<TeamModel> {
    private String name;
    private int draws;
    private int wins;
    private int loses;
    private int goalsScored;
    private int goalsConceded;

    public TeamModel(String name) {
        this.name = name;
        this.draws = 0;
        this.wins = 0;
        this.loses = 0;
        this.goalsScored = 0;
        this.goalsConceded = 0;
    }

    public static List<TeamModel> getTable(List<Game> games, List<Team> allTeams){
        List<TeamModel> teams = new ArrayList<>();
        for (Team team : allTeams) {
            TeamModel teamForTable = new TeamModel(team.getName());
            teams.add(teamForTable);
        }
        for (TeamModel team : teams) {
            team.updateRank(games);
        }
        Collections.sort(teams);
        return teams;
    }

    private void updateRank(List<Game> teamGames) {
        for (Game game: teamGames) {
            if (game.getLive() != null) {
                if (game.getTeamA().getName().equals(this.name)) {
                    this.updateByGoals(game.getScoreA(), game.getScoreB());
                } else if (game.getTeamB().getName().equals(this.name)) {
                    this.updateByGoals(game.getScoreB(), game.getScoreA());
                }
            }
        }
    }


    private void updateByGoals(int goalsScored, int goalsConceded){
        if (goalsScored > goalsConceded){
            this.wins += 1;
        } else if (goalsScored < goalsConceded){
            this.loses += 1;
        } else {
            this.draws += 1;
        }
        this.goalsConceded += goalsConceded;
        this.goalsScored += goalsScored;
    }

    @Override
    public int compareTo(TeamModel other) {
        int pointsCompare = Integer.compare((other.wins * 3 + other.draws), (this.wins * 3 + this.draws));
        if (pointsCompare != 0) {
            return pointsCompare;
        }

        int goalDifferenceCompare = Integer.compare((other.goalsScored - other.goalsConceded),
                (this.goalsScored - this.goalsConceded));
        if (goalDifferenceCompare != 0) {
            return goalDifferenceCompare;
        }

        return this.name.compareTo(other.name);
    }
}
