package core;


import commands.*;
import listeners.readyListener;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import util.STATIC;
import webserver.WebThread;

import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static JDABuilder builder = JDABuilder.create(new ArrayList<>());
    public static void main(String[] args) {
        ArrayList<GatewayIntent> gis = new ArrayList<>(Arrays.asList(GatewayIntent.values()));
        builder = JDABuilder.create(gis);
        builder.setToken(STATIC.getToken());
        builder.setAutoReconnect(true);
        builder.setStatus(OnlineStatus.ONLINE);
        String Version = "v 1.0";


        builder.setActivity(Activity.playing("Shell Shock Live"));
        System.out.println("Starte auf " + Version + " ...");
        addListeners();
        //readInStartValues();

        try {
            builder.build();
            startThreads();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void addListeners() {
        //Listeners
        builder.addEventListeners(new readyListener());


        //Commands
        builder.addEventListeners(new cmdTop());
        builder.addEventListeners(new cmdInvite());
        builder.addEventListeners(new cmdGithub());
        builder.addEventListeners(new cmdLink());
        builder.addEventListeners(new cmdUnlink());
        builder.addEventListeners(new cmdRank());
        builder.addEventListeners(new cmdServerLb());
        builder.addEventListeners(new cmdSurrounding());
        builder.addEventListeners(new cmdDisplaystyle());

    }

    private static void startThreads() {
        Thread webserver = new Thread(new WebThread());
        webserver.start();
    }


}