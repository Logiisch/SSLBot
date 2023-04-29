package core;


import commands.cmdGithub;
import commands.cmdInvite;
import commands.cmdTop;
import listeners.readyListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import util.STATIC;

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
            JDA jda = builder.build();
            startThreads(jda);
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

    }

    private static void startThreads(JDA jda) {

    }
}