package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import util.LeaderboardEntry;
import util.STATIC;
import util.SteamConnector;

import java.util.ArrayList;
import java.util.List;

public class cmdTop extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("top")) return;

        List<LeaderboardEntry> lb;


        try {
           lb = new ArrayList<>(SteamConnector.getTopPlayers(1, 15));
        } catch (Exception e) {
            event.reply("During execution, an error occured. Please check logs!").queue();
            e.printStackTrace();
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("Rank, ID, XP").append("\n");
        for (LeaderboardEntry le:lb) {
            sb
                    .append("**")
                    .append(le.getRank())
                    .append("** __")
                    .append(le.getSteamID())
                    .append("__ ")
                    .append(STATIC.formatXp(le.getXp()))
                    .append("xp")
                    .append("\n");
        }

        event.reply(sb.toString()).queue();


    }
}
