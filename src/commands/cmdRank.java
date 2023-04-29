package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import util.LeaderboardEntry;
import util.STATIC;
import util.SteamConnector;

import java.awt.*;
import java.util.List;

public class cmdRank extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("rank")) return;
        OptionMapping omSteamID = sce.getOption("steamid");
        OptionMapping omPlayer = sce.getOption("player");

        if (omSteamID!=null && omPlayer !=null) {
            event.reply("Error: Please enter *either* a steam ID *or* mention a player, not both!").setEphemeral(true).queue();
            return;
        }
        String steamID ="";
        if (omSteamID!=null) {
            steamID = omSteamID.getAsString();
            if(!SteamConnector.isValidSteamID(steamID)) {
                event.reply("Error: Please provide a valid 17 digit Steam ID!").setEphemeral(true).queue();
                return;
            }
        }
        if (omSteamID == null & omPlayer == null) {
            steamID = SteamConnector.getSteamID(event.getUser().getId());
            if(steamID.length()==0) {
                event.reply("Error: You haven't linked your steam account. Please use `/link`!").setEphemeral(true).queue();
                return;
            }
        }
        if (omPlayer !=null) {
            String discordID = omPlayer.getAsUser().getId();
            steamID = SteamConnector.getSteamID(discordID);
            if (steamID.length()==0) {
                event.reply("Error: This Player hasn't linked his Account. Ask him/her to use `/link`!").setEphemeral(true).queue();
                return;
            }
        }


        List<LeaderboardEntry> ls;
        try {
            ls = SteamConnector.getFriendListOfPlayer(steamID);
        } catch (Exception e) {
            event.reply("Error: Something went wrong. Please check the console!").setEphemeral(true).queue();
            return;
        }
        String name = SteamConnector.getName(steamID);
        for (LeaderboardEntry le:ls) {
            if (le.getSteamID().equalsIgnoreCase(steamID)) {
                EmbedBuilder eb = new EmbedBuilder().setColor(le.getRank()<=100?new Color(218,165,32):Color.green);
                eb.setTitle(name);
                eb.addField("Current XP", STATIC.formatXp(le.getXp()) +" XP",false).addField("Current Rank", String.valueOf(le.getRank()),false);
                event.replyEmbeds(eb.build()).queue();
                return;
            }
        }
        event.reply("Error: Couldn't find "+name+" in the leaderboard. Does he play Shell Shock Live at all?").queue();

    }
}