package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import util.DiscordFormatter;

import java.awt.*;

public class cmdDisplaystyle extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("displaystyle")) return;

        Guild guild = event.getGuild();
        if (guild==null) {
            event.replyEmbeds(DiscordFormatter.error("This command can only be used on a server!")).setEphemeral(true).queue();
            return;
        }
        DiscordFormatter.Displaystyle current = DiscordFormatter.getGuildStype(guild.getId());

        String currentDS = switch (current) {

            case EMBEDS -> "Embeds";
            case CODEBLOCK -> "Code Block";
            case PLAIN -> "Plain Text";
        };

        EmbedBuilder eb = new EmbedBuilder().setTitle("Choose display style").setAuthor(guild.getName()).addField("Current Style",currentDS,true).setColor(Color.gray);

        Button embeds = Button.primary("displaystyle-embeds","Embeds");
        Button codeblock = Button.primary("displaystyle-codeblock","Code Block");
        Button plain = Button.primary("displaystyle-plain","Plain Text");

        event.replyEmbeds(eb.build()).addActionRow(
                (current.equals(DiscordFormatter.Displaystyle.EMBEDS)?embeds.asDisabled():embeds.asEnabled()),
                (current.equals(DiscordFormatter.Displaystyle.CODEBLOCK)?codeblock.asDisabled():codeblock.asEnabled()),
                (current.equals(DiscordFormatter.Displaystyle.PLAIN)?plain.asDisabled():plain.asEnabled())

        ).setEphemeral(true).queue();

    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String btnName = event.getButton().getId();
        assert btnName!=null;
        Guild guild = event.getGuild();
        assert guild !=null;
        if (!btnName.startsWith("displaystyle")) return;
        String newstypest = btnName.split("-")[1];
        DiscordFormatter.Displaystyle newds = switch (newstypest){
            case "embeds" -> DiscordFormatter.Displaystyle.EMBEDS;
            case "codeblock" -> DiscordFormatter.Displaystyle.CODEBLOCK;
            case "plain" -> DiscordFormatter.Displaystyle.PLAIN;
            default -> DiscordFormatter.DEFAULT_DS;
        };
        if (!newds.equals(DiscordFormatter.DEFAULT_DS)) DiscordFormatter.setDisplaystyle(guild.getId(),newds);

        String currentDS = switch (newds) {

            case EMBEDS -> "Embeds";
            case CODEBLOCK -> "Code Block";
            case PLAIN -> "Plain Text";
        };

        EmbedBuilder eb = new EmbedBuilder().setTitle("Choose display style").setAuthor(guild.getName()).addField("Current Style",currentDS,true).setColor(Color.gray);

        Button embeds = Button.primary("displaystyle-embeds","Embeds");
        Button codeblock = Button.primary("displaystyle-codeblock","Code Block");
        Button plain = Button.primary("displaystyle-plain","Plain Text");

        event.editMessageEmbeds(eb.build()).setActionRow(
                (newds.equals(DiscordFormatter.Displaystyle.EMBEDS)?embeds.asDisabled():embeds.asEnabled()),
                (newds.equals(DiscordFormatter.Displaystyle.CODEBLOCK)?codeblock.asDisabled():codeblock.asEnabled()),
                (newds.equals(DiscordFormatter.Displaystyle.PLAIN)?plain.asDisabled():plain.asEnabled())

        ).queue();


    }
}
