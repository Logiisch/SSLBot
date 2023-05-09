package commands;

import listeners.readyListener;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class cmdLink extends ListenerAdapter {

    //<loginid,userid>
    private static final Map<String, String> loginids = new HashMap<>();

    public static String addCode(String userID) {

        if (loginids.containsValue(userID)) {
            for (String key:loginids.keySet()) {
                if (loginids.get(key).equalsIgnoreCase(userID)) return key;
            }
        }

        String code = generateCode();

        while (loginids.containsKey(code)) code = generateCode();

        loginids.put(code,userID);
        return code;

    }

    private static String generateCode() {
        double rnd =Math.random();
        long code = (long) Math.floor(rnd*100000000);
        StringBuilder out = new StringBuilder(String.valueOf(code));
        while (out.length()<8) out.insert(0, "0");

        return out.toString();

    }

    public static String getUserID(String loginid) {
        if (loginids.containsKey(loginid)) {
            String out = loginids.get(loginid);
            loginids.remove(loginid);
            return out;
        }
        return "";
    }
    private static final String LOGIN_LINK = "https://sslbot.logii.de/login";

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

            String fullLink = LOGIN_LINK + "?loginid="+addCode(event.getUser().getId());

            sce.reply("Please log in through steam here to link your accounts").setEphemeral(true).addActionRow(
                    Button.link(fullLink,"Login")
            ).queue();
            return;
        }

        String code = codeOM.getAsString();
        String userid;
        try {
            userid = CodeHandler.getAndRemove(code);
        } catch (Exception e) {
            String fullLink = LOGIN_LINK + "?loginid="+addCode(event.getUser().getId());
            event.replyEmbeds(DiscordFormatter.error("This code isnt valid. Please check again. If you dont have a code yet, please use this link!")).setEphemeral(true).addActionRow(
                    Button.link(fullLink,"Login")
            ).queue();
            return;
        }

        linkUser(userid,event.getUser().getId());



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

    public static void linkUser(String steamID, String discordID) {
        String username = SteamConnector.getName(steamID);
        String link = DiscordFormatter.getURL(steamID);
        String avatar = SteamConnector.getAvatar(steamID);
        EmbedBuilder eb = new EmbedBuilder()
                .setColor(Color.BLACK)
                .setAuthor("ID: "+steamID)
                .setTitle(username,link)
                .setThumbnail(avatar)
                .setDescription("Do you want to link this account?");


        try {
            Objects.requireNonNull(readyListener.jda.getUserById(discordID)).openPrivateChannel().complete().sendMessageEmbeds(eb.build()).addActionRow(
                    Button.primary("link-yes-"+steamID,"Yes"),
                    Button.danger("link-no","No")
            ).queue();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
