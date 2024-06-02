package com.ashcollege.entities;

import com.ashcollege.utils.Weather;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue
    @Column
    private int id;
    @ManyToOne
    @JoinColumn
    private Team teamA;
    @ManyToOne
    @JoinColumn
    private Team teamB;
    @Column
    private int scoreA;
    @Column
    private int scoreB;
    @Column
    private Weather weather;
    @Column
    private Boolean isLive;
    @Column
    private int round;
    @Column
    private LocalDate date;

    public Game(Team teamA, Team teamB, int scoreA, int scoreB, Weather weather, Boolean isLive, int round, LocalDate date) {
        this.teamA = teamA;
        this.teamB = teamB;
        this.scoreA = scoreA;
        this.scoreB = scoreB;
        this.weather = weather;
        this.isLive = isLive;
        this.round = round;
        this.date = date;
    }

    public Game() {

    }

    public static List<Game> scheduleGames(List<Team> teams) {
        LocalDate startDate = LocalDate.now();
        List<List<Game>> schedule = new ArrayList<>();
        for (int round = 0; round < 7; round++) {
            LocalDate roundDate = startDate.plusDays(round);
            List<Game> roundSchedule = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                var teamA = teams.get(i);
                var teamB = teams.get(teams.size() - 1 - i);
                Game current = new Game(teamA, teamB, 0,0, Weather.getRandomWeather(), null, round, roundDate);
                roundSchedule.add(current);
            }
            Collections.rotate(teams.subList(1, teams.size()), 1); // Rotate teams for next round
            schedule.add(roundSchedule);
        }

        List<Game> games = new ArrayList<>();
        // Print schedule
        for (List<Game> round : schedule) {
            for (Game game : round) {
                games.add(game);
                System.out.println(game.toString());
            }
            System.out.println("-------------------");
            System.out.println("-------------------");
        }
        return games;

    }

    @Override
    public String toString() {
        return "Game{" +
                teamA.getName() +
                " <> " + teamB.getName() +
                ", " + weather +
                ", " + date +
                '}';
    }

    public Team whoWin() {
        if(this.getScoreA()>this.getScoreB()){
            return this.getTeamA();
        }
        else if(this.getScoreA()<this.getScoreB()){
            return this.getTeamB();
        }
        else{
            return null;
        }
    }
    public Team whoLose() {
        if(this.getScoreA()<this.getScoreB()){
            return this.getTeamA();
        }
        else if(this.getScoreA()>this.getScoreB()){
            return this.getTeamB();
        }
        else{
            return null;
        }
    }

    public Team getTeamA() {
        return teamA;
    }

    public void setTeamA(Team teamA) {
        this.teamA = teamA;
    }

    public Team getTeamB() {
        return teamB;
    }

    public void setTeamB(Team teamB) {
        this.teamB = teamB;
    }

    public int getScoreA() {
        return scoreA;
    }

    public void setScoreA(int scoreA) {
        this.scoreA = scoreA;
    }

    public int getScoreB() {
        return scoreB;
    }

    public void setScoreB(int scoreB) {
        this.scoreB = scoreB;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public Boolean getLive() {
        return isLive;
    }

    public void setLive(Boolean live) {
        isLive = live;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
