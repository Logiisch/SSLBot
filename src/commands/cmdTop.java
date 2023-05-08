package commands;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;
import util.*;

import java.util.ArrayList;
import java.util.List;

public class cmdTop extends ListenerAdapter {
    private static final int PAGE_SIZE = 10;

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommandInteraction sce = event.getInteraction();
        if (!sce.getName().equalsIgnoreCase("top")) return;

        int page = 1;

        OptionMapping om = event.getOption("page");
        if (om!=null) {
            int pageNum = om.getAsInt();

            if (pageNum<1) {
                event.replyEmbeds(DiscordFormatter.error("Page number cant be smaller than 1!")).setEphemeral(true).queue();
                return;
            }
            page = pageNum;


        }

        int from = (page-1)*PAGE_SIZE+1;
        int to = page*PAGE_SIZE;

        event.deferReply().queue();

        List<LeaderboardEntry> lb;


        try {
           lb = new ArrayList<>(SteamConnector.getTopPlayers(from, to));
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(DiscordFormatter.error("During execution, an error occured. Please check logs!")).queue();
            e.printStackTrace();
            return;
        }

        Button left = Button.primary("top-p"+(page-1), Emoji.fromUnicode("U+2B05"));

        DiscordFormatter.sendLeaderboardAuto(lb,event.getHook(),event.getGuild(),"ShellShock Live XP Leaderboard Page 1").setActionRow(
                (page==1?left.asDisabled():left),Button.primary("top-p"+(page+1), Emoji.fromUnicode("U+27A1")),Button.secondary("top-close",Emoji.fromUnicode("U+274C"))
        ).queue();

       /* event.getHook().sendMessage(DiscordFormatter.formatLeaderboardCodeBlock(lb,"ShellShock Live XP Leaderboard")).addActionRow(
                (page==1?left.asDisabled():left),Button.primary("top-p"+(page+1), Emoji.fromUnicode("U+27A1")),Button.secondary("top-close",Emoji.fromUnicode("U+274C"))
        ).queue();*/


        /*event.getHook().sendMessageEmbeds(page==1? DiscordFormatter.formatLeaderboardEmbeds(lb,true):DiscordFormatter.formatLeaderboardEmbeds(lb)).addActionRow(
                (page==1?left.asDisabled():left),Button.primary("top-p"+(page+1), Emoji.fromUnicode("U+27A1")),Button.secondary("top-close",Emoji.fromUnicode("U+274C"))
        ).queue();*/



    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        String btnName = event.getButton().getId();
        assert btnName!=null;
        if (!btnName.startsWith("top")) return;

        Message msg = event.getMessage();

        if (InteractionManager.hasMessage(msg.getId())) {
            if (!InteractionManager.getMessageOwner(msg.getId()).equalsIgnoreCase(event.getUser().getId())) {
                event.replyEmbeds(DiscordFormatter.error("You dont have Permission to do that!")).setEphemeral(true).queue();
                return;
            }
        }

        if (btnName.equalsIgnoreCase("top-close")) {
            event.getMessage().delete().queue();
            InteractionManager.removeMessage(msg.getId());
            return;
        }
        int page = Integer.parseInt(btnName.replace("top-p",""));

        int from = (page-1)*PAGE_SIZE+1;
        int to = page*PAGE_SIZE;

        event.deferEdit().queue();

        List<LeaderboardEntry> lb;


        try {
            lb = new ArrayList<>(SteamConnector.getTopPlayers(from, to));
        } catch (Exception e) {
            event.getHook().sendMessageEmbeds(DiscordFormatter.error("During execution, an error occurred. Please check logs!")).queue();
            e.printStackTrace();
            return;
        }

        Button left = Button.primary("top-p"+(page-1), Emoji.fromUnicode("U+2B05"));

        DiscordFormatter.editLeaderboardAuto(lb,event.getHook(),event.getGuild(),"ShellShock Live Xp Leaderboard Page "+page).setActionRow(
                (page==1?left.asDisabled():left),Button.primary("top-p"+(page+1), Emoji.fromUnicode("U+27A1")),Button.secondary("top-close",Emoji.fromUnicode("U+274C"))
        ).queue();
        /*event.getMessage().editMessage(DiscordFormatter.formatLeaderboardCodeBlock(lb,"ShellShockLive XP Leaderboard")).setActionRow(
                (page==1?left.asDisabled():left),Button.primary("top-p"+(page+1), Emoji.fromUnicode("U+27A1")),Button.secondary("top-close",Emoji.fromUnicode("U+274C"))
        ).queue();*/

        /*event.getMessage().editMessageEmbeds(page==1?DiscordFormatter.formatLeaderboardEmbeds(lb,true):DiscordFormatter.formatLeaderboardEmbeds(lb)).setActionRow(
                (page==1?left.asDisabled():left),Button.primary("top-p"+(page+1), Emoji.fromUnicode("U+27A1")),Button.secondary("top-close",Emoji.fromUnicode("U+274C"))
        ).queue();*/


    }
}
