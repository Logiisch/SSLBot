package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import util.DiscordFormatter;
import util.LeaderboardEntry;
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
            event.replyEmbeds(DiscordFormatter.error("Please enter either a steam ID or mention a player, not both!")).setEphemeral(true).queue();
            return;
        }
        String steamID ="";
        if (omSteamID!=null) {
            steamID = omSteamID.getAsString();
            if(!SteamConnector.isValidSteamID(steamID)) {
                event.replyEmbeds(DiscordFormatter.error("Please provide a valid 17 digit Steam ID!")).setEphemeral(true).queue();
                return;
            }
        }
        if (omSteamID == null & omPlayer == null) {
            steamID = SteamConnector.getSteamID(event.getUser().getId());
            if(steamID.length()==0) {
                event.replyEmbeds(DiscordFormatter.error("You haven't linked your steam account. Please use `/link`!")).setEphemeral(true).queue();
                return;
            }
        }
        if (omPlayer !=null) {
            String discordID = omPlayer.getAsUser().getId();
            steamID = SteamConnector.getSteamID(discordID);
            if (steamID.length()==0) {
                event.replyEmbeds(DiscordFormatter.error("This Player hasn't linked his Account. Ask him/her to use `/link`!")).setEphemeral(true).queue();
                return;
            }
        }


        List<LeaderboardEntry> ls;
        try {
            ls = SteamConnector.getFriendListOfPlayer(steamID);
        } catch (Exception e) {
            event.replyEmbeds(DiscordFormatter.error("Something went wrong. Please check the console!")).setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();
        String name = SteamConnector.getName(steamID);
        for (LeaderboardEntry le:ls) {
            if (le.getSteamID().equalsIgnoreCase(steamID)) {
                EmbedBuilder eb = new EmbedBuilder().setColor(le.getRank()<=100?new Color(218,165,32):Color.green);
                eb.setTitle(name);
                eb.addField("Current XP", DiscordFormatter.formatXp(le.getXp()) +" XP",true);
                eb.addField("Current Rank", DiscordFormatter.formatXp(le.getRank()),true);
                int hoursSSL = SteamConnector.getHours(steamID);
                if (hoursSSL>-1) {
                    eb.addField("Hours played",DiscordFormatter.formatXp(hoursSSL)+" h",true);
                    int xpPerH = Math.floorDiv(le.getXp(),hoursSSL);
                    eb.addField("Average XP/h",DiscordFormatter.formatXp(xpPerH)+" XP/h",true);
                }
                String avatar = SteamConnector.getAvatar(steamID);
                if (!avatar.equalsIgnoreCase("")) {
                    eb.setThumbnail(avatar);
                }
                event.getHook().sendMessageEmbeds(eb.build()).queue();
                return;
            }
        }
        event.getHook().sendMessageEmbeds(DiscordFormatter.error("Couldn't find "+name+" on the leaderboard. Does he/she play Shell Shock Live at all?")).queue();

    }
}
