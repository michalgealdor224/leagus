package com.ashcollege.controllers;

import com.ashcollege.entities.Game;
import com.ashcollege.utils.DbUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class LiveUpdateController {
    @Autowired
    private DbUtils dbUtils;
    private List<SseEmitter> clients = new ArrayList<>();

    @RequestMapping(value = "/start-streaming")
    public SseEmitter streaming() {
        SseEmitter emitter = new SseEmitter();
        clients.add(emitter);
        return emitter;
    }
/*@PostConstruct
    public void init(){
        new Thread(()->{
            while(true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                for (SseEmitter sse :clients)
                {
                    try {
                        sse.send(true);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }).start();
    }*/

    public void sendGamesIsUpdate(){
        for (SseEmitter sse :clients)
        {
            try {
                sse.send(true);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
