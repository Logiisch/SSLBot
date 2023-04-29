package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import org.apache.commons.collections4.BidiMap;
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

        int from = 1;
        int to = 15;

        OptionMapping om = event.getOption("range");
        if (om!=null) {
            String range = om.getAsString().strip();
            String[] split = range.split("-");
            if (split.length !=2) {
                event.reply("Error: Use the range parameter as ´from-to´, for example: `5-10` or `1-30`!").setEphemeral(true).queue();
                return;
            }
            try {
                from = Integer.parseInt(split[0]);
                to = Integer.parseInt(split[1]);
            } catch (Exception e) {
                event.reply("Error: Use the range parameter as ´from-to´, for example: `5-10` or `1-30`!").setEphemeral(true).queue();
                return;
            }
            if (from>to) {
                int cache = to;
                to = from;
                from = cache;
            }
            if (from < 1) {
                event.reply("Error: No value can be lower than 1!").setEphemeral(true).queue();
                return;
            }

            int size = to-from+1;
            if (size>STATIC.MAX_REQUEST_SIZE) {
                event.reply("Error: Requests are limited to "+STATIC.MAX_REQUEST_SIZE+" placings!").setEphemeral(true).queue();
                return;
            }

        }

        event.deferReply().queue();

        List<LeaderboardEntry> lb;


        try {
           lb = new ArrayList<>(SteamConnector.getTopPlayers(from, to));
        } catch (Exception e) {
            event.reply("During execution, an error occured. Please check logs!").queue();
            e.printStackTrace();
            return;
        }


        event.getHook().sendMessage(STATIC.formatLeaderboard(lb)).queue();



    }
}
