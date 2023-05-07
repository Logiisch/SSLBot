package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import util.DiscordFormatter;
import util.SteamConnector;
import webserver.CodeHandler;

import java.awt.*;

public class cmdLink extends ListenerAdapter {
    private static final String LOGIN_LINK = "https://sslbot.logii.de/";

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("link")) return;

        OptionMapping codeOM = sce.getOption("code");

        String teststeamid = SteamConnector.getSteamID(event.getUser().getId());
        if (teststeamid.length()>0) {
            event.replyEmbeds(DiscordFormatter.error("Your Account is already linked. If you want to link to a different account, please `/unlink` first!")).setEphemeral(true).queue();
            return;
        }

        if(codeOM==null) {
            sce.reply("Please log in through steam here to link your accounts").setEphemeral(true).addActionRow(
                    Button.link(LOGIN_LINK,"Login")
            ).queue();
            return;
        }

        String code = codeOM.getAsString();
        String userid;
        try {
            userid = CodeHandler.getAndRemove(code);
        } catch (Exception e) {
            event.replyEmbeds(DiscordFormatter.error("This code isnt valid. Please check again. If you dont have a code yet, please use this link!")).setEphemeral(true).addActionRow(
                    Button.link(LOGIN_LINK,"Login")
            ).queue();
            return;
        }
        String username = SteamConnector.getName(userid);
        String link = DiscordFormatter.getURL(userid);
        String avatar = SteamConnector.getAvatar(userid);
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.BLACK)
                .setAuthor("ID: "+userid)
                .setTitle(username,link)
                .setThumbnail(avatar)
                .setDescription("Do you want to link this account?");

        event.replyEmbeds(eb.build()).addActionRow(
                Button.primary("link-yes-"+userid,"Yes"),
                Button.danger("link-no","No")
        ).setEphemeral(true).queue();


    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String btnName = event.getButton().getId();
        assert btnName!=null;
        if (!btnName.startsWith("link")) return;
        Message msg = event.getMessage();
        if (btnName.equalsIgnoreCase("link-no")) {
            EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0)).setColor(Color.red).setDescription("Aborted!");
            event.editMessageEmbeds(eb.build()).setActionRow(
                    Button.primary("link-yes","Yes").asDisabled(),
                    Button.danger("link-no","No").asDisabled()
            ).queue();
            return;
        }
        String userID = btnName.split("-")[2];
        SteamConnector.addLinking(event.getUser().getId(),userID);
        EmbedBuilder eb = new EmbedBuilder(msg.getEmbeds().get(0)).setColor(Color.green).setDescription("You successfully linked your Steam Account!");
        event.editMessageEmbeds(eb.build()).setActionRow(
                Button.primary("link-yes","Yes").asDisabled(),
                Button.danger("link-no","No").asDisabled()
        ).queue();
    }
}
