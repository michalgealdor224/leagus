package com.ashcollege.utils;

import java.util.Random;

public enum Weather {
    SUNNY,
    CLEAR,
    RAINY;

    public static double powerFrom(Weather weather){
        switch (weather) {
            case CLEAR:
                return 1.2;
            case SUNNY:
                return 1.0;
            case RAINY:
                return 0.8;
            default:
                return 1d;
        }
    }

    public static Weather getRandomWeather() {
        Weather[] weathers = Weather.values();
        int randomIndex = new Random().nextInt(weathers.length);
        return weathers[randomIndex];
    }
}
