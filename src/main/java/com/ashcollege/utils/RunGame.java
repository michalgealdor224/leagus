package com.ashcollege.utils;

import com.ashcollege.entities.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

public class RunGame  implements Runnable{
    private CountDownLatch latch;
    private Game game;
    private DbUtils dbUtils;

    private ScoreGenerator scoreGenerator;

    public RunGame(CountDownLatch latch, Game game, DbUtils dbUtils, ScoreGenerator scoreGenerator) {
        this.latch = latch;
        this.game = game;
        this.dbUtils = dbUtils;
        this.scoreGenerator = scoreGenerator;
    }

    @Override
    public void run() {
        game.setLive(true);
        dbUtils.updateGame(game);
        scoreGenerator.calculate(game);
        game.setLive(false);
        dbUtils.updateGame(game);
        latch.countDown();
    }
}

