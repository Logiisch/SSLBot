package listeners;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.STATIC;

public class readyListener extends ListenerAdapter {

    @Override
    public void onReady(ReadyEvent event) {
        registerCommands(event.getJDA());
    }

    private void registerCommands(JDA jda) {
        Guild guild =jda.getGuildById(STATIC.TESTSERVER_ID);
        if (guild ==null) {
            System.out.println("Fehler beim Registieren der Commands: Guild == null");
            return;
        }
        guild
                .upsertCommand("top","Show the top players in the leaderboard")
                .queue();
    }
}
