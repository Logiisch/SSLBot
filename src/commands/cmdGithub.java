package commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import util.STATIC;

public class cmdGithub extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("github")) return;
        event.reply("You can find the code of the bot und report issues here:").setEphemeral(true).addActionRow(
                Button.link(STATIC.GITHUB_LINK,"Github")
        ).queue();
    }
}
