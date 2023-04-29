package commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import util.LeaderboardEntry;
import util.STATIC;
import util.SteamConnector;

import java.util.ArrayList;
import java.util.List;

public class cmdServerLb extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("serverlb")) return;

        Guild guild = sce.getGuild();
        if (guild==null) {
            event.reply("Error: This Command only works on servers!").setEphemeral(true).queue();
            return;
        }
        event.deferReply().queue();

        List<String> allIDs = new ArrayList<>();

        for (Member mem:sce.getGuild().getMembers()) {
            String discordID = mem.getUser().getId();
            String steamID = SteamConnector.getSteamID(discordID);
            if (steamID.length()>0) allIDs.add(steamID);
        }
        List<LeaderboardEntry> entries = new ArrayList<>();
        while (!allIDs.isEmpty()) {
            String steamID = allIDs.get(0);
            allIDs.remove(0);
            List<LeaderboardEntry> les;
            try {
                les = SteamConnector.getFriendListOfPlayer(steamID);
            } catch (Exception e) {
                e.printStackTrace();
                event.reply("Error: Something went wrong while collecting Data. See console.").setEphemeral(true).queue();
                return;
            }
            for (LeaderboardEntry le:les) {
                if (allIDs.contains(le.getSteamID())) {
                    allIDs.remove(le.getSteamID());
                    entries.add(le);
                }
                if (le.getSteamID().equalsIgnoreCase(steamID)) entries.add(le);
            }
        }
        entries = sortLeaderboard(entries);



        event.getHook().sendMessageEmbeds(STATIC.formatLeaderboardEmbeds(entries)).queue();

    }

    private static List<LeaderboardEntry> sortLeaderboard(List<LeaderboardEntry> entries) {
        List<LeaderboardEntry> out = new ArrayList<>();
        out.add(entries.get(0));
        entries.remove(0);
        for (LeaderboardEntry le:entries) {
            int pos = 0;

            while (true) {
                if (pos==out.size()) {
                    out.add(le);
                    break;
                }
                LeaderboardEntry comp = out.get(pos);
                if (comp.getRank()>le.getRank()) {
                    out.add(pos,le);
                    break;
                }
                pos++;
            }
        }

        return out;
    }
}
