package com.ashcollege.entities;


import javax.persistence.*;


@Entity
@Table(name = "bets")
public class Bet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private int id;

    @ManyToOne
    @JoinColumn
    private User user;

    @ManyToOne
    @JoinColumn
    private Game game;
    @ManyToOne
    @JoinColumn
    private Team teamIsWin;

    @Column
    private int teamAScore;
    @Column
    private int teamBScore;
    @Column
    private int sum;
    @Column
    private double ratio;
    @Column
    private boolean isDouble;
    @Column
    private boolean success;

    public Bet( User user, Game game, Team teamIsWin, int teamAScore, int teamBScore,int sum ,double ratio) {
        this.user = user;
        this.game = game;
        this.teamIsWin = teamIsWin;
        this.teamAScore = teamAScore;
        this.teamBScore = teamBScore;
        this.sum=sum;
        this.ratio=ratio;
        this.success=false;
    }
    public Bet( User user, Game game,boolean isDouble ,int sum ,double ratio) {
        this.user = user;
        this.game = game;
        this.isDouble=isDouble;
        this.sum=sum;
        this.ratio=ratio;
        this.success=false;
    }

    public Bet() {

    }
    public void calculateBet(boolean teamAWin ,boolean teamBWin, int teamAScore, int teamBScore)
    {
        if (teamAWin) {
            Team teamIsWin = this.game.getTeamA();
            this.setTeamIsWin(teamIsWin);
        } else if (teamBWin) {
            Team teamIsWin = this.game.getTeamB();
            this.setTeamIsWin(teamIsWin);
        } else {
            Team teamIsWin = null;
        }

        if(this.isDouble){
            this.setTeamAScore(teamAScore);
            this.setTeamBScore(teamBScore);
        }
    }
    public  void calculateSuccess(){
        boolean success =false;
        if(this.teamIsWin!=null && this.game.whoWin()!=null){
            if(this.teamIsWin.getId()==this.game.whoWin().getId()){
                success=true;
            }
        }
        else if(this.teamIsWin==null && this.game.whoWin()==null){
            success=true;
        }

        if(this.isDouble()){
            if(this.teamAScore==this.game.getScoreA() && this.teamBScore==this.game.getScoreB()){
                success=true;
            }
        }
        if(this.game.getLive()!=null){
            if(this.game.getLive()==false&&success){
                this.user.setBalance(this.user.getBalance()+ (this.sum* this.ratio));

            }
        }

        this.success=success;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Game getGame() {
        return game;
    }

    public void setGame(Game game) {
        this.game = game;
    }

    public Team getTeamIsWin() {
        return teamIsWin;
    }

    public void setTeamIsWin(Team teamIsWin) {
        this.teamIsWin = teamIsWin;
    }

    public int getTeamAScore() {
        return teamAScore;
    }

    public void setTeamAScore(int teamAScore) {
        this.teamAScore = teamAScore;
    }

    public int getTeamBScore() {
        return teamBScore;
    }

    public void setTeamBScore(int teamBScore) {
        this.teamBScore = teamBScore;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }
    public boolean isDouble() {
        return isDouble;
    }

    public void setDouble(boolean aDouble) {
        isDouble = aDouble;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
