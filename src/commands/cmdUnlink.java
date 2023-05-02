package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import util.DiscordFormatter;
import util.SteamConnector;

public class cmdUnlink extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("unlink")) return;

        String steamid = SteamConnector.getSteamID(event.getUser().getId());
        if (steamid.length()==0) {
            event.replyEmbeds(DiscordFormatter.error("Your account is currently not linked. Please use `/link` to link your accounts!")).setEphemeral(true).queue();
            return;
        }
        event.reply("Do you really want to unlink yor accounts?").setEphemeral(true).addActionRow(
                Button.primary("unlink-no","No"),
                Button.danger("unlink-yes","Yes")
        ).queue();
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String buttonID = event.getComponentId();
        if (buttonID.equalsIgnoreCase("unlink-no")) {
            event.editMessage("Aborted!").setActionRow(
                    Button.primary("unlink-no","No").asDisabled(),
                    Button.danger("unlink-yes","Yes").asDisabled()
            ).closeResources().queue();
        }
        if (buttonID.equalsIgnoreCase("unlink-yes")) {
            SteamConnector.unlink(event.getUser().getId());
            event.editMessage("You unlinked your account!").setActionRow(
                    Button.primary("unlink-no","No").asDisabled(),
                    Button.danger("unlink-yes","Yes").asDisabled()
            ).closeResources().queue();
        }
    }
}
