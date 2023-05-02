package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.jetbrains.annotations.NotNull;
import util.LeaderboardEntry;
import util.STATIC;
import util.SteamConnector;

import java.awt.*;
import java.util.List;

public class cmdSurrounding extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("surrounding")) return;

        String steamID = SteamConnector.getSteamID(event.getUser().getId());
        if (steamID.length()==0) {
            event.reply("Error: Your account isn't linked. Plese use `/link`!").setEphemeral(true).queue();
            return;
        }

        event.deferReply().setEphemeral(true).queue();

        List<LeaderboardEntry> friendsLB;
        try {
            friendsLB = SteamConnector.getFriendListOfPlayer(steamID);
        } catch (Exception e) {
            event.getHook().sendMessage("Error: There was a problem handling your Request. Please see the console.").setEphemeral(true).queue();
            e.printStackTrace();
            return;
        }
        int rank = -1;

        for (LeaderboardEntry le:friendsLB) {
            if (le.getSteamID().equalsIgnoreCase(steamID)) rank = le.getRank();
        }
        if (rank<1) {
            event.getHook().sendMessage("Error: There was a problem handling your Request. Please see the console.").setEphemeral(true).queue();
            System.err.println("Error: Couldn't find the users rank in his own friend list. SteamID: "+steamID);
            return;
        }
        int from = Math.max(rank-4,1);
        int to = rank+5;
        List<LeaderboardEntry> globalList;
        try {
            globalList = SteamConnector.getTopPlayers(from,to);
        } catch (Exception e) {
            event.getHook().sendMessage("Error: There was a problem handling your Request. Please see the console.").setEphemeral(true).queue();
            e.printStackTrace();
            return;
        }

        event.getHook().sendMessageEmbeds(STATIC.formatLeaderboardEmbeds(globalList,steamID, Color.RED)).setEphemeral(true).queue();

    }
}
