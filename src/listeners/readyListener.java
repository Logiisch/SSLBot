package listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import util.STATIC;
import util.SteamConnector;

public class readyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        //registerLocalCommands(event.getJDA());
        //registerGlobalCommands(event.getJDA());
        SteamConnector.loadLinks();
    }

    private static void registerGlobalCommands(JDA jda) {

    }

    private void registerLocalCommands(JDA jda) {
        Guild guild =jda.getGuildById(STATIC.TESTSERVER_ID);
        if (guild ==null) {
            System.out.println("Fehler beim Registieren der Commands: Guild == null");
            return;
        }
        guild
                .upsertCommand("top","Show the top players in the leaderboard")
                .addOption(OptionType.INTEGER,"page","page nbr. you want to see",false)
                .queue();

        guild
                .upsertCommand("invite","Sends you the invite link of the bot")
                .queue();
        guild
                .upsertCommand("github","Get the Github link, where you can find the code of the bot")
                .queue();
        guild
                .upsertCommand("link","Link your discord account to your Steam Account")
                .addOption(OptionType.STRING,"code","the code from the website",false)
                .queue();
        guild
                .upsertCommand("unlink","Unlink your Steam and Discord Account")
                .queue();
        guild
                .upsertCommand("rank","Shows the XP and rank from you or another player")
                .addOption(OptionType.STRING,"steamid","the steam id of the player you want to know the rank of",false)
                .addOption(OptionType.USER,"player","the player you want to know the rank of",false)
                .queue();
        guild
                .upsertCommand("serverlb","Shows the leaderboard with all users of the server")
                .setGuildOnly(true)
                .queue();
        guild
                .upsertCommand("surrounding","Show what players are directly in front or behind you on the leaderboard")
                .queue();
    }
}
